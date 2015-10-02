package org.eclipse.dirigible.runtime.utils;

import static org.junit.Assert.*;

import org.eclipse.dirigible.runtime.scripting.utils.ExceptionUtils;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExceptionUtilsTest {

	private ExceptionUtils exceptionUtils = new ExceptionUtils();
	
	@Test
	public void testCreateException() throws Exception {
		String expectedMessage = "Exception Message";
		Exception exception = exceptionUtils.createException(expectedMessage);
		assertNotNull(exception);
		assertEquals(expectedMessage, exception.getMessage());
	}
}
