package org.eclipse.dirigible.commons.api.scripting;

public class ScriptingException extends Exception {

	private static final long serialVersionUID = 375339390660073390L;

	public ScriptingException() {
		super();
	}

	public ScriptingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptingException(String message) {
		super(message);
	}

	public ScriptingException(Throwable cause) {
		super(cause);
	}

}
