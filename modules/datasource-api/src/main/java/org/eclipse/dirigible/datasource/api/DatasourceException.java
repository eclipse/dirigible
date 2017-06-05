package org.eclipse.dirigible.datasource.api;

public class DatasourceException extends RuntimeException {

	private static final long serialVersionUID = -2161860568272479874L;

	public DatasourceException() {
		super();
	}

	public DatasourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DatasourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatasourceException(String message) {
		super(message);
	}

	public DatasourceException(Throwable cause) {
		super(cause);
	}

}
