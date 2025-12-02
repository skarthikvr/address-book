package com.demo.addressbook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;

import com.demo.addressbook.dto.ContactDto;
import com.demo.addressbook.entity.AddressBook;
import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.exception.InputValidationException;
import com.demo.addressbook.repository.AddressBookRepository;
import com.demo.addressbook.repository.ContactRepository;

class AddressBookServiceTest {

	@Mock
	AddressBookRepository addressBookRepository;

	@Mock
	ContactRepository contactRepository;

	AddressBookService addressBookService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		addressBookService = new AddressBookService(addressBookRepository, contactRepository);
	}

	@Test
	void addAddressBook_nullName_throws() {
		assertThrows(InputValidationException.class, () -> addressBookService.addAddressBook(null));
	}

	@Test
	void addAddressBook_validName_succeeds() {
		AddressBook saved = new AddressBook("MyBook");
		when(addressBookRepository.save(any())).thenReturn(saved);

		AddressBook result = addressBookService.addAddressBook("MyBook");
		assertEquals("MyBook", result.getName());
	}

	@Test
	void findAllContacts_missingAddressBook_throws() {
		when(addressBookRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(InputValidationException.class, () -> addressBookService.findAllContactsByAddressBookId("1"));
	}

	@Test
	void findAllContacts_present_returnsSet() {
		AddressBook ab = new AddressBook("B");
		Contact c = new Contact("A", "B", "123456789");
		ab.getContacts().add(c);
		when(addressBookRepository.findById(1L)).thenReturn(Optional.of(ab));

		Set<Contact> set = addressBookService.findAllContactsByAddressBookId("1");
		assertEquals(1, set.size());
	}

	@Test
	void addContact_nullAddressBookId_throws() {
		ContactDto dto = new ContactDto(null, "A", "B", "Org", "a@b.com", "1234");
		assertThrows(InputValidationException.class, () -> addressBookService.addContact(null, dto));
	}

	@Test
	@DisplayName("Adding a contact to a non-existent Address Book throws InputValidationException")
	void addContact_invalidAddressBookId_throws() {
		ContactDto dto = new ContactDto(null, "A", "B", "Org", "a@b.com", "1234");
//        Contact contact = new Contact();
//        BeanUtils.copyProperties(dto, contact);
		when(addressBookRepository.findById(any())).thenReturn(Optional.empty());
		when(contactRepository.findAll()).thenReturn(Collections.emptyList());
		assertThrows(InputValidationException.class, () -> addressBookService.addContact("100", dto));
	}

	@Test
	void addContact_duplicate_throwsDataIntegrityViolation() {
		// make contactRepository return an existing contact with same first/last name
		Contact existing = new Contact("Jane", "Smith", "987654321");
		when(contactRepository.findAll()).thenReturn(java.util.List.of(existing));

		ContactDto dto = new ContactDto(null, "Jane", "Smith", "Org", "jane@x.com", "987654321");
		// even if address book exists, service checks duplicate first
		when(addressBookRepository.findById(1L)).thenReturn(Optional.of(new AddressBook("AB")));

		assertThrows(DataIntegrityViolationException.class, () -> addressBookService.addContact("1", dto));
	}

	@Test
	void addExistingContact_success_returnsMessage() {
		Contact existing = new Contact("A", "B", "123");
		when(contactRepository.findById(2L)).thenReturn(Optional.of(existing));
		AddressBook ab = new AddressBook("AB");
		when(addressBookRepository.findById(1L)).thenReturn(Optional.of(ab));
		when(addressBookRepository.save(any())).thenReturn(ab);

		String res = addressBookService.addExistingContact("1", "2");
		assertEquals("Contact added to Address Book", res);
	}

	@Test
	void addExistingContact_contactNotFound_throws() {
		when(contactRepository.findById(2L)).thenReturn(Optional.empty());
		assertThrows(InputValidationException.class, () -> addressBookService.addExistingContact("1", "2"));
	}

	@Test
	void addExistingContact_invalidIds_throws() {
		assertThrows(InputValidationException.class, () -> addressBookService.addExistingContact(null, "1"));
		assertThrows(InputValidationException.class, () -> addressBookService.addExistingContact("1", null));
	}

	@Test
	void removeContact_invalidAddressBook_throws() {
		ContactDto dto = new ContactDto(1L, "A", "B", "Org", "a@b.com", "1234");
		when(addressBookRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(InputValidationException.class, () -> addressBookService.removeContactByAddressBookId("1", dto));
	}

	@Test
	void findAllContacts_invalidIdString_throwsNumberFormat() {
		assertThrows(NumberFormatException.class, () -> addressBookService.findAllContactsByAddressBookId("abc"));
	}
}