package com.demo.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactDto(Long contactId,
		@NotBlank(message = "First Name is required") @Size(min = 3, max = 20, message = "First Name must be between 3 and 20 characters") String firstName,
		@NotBlank(message = "Last Name is required") @Size(min = 3, max = 20, message = "Last Name must be between 3 and 20 characters") String lastName,
		@Size(min = 3, max = 25, message = "Organisation Name must be between 3 and 20 characters") String orgName,
		@Email String email,
		@NotBlank(message = "Contact Number is required") @Size(min = 3, max = 25, message = "Contact Number must be between 9 and 15 characters") String contactNumber) {
}
