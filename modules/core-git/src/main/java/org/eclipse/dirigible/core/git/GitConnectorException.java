package org.eclipse.dirigible.core.git;

public class GitConnectorException extends Exception {

	private static final long serialVersionUID = 3164412135969838078L;

	public GitConnectorException() {
		super();
	}

	public GitConnectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GitConnectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitConnectorException(String message) {
		super(message);
	}

	public GitConnectorException(Throwable cause) {
		super(cause);
	}

}
