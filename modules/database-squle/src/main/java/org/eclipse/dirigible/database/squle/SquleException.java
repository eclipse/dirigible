package org.eclipse.dirigible.database.squle;

public class SquleException extends RuntimeException {

	private static final long serialVersionUID = 4878658205810743068L;

	public SquleException() {
		super();
	}

	public SquleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SquleException(String message, Throwable cause) {
		super(message, cause);
	}

	public SquleException(String message) {
		super(message);
	}

	public SquleException(Throwable cause) {
		super(cause);
	}

	
}
