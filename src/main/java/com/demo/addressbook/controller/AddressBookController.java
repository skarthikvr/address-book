package com.demo.addressbook.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.addressbook.dto.ContactDto;
import com.demo.addressbook.entity.AddressBook;
import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.service.AddressBookService;
import com.demo.addressbook.service.ContactService;
import com.demo.addressbook.service.InitialSetupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;


@Validated
@RestController
@RequestMapping("/v1/address-book")
public class AddressBookController {

	InitialSetupService initialSetupService;
	AddressBookService addressBookService;
	ContactService contactService;
	
	public AddressBookController(InitialSetupService initialSetupService, AddressBookService addressBookService, ContactService contactService) {
//		super();
		this.initialSetupService = initialSetupService;
		this.addressBookService = addressBookService;
		this.contactService = contactService;
	}
	
	// Add Address Book
	@Tag(name = "Add Address Book")
	@Operation(description = "Create a new address book with the given name")
	@PutMapping(value = "/add/{name}")
    public ResponseEntity<String> addAddressBooks(@Parameter(description = "Provide the Address Book name") @Valid @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Address Book Name must contain only alphanumeric characters.") @PathVariable String name) {
		AddressBook addressBook = addressBookService.addAddressBook(name);
        return new ResponseEntity<>("Address Book added:" + addressBook, HttpStatus.OK);
    }
	
	// Add Contact to Address Book
	@Tag(name = "Add Contact", description = "There are two ways to add a contact to an address book")
	@Operation(description = "Add a new contact to an Address Book")
	@PutMapping(value = "/contact/add/{addressBookId}")
    public ResponseEntity<String> addContact(@Parameter(description = "Provide the address book Id") @Valid @Pattern(regexp = "^[0-9]*$", message = "Address Book Id must be a number.") @PathVariable String addressBookId, @Valid @RequestBody ContactDto contact)  {
		String response = addressBookService.addContact(addressBookId, contact);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	// Add Existing Contact to Address Book
	@Tag(name = "Add Contact", description = "There are two ways to add a contact to an address book")
	@Operation(description = "Add an existing contact to an Address Book")
	@PutMapping(value = "/contact/addexisting/{addressBookId}~{contactId}")
    public ResponseEntity<String> addExistingContact(@Parameter(description = "Provide the address book Id") @Valid @Pattern(regexp = "^[0-9]*$", message = "Address Book Id must be a number.") @PathVariable String addressBookId, @Parameter(description = "Provide the contact Id") @Valid @Pattern(regexp = "^[0-9]*$", message = "Contact Id must be a number.") @PathVariable String contactId)  {
		String response = addressBookService.addExistingContact(addressBookId, contactId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	// Remove existing contact from Address Book
	@Tag(name = "Remove Contact")
	@Operation(description = "Remove an existing contact from an Address Book")
	@DeleteMapping(value = "/contact/remove/{addressBookId}")
    public ResponseEntity<String> removeContactFromAddressBook(@Parameter(description = "Provide the address book Id") @Valid @Pattern(regexp = "^[0-9]*$", message = "Address Book Id must be a number.") @PathVariable String addressBookId, @Parameter(description = "Provide the contact details") @Valid @RequestBody ContactDto contact) {
		String response = addressBookService.removeContactByAddressBookId(addressBookId, contact);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	//Print all contacts in an Address Book
	@Tag(name = "View All Contacts", description = "There are two ways to view contacts")
	@Operation(description = "Get all contacts for an Address Book")
	@GetMapping(value = "/contacts/{addressBookId}")
    public ResponseEntity<String> getContacts(@Parameter(description = "Provide the address book Id") @Valid @Pattern(regexp = "^[0-9]*$", message = "Address Book Id must be a number.") @PathVariable String addressBookId) {
		HttpStatus status = HttpStatus.OK;
		String response = null;
			Set<Contact> contacts = addressBookService.findAllContactsByAddressBookId(addressBookId.trim());
			response = contacts.isEmpty() ? "No contacts available for the Address Book" : "Contacts for addressBookId: " + contacts;
        return new ResponseEntity<>(response, status);
    }
	//Print all unique contacts across Address Books
	@Tag(name = "View All Contacts", description = "There are two ways to view contacts")
	@Operation(description = "Get all contacts across Address Books")
	@GetMapping(value = "/allcontacts")
    public ResponseEntity<String> getAllContacts() {
			Set<Contact> contacts = contactService.getAllUniqueContacts();
		
			String response = contacts.isEmpty() ? "No contacts found in address books." : "All Contacts: " + contacts;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@Tag(name = "Set Up Data")
	@Operation(description = "Convenience method to set up initial address book data with 5 address books and contacts")
	@PutMapping(value = "/setup")
    public ResponseEntity<String> getAddressBooks() {
		List<AddressBook> addressBooks = initialSetupService.setUpAddressBookData();
        return new ResponseEntity<>("You address Book Id:" + addressBooks, HttpStatus.OK);
    }
	
	
}