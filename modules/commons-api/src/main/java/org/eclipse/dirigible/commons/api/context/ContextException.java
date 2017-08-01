package org.eclipse.dirigible.commons.api.context;

public class ContextException extends Exception {

	private static final long serialVersionUID = 5039208772641246649L;

	public ContextException() {
		super();
	}

	public ContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextException(String message) {
		super(message);
	}

	public ContextException(Throwable cause) {
		super(cause);
	}

}
