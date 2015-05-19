package org.eclipse.dirigible.runtime.scripting;

public class EStorageException extends Exception {

	private static final long serialVersionUID = -9130624040418318845L;

	public EStorageException() {
	}

	public EStorageException(String message) {
		super(message);
	}

	public EStorageException(Throwable cause) {
		super(cause);
	}

	public EStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public EStorageException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
