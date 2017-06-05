package org.eclipse.dirigible.datasource.local;

import org.eclipse.dirigible.datasource.api.DatasourceException;

public class LocalDatasourceException extends DatasourceException {

	private static final long serialVersionUID = -2161860568272479874L;

	public LocalDatasourceException() {
		super();
	}

	public LocalDatasourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LocalDatasourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocalDatasourceException(String message) {
		super(message);
	}

	public LocalDatasourceException(Throwable cause) {
		super(cause);
	}

}
