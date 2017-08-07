package org.eclipse.dirigible.commons.api.context;

public class InvalidStateException extends RuntimeException {

	private static final long serialVersionUID = 5039208772641246649L;

	public InvalidStateException() {
		super();
	}

	public InvalidStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidStateException(String message) {
		super(message);
	}

	public InvalidStateException(Throwable cause) {
		super(cause);
	}

}
