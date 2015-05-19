package org.eclipse.dirigible.repository.ext.extensions;

public class EExtensionException extends Exception {

	private static final long serialVersionUID = -2069640036396336034L;

	public EExtensionException() {
	}

	public EExtensionException(String message) {
		super(message);
	}

	public EExtensionException(Throwable cause) {
		super(cause);
	}

	public EExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EExtensionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
