package com.rob.ceuploadxml.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Details of an exception when a file cannot be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public final class StorageFileNotFoundException extends StorageException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2570647946932759317L;

	public StorageFileNotFoundException(final String message) {
		super(message);
	}

	public StorageFileNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}
}