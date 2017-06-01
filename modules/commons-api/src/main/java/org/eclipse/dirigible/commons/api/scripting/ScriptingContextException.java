package org.eclipse.dirigible.commons.api.scripting;

public class ScriptingContextException extends ScriptingException {

	private static final long serialVersionUID = 5039208772641246649L;

	public ScriptingContextException() {
		super();
	}

	public ScriptingContextException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScriptingContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptingContextException(String message) {
		super(message);
	}

	public ScriptingContextException(Throwable cause) {
		super(cause);
	}

}
