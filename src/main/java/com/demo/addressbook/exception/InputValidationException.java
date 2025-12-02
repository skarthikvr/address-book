package com.demo.addressbook.exception;

@SuppressWarnings("serial")
public class InputValidationException extends AddressBookException {
	public InputValidationException(String message) {
		super(message);
	}
}
