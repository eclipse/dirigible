package org.eclipse.dirigible.database.managed;

import org.eclipse.dirigible.database.api.DatabaseException;

public class ManagedDatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	public ManagedDatabaseException() {
		super();
	}

	public ManagedDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ManagedDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagedDatabaseException(String message) {
		super(message);
	}

	public ManagedDatabaseException(Throwable cause) {
		super(cause);
	}

}
