package com.digitalvillage.exception;

/**
 * Base application exception for future domain and infrastructure errors.
 */
public class ApplicationException extends RuntimeException {

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
