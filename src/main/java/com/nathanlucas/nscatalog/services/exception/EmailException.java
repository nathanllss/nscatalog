package com.nathanlucas.nscatalog.services.exception;

@SuppressWarnings("serial")
public class EmailException extends RuntimeException {

	public EmailException(String msg) {
		super(msg);
	}
}
