package com.demo.addressbook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.repository.ContactRepository;

class ContactServiceTest {

	@Mock
	ContactRepository contactRepository;

	ContactService contactService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		contactService = new ContactService(contactRepository);
	}

	@Test
	void getAllUniqueContacts_empty_returnsEmptySet() {
		when(contactRepository.findAll()).thenReturn(List.of());
		Set<Contact> result = contactService.getAllUniqueContacts();
		assertEquals(0, result.size());
	}

	@Test
	void getAllUniqueContacts_nonEmpty_returnsSet() {
		Contact c = new Contact("X", "Y", "123");
		when(contactRepository.findAll()).thenReturn(List.of(c));
		Set<Contact> result = contactService.getAllUniqueContacts();
		assertEquals(1, result.size());
	}
}
