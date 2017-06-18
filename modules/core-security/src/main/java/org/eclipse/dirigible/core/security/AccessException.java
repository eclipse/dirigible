package org.eclipse.dirigible.core.security;

public class AccessException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public AccessException() {
		super();
	}

	public AccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessException(String message) {
		super(message);
	}

	public AccessException(Throwable cause) {
		super(cause);
	}

}
