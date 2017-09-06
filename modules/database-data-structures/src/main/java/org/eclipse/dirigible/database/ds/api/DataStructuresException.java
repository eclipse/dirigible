package org.eclipse.dirigible.database.ds.api;

public class DataStructuresException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public DataStructuresException() {
		super();
	}

	public DataStructuresException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataStructuresException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataStructuresException(String message) {
		super(message);
	}

	public DataStructuresException(Throwable cause) {
		super(cause);
	}

}
