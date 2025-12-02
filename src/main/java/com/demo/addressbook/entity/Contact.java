package com.demo.addressbook.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CONTACT", uniqueConstraints = {
		@UniqueConstraint(name = "UC_NAME", columnNames = { "firstName", "lastName" }) })
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long contactId;

	@NotBlank(message = "First Name is required")
	@Size(min = 3, max = 20, message = "First Name must be between 3 and 20 characters")
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "Last Name is required")
	@Size(min = 3, max = 20, message = "Last Name must be between 3 and 20 characters")
	@Column(nullable = false)
	private String lastName;

	@Size(min = 3, max = 25, message = "Organisation Name must be between 3 and 20 characters")
	@Column
	private String orgName;

	@Email
	@Column
	private String email;

	@NotBlank(message = "Contact Number is required")
	@Size(min = 3, max = 25, message = "Contact Number must be between 9 and 15 characters")
	@Column(nullable = false)
	private String contactNumber;

	@ManyToMany(mappedBy = "contacts", fetch = FetchType.LAZY)
	private Set<AddressBook> addressBooks = new HashSet<>();

	public Contact() {
	}

	public Contact(String firstName, String lastName, String contactNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.contactNumber = contactNumber;
	}

	public Contact(String firstName, String lastName, String orgName, String email, String contactNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.orgName = orgName;
		this.email = email;
		this.contactNumber = contactNumber;
	}

	public Long getContactId() {
		return contactId;
	}

	/*
	 * public void setContactId(Long contactId) { this.contactId = contactId; }
	 */

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Set<AddressBook> getAddressBooks() {
		return addressBooks;
	}

	public void setAddressBooks(Set<AddressBook> addressBooks) {
		this.addressBooks = addressBooks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [");
		if (contactId != null) {
			builder.append("contactId=");
			builder.append(contactId);
			builder.append(", ");
		}
		if (firstName != null) {
			builder.append("firstName=");
			builder.append(firstName);
			builder.append(", ");
		}
		if (lastName != null) {
			builder.append("lastName=");
			builder.append(lastName);
			builder.append(", ");
		}
		if (orgName != null) {
			builder.append("orgName=");
			builder.append(orgName);
			builder.append(", ");
		}
		if (email != null) {
			builder.append("email=");
			builder.append(email);
			builder.append(", ");
		}
		if (contactNumber != null) {
			builder.append("contactNumber=");
			builder.append(contactNumber);
		}
		builder.append("]");
		return builder.toString();
	}

}
