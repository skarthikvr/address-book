package com.demo.addressbook.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.addressbook.entity.Contact;
import com.demo.addressbook.repository.ContactRepository;

@Service
public class ContactService {
	ContactRepository contactRepository;
	
	public ContactService(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}
	
	public Set<Contact> getAllUniqueContacts() {
		return new HashSet<>(contactRepository.findAll());
	}
	
	public List<Contact> getAllContacts() {
		return contactRepository.findAll();
	}
}
