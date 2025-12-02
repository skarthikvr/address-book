package com.demo.addressbook.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. ";
	private static final String RESOURCE_ERROR_MESSAGE = "Requested resource not found. ";
	private static final String VALIDATION_ERROR_MESSAGE = "Validation failed for input request parameters. Please check and try again. ";
	private static final String DATABASE_VIOLATION_MESSAGE = "Database Constraint violation. ";
	private static final String EXCEPTION_DETAILS_HEADING = "Exception Trace: ";
	private final boolean enableStackTrace = true; // Set to false to disable stack trace in responses
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return buildRespopnseWithExceptionTrace(ex, GENERIC_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handleGenericException(ConstraintViolationException ex) {
		return buildRespopnseWithExceptionTrace(ex, VALIDATION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleGenericException(MethodArgumentNotValidException ex) {
		return buildRespopnseWithExceptionTrace(ex, VALIDATION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleGenericException(DataIntegrityViolationException ex) {
		return buildRespopnseWithExceptionTrace(ex, DATABASE_VIOLATION_MESSAGE, HttpStatus.CONFLICT);
	}
	
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<String> handleGenericException(HandlerMethodValidationException ex) {
		return buildRespopnseWithExceptionTrace(ex, VALIDATION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InputValidationException.class)
    public ResponseEntity<String> handleException(InputValidationException ex) {
    	return buildRespopnseWithExceptionTrace(ex, RESOURCE_ERROR_MESSAGE, HttpStatus.NOT_FOUND);
    }
	
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleException(NoResourceFoundException ex) {
    	return buildRespopnseWithExceptionTrace(ex, RESOURCE_ERROR_MESSAGE, HttpStatus.NOT_FOUND);
    }
    
    private ResponseEntity<String> buildRespopnseWithExceptionTrace(Exception ex, String exceptionType, HttpStatus status) {
    	StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw); // Print the stack trace to the PrintWriter
    	StringBuilder sb = new StringBuilder();
    	sb.append(exceptionType)
    	.append("\n")
    	.append(ex.getMessage());
    	if (enableStackTrace) {
	    	sb.append("\n\n")
	    	.append(EXCEPTION_DETAILS_HEADING)
	    	.append("\n")
	    	.append(sw.toString());
    	}
		
        return ResponseEntity.status(status).body(sb.toString());
	}
}
