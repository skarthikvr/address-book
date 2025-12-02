package com.demo.addressbook.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.addressbook.entity.AddressBook;
import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.repository.AddressBookRepository;
import com.demo.addressbook.repository.ContactRepository;

@Service
public class InitialSetupService {
	AddressBookRepository addressBookRepository;
	ContactRepository contactRepository;

	public InitialSetupService(AddressBookRepository addressBookRepository, ContactRepository contactRepository) {
		this.addressBookRepository = addressBookRepository;
		this.contactRepository = contactRepository;
	}

	@Transactional
	public List<AddressBook> setUpAddressBookData() {
		// Set up 5 Address Books and 5 Contacts
		// Link each Contact to one Address Book initially
		// Then link one Contact to multiple Address Books
		AddressBook addressBook1 = new AddressBook("Business");
		addressBookRepository.save(addressBook1);
		AddressBook addressBook2 = new AddressBook("Personal");
		AddressBook addressBook3 = new AddressBook("Government");
		AddressBook addressBook4 = new AddressBook("NGO");
		AddressBook addressBook5 = new AddressBook("Contractor");

		Contact contact1 = new Contact("Matthew", "Hayden", "+61 123 456 789");
		Contact contact2 = new Contact("Andrew", "Symonds", "+61 234 567 890");
		Contact contact3 = new Contact("Adam", "Gilchrist", "+61 345 678 901");
		Contact contact4 = new Contact("Michael", "Bevan", "+61 456 789 012");
		Contact contact5 = new Contact("Shane", "Warne", "+61 567 890 123");

		addressBook1.getContacts().add(contact1);
		addressBook2.getContacts().add(contact2);
		addressBook3.getContacts().add(contact3);
		addressBook4.getContacts().add(contact4);
		addressBook5.getContacts().add(contact5);

		addressBookRepository
				.saveAll(Stream.of(addressBook1, addressBook2, addressBook3, addressBook4, addressBook5).toList());

		Contact c = contactRepository.findById(5L).get();
		addressBook1.getContacts().add(c);
		addressBookRepository.save(addressBook1);

		return addressBookRepository.findAll();
	}
}
