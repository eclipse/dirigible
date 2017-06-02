package org.eclipse.dirigible.commons.config;

public class TestModeException extends Exception {
	
	private static final long serialVersionUID = -7766343853084847849L;
	
	public TestModeException() {
		super();
	}

	public TestModeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TestModeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TestModeException(String message) {
		super(message);
	}

	public TestModeException(Throwable cause) {
		super(cause);
	}

}
