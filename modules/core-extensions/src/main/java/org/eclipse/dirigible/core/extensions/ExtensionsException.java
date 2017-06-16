package org.eclipse.dirigible.core.extensions;

public class ExtensionsException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public ExtensionsException() {
		super();
	}

	public ExtensionsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExtensionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtensionsException(String message) {
		super(message);
	}

	public ExtensionsException(Throwable cause) {
		super(cause);
	}

}
