package org.eclipse.dirigible.runtime.scripting.utils;

public class ExceptionUtils {

	public Exception createException(String expectedMessage) {
		return new Exception(expectedMessage);
	}

}
