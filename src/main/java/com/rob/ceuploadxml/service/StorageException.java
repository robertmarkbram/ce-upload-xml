package com.rob.ceuploadxml.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Details of an exception when trying to save a file.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageException extends RuntimeException {
	
	/**
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 6231003402518549932L;

	public StorageException(final String message) {
		super(message);
	}

	public StorageException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
