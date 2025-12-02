package com.demo.addressbook.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.addressbook.dto.ContactDto;
import com.demo.addressbook.entity.AddressBook;
import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.exception.InputValidationException;
import com.demo.addressbook.repository.AddressBookRepository;
import com.demo.addressbook.repository.ContactRepository;

@Service
public class AddressBookService {

	AddressBookRepository repository;
	ContactRepository contactRepository;

	public AddressBookService(AddressBookRepository repository, ContactRepository contactRepository) {
		this.repository = repository;
		this.contactRepository = contactRepository;
	}

	public AddressBook addAddressBook(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new InputValidationException("Address Book name cannot be null or empty");
		}
		AddressBook addressBook = new AddressBook(name.trim());
		return repository.save(addressBook);
	}

	@Transactional
	public String addContact(String addressBookId, ContactDto contact) {
		if (addressBookId == null || addressBookId.trim().isEmpty()) {
			throw new InputValidationException("Address Book Id is required");
		}
		if (contact == null || (contact.firstName() == null || contact.firstName().trim().isEmpty())
				|| (contact.lastName() == null || contact.lastName().trim().isEmpty())) {
			throw new InputValidationException("Contact Number is required");
		}

		// Check if contact already exists in DB
		List<Contact> contacts = contactRepository.findAll();
		Contact existingContact = null;
		if (!contacts.isEmpty()) {
			existingContact = contacts.stream().filter(c -> c.getFirstName().equals(contact.firstName().trim())
					&& c.getLastName().equals(contact.lastName().trim())).findFirst().get();
		}

		if (existingContact != null) {
			String addressbookNames = existingContact.getAddressBooks().stream().map(AddressBook::getName)
					.collect(Collectors.joining(", "));
			throw new DataIntegrityViolationException(
					"Contact with same First Name and Last Name already exists in Address Book: " + addressbookNames);
		}

		Contact contactEntity = new Contact();
		BeanUtils.copyProperties(contact, contactEntity);
		Optional<AddressBook> optionalAddressBook = repository.findById(Long.valueOf(addressBookId.trim()));
		if (optionalAddressBook.isEmpty()) {
			throw new InputValidationException("Address Book not found");
		}
		AddressBook addressBook = optionalAddressBook.get();
		addressBook.getContacts().add(contactEntity);
		repository.save(addressBook);
		return "Contact added to Address Book";
	}

	@Transactional
	public String addExistingContact(String addressBookId, String contactId) {
		if (addressBookId == null || addressBookId.trim().isEmpty()) {
			throw new InputValidationException("Address Book Id is required");
		}
		if (contactId == null || contactId.trim().isEmpty()) {
			throw new InputValidationException("Contact Id is required");
		}

		Optional<Contact> optExistingContact = contactRepository.findById(Long.valueOf(contactId.trim()));
		if (optExistingContact.isEmpty()) {
			throw new InputValidationException("Contact not found");
		}

		Contact existingContact = optExistingContact.get();
		Optional<AddressBook> optionalAddressBook = repository.findById(Long.valueOf(addressBookId.trim()));
		if (optionalAddressBook.isEmpty()) {
			throw new InputValidationException("Address Book not found");
		}

		AddressBook addressBook = optionalAddressBook.get();
		addressBook.getContacts().add(existingContact);
		repository.save(addressBook);

		return "Contact added to Address Book";
	}

	@Transactional(readOnly = true)
	public Set<Contact> findAllContactsByAddressBookId(String addressBookId) {
		if (addressBookId == null || addressBookId.trim().isEmpty()) {
			throw new InputValidationException("Address Book Id is required");
		}
		Optional<AddressBook> optionalAddressBook = repository.findById(Long.valueOf(addressBookId.trim()));
		if (optionalAddressBook.isEmpty()) {
			throw new InputValidationException("Address Book not found");
		}
		AddressBook addressBook = optionalAddressBook.get();
		return addressBook.getContacts();
	}

	@Transactional
	public String removeContactByAddressBookId(String addressBookId, ContactDto contact) {
		if (addressBookId == null || addressBookId.trim().isEmpty()) {
			throw new InputValidationException("Address Book Id is required");
		}
		if (contact == null || contact.contactId() == null) {
			throw new InputValidationException("Contact is required");
		}
		Contact contactEntity = new Contact();
		BeanUtils.copyProperties(contact, contactEntity);
		Optional<AddressBook> optionalAddressBook = repository.findById(Long.valueOf(addressBookId.trim()));
		if (optionalAddressBook.isEmpty()) {
			throw new InputValidationException("Address Book not found");
		}

		AddressBook addressBook = optionalAddressBook.get();
		boolean contactExistsInAddressBook = addressBook.getContacts().stream()
				.anyMatch(c -> contact.contactId() != null && c.getContactId().longValue() == (contact.contactId().longValue()));
		if (contactExistsInAddressBook) {
			addressBook.getContacts().iterator().forEachRemaining(c -> {
				if (contact.contactId() != null && c.getContactId().longValue() == (contact.contactId().longValue())) {
					addressBook.getContacts().remove(c);
					c.getAddressBooks().remove(addressBook);
				}
			});
			repository.save(addressBook);
	
			// Since it is ManyToMany Mapping and Contact is not the owner, it has to be
			// explicitly deleted if it has become and orphan
			Contact contactFromDb = contactRepository.findById(contact.contactId()).get();
	
			if (contactFromDb.getAddressBooks().isEmpty()) {
				contactRepository.deleteById(contactFromDb.getContactId());
			}
		}

		return "Successfully removed contact from Address Book";
	}
}
