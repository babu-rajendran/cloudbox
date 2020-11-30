package com.babu.cloudbox.exception;

public class StorageFileNotFoundException extends com.babu.cloudbox.exception.StorageException {

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}