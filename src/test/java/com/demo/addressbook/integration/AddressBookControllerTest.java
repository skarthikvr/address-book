package com.demo.addressbook.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.demo.addressbook.controller.AddressBookController;
import com.demo.addressbook.entity.AddressBook;
import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.service.AddressBookService;
import com.demo.addressbook.service.ContactService;
import com.demo.addressbook.service.InitialSetupService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AddressBookController.class)
class AddressBookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AddressBookService addressBookService;

	@MockitoBean
	private ContactService contactService;

	@MockitoBean
	private InitialSetupService initialSetupService;

	@Test
	@DisplayName("When no contacts exist for an address book, a specific message is returned")
	void getContacts_noContacts_returnsNoContactsMessage() throws Exception {
		when(addressBookService.findAllContactsByAddressBookId("1")).thenReturn(Collections.emptySet());

		mockMvc.perform(get("/v1/address-book/contacts/1")).andExpect(status().isOk())
				.andExpect(content().string("No contacts available for the Address Book"));
	}

	@Test
	@DisplayName("When contacts exist for an address book, they are returned")
	void getContacts_withContacts_returnsContacts() throws Exception {
		Contact c = new Contact("John", "Doe", "Org", "john@example.com", "123456789");
		Set<Contact> set = Set.of(c);
		when(addressBookService.findAllContactsByAddressBookId("1")).thenReturn(set);

		mockMvc.perform(get("/v1/address-book/contacts/1")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Contacts for addressBookId:")))
				.andExpect(content().string(Matchers.containsString("firstName=John")))
				.andExpect(content().string(Matchers.containsString("lastName=Doe")));
	}

	@Test
	@DisplayName("Adding a new contact returns the service response")
	void addContact_returnsServiceResponse() throws Exception {
		Map<String, Object> contactJson = new HashMap<>();
		contactJson.put("contactId", null);
		contactJson.put("firstName", "Jane");
		contactJson.put("lastName", "Smith");
		contactJson.put("orgName", "Org");
		contactJson.put("email", "jane@example.com");
		contactJson.put("contactNumber", "987654321");

		String json = objectMapper.writeValueAsString(contactJson);

		when(addressBookService.addContact(eq("1"), any())).thenReturn("Contact added");

		mockMvc.perform(put("/v1/address-book/contact/add/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Contact added"));
	}

	@Test
	@DisplayName("Adding a contact with invalid input returns Bad Request")
	void addContact_invalidJson_returnsBadRequest() throws Exception {
		// missing required fields or invalid types
		Map<String, Object> bad = new HashMap<>();
		bad.put("firstName", "");
		String json = objectMapper.writeValueAsString(bad);

		mockMvc.perform(put("/v1/address-book/contact/add/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Adding an existing contact returns a service response")
	void addExistingContact_returnsServiceResponse() throws Exception {
		when(addressBookService.addExistingContact("1", "2")).thenReturn("Contact added to Address Book");

		mockMvc.perform(put("/v1/address-book/contact/addexisting/1~2")).andExpect(status().isOk())
				.andExpect(content().string("Contact added to Address Book"));
	}

	@Test
	@DisplayName("Adding an existing contact with invalid input returns Bad Request")
	void addExistingContact_invalidContactId_returnsBadRequest() throws Exception {
		mockMvc.perform(put("/v1/address-book/contact/addexisting/1~abc")).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Adding a contact with invalid address book ID returns Bad Request")
	void addContact_invalidAddressBookId_returnsBadRequest() throws Exception {
		Map<String, Object> contactJson = new HashMap<>();
		contactJson.put("firstName", "Jane");
		String json = objectMapper.writeValueAsString(contactJson);

		mockMvc.perform(put("/v1/address-book/contact/add/abc").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("When contacts exist, return them across all address books")
	void getAllContacts_returnsContacts() throws Exception {
		Contact contact1 = new Contact("Matthew", "Hayden", "+61 123 456 789");
		Contact contact2 = new Contact("Andrew", "Symonds", "+61 234 567 890");
		Contact contact3 = new Contact("Adam", "Gilchrist", "+61 345 678 901");
		Contact contact4 = new Contact("Michael", "Bevan", "+61 456 789 012");
		Contact contact5 = new Contact("Shane", "Warne", "+61 567 890 123");
		Set<Contact> contacts = Set.of(contact1, contact2, contact3, contact4, contact5);
		when(contactService.getAllUniqueContacts()).thenReturn(contacts);

		mockMvc.perform(get("/v1/address-book/allcontacts")).andExpect(status().isOk())
				.andExpect(content().string("All Contacts: " + contacts));
	}

	@Test
	@DisplayName("When no contacts exist across all address books, a specific message is returned")
	void getAllContacts_empty_returnsNoContactsFoundMessage() throws Exception {
		when(contactService.getAllUniqueContacts()).thenReturn(Collections.emptySet());

		mockMvc.perform(get("/v1/address-book/allcontacts")).andExpect(status().isOk())
				.andExpect(content().string("No contacts found in address books."));
	}

	@Test
	@DisplayName("Convenience method to set up initial address book data returns created address books")
	void setup_returnsAddressBooks() throws Exception {
		AddressBook ab = new AddressBook("MyBook");
		ab.setAddressBookId(1L);
		List<AddressBook> list = List.of(ab);
		when(initialSetupService.setUpAddressBookData()).thenReturn(list);

		mockMvc.perform(put("/v1/address-book/setup")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("You address Book Id:")))
				.andExpect(content().string(Matchers.containsString("name=MyBook")));
	}

	@Test
	@DisplayName("Getting contacts with invalid address book ID returns Bad Request")
	void getContacts_invalidId_returnsBadRequest() throws Exception {
		mockMvc.perform(get("/v1/address-book/contacts/abc")).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Removing a contact returns the service response")
	void removeContactFromAddressBook_returnsServiceResponse() throws Exception {
		Map<String, Object> contactJson = new HashMap<>();
		contactJson.put("firstName", "Jane");
		contactJson.put("lastName", "Smith");
		contactJson.put("orgName", "Org");
		contactJson.put("email", "jane@example.com");
		contactJson.put("contactNumber", "987654321");

		String json = objectMapper.writeValueAsString(contactJson);

		when(addressBookService.removeContactByAddressBookId(eq("1"), any())).thenReturn("Contact removed");

		mockMvc.perform(
				delete("/v1/address-book/contact/remove/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Contact removed"));
	}

	@Test
	@DisplayName("Removing a contact with invalid input returns Bad Request")
	void removeContactFromAddressBook_invalidId_returnsBadRequest() throws Exception {
		Map<String, Object> contactJson = new HashMap<>();
		contactJson.put("firstName", "Jane");
		String json = objectMapper.writeValueAsString(contactJson);

		mockMvc.perform(
				delete("/v1/address-book/contact/remove/abc").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
}