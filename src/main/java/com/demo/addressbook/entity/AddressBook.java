package com.demo.addressbook.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class AddressBook {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long addressBookId;
		
		@NotBlank(message = "Name is required")
		@Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
		@Column
		private String name;
		
		@Column(nullable = true)
		@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
		@JoinTable(
				  name = "addressBookContact", 
				  joinColumns = @JoinColumn(name = "addressBookId"), 
				  inverseJoinColumns = @JoinColumn(name = "contactId"))
		private Set<Contact> contacts = new HashSet<>();
		
		public Long getAddressBookId() {
			return addressBookId;
		}
		
		public void setAddressBookId(Long addressBookId) {
			this.addressBookId = addressBookId;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Set<Contact> getContacts() {
			return contacts;
		}
		
		public void setContacts(Set<Contact> contacts) {
			this.contacts = contacts;
		}
	
		public AddressBook() {
		}
		
		public AddressBook(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AddressBook [addressBookId=");
			builder.append(addressBookId);
			builder.append(", name=");
			builder.append(name);
			builder.append(", contacts=");
			builder.append(contacts);
			builder.append("]");
			return builder.toString();
		}
	}
