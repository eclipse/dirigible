/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.logger;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.commons.AbstractConsoleOutputTest;


public class LoggerTest extends AbstractConsoleOutputTest {

	private static final Logger logger = LoggerStub.getLogger(LoggerTest.class);
	
	private static final String ERROR = "Error";
	private static final String DEBUG = "Debug";
	private static final String WARN = "Warn";
	private static final String INFO = "Info";
	private static final String TRACE = "Trace";

	private static final Throwable THROWABLE = new Exception("Exception");

	private static void assertContains(Throwable t, String output) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		t.printStackTrace(writer);

		assertContains(t.getMessage(), output);
		assertContains(baos.toString(), output);
		
		writer.close();
		baos.close();
	}

	private static void assertContains(String expected, String output) throws Exception {
		String message = String.format("The output did not contains \"%s\", output \"%s\"", expected, output);
		assertTrue(message, output.contains(expected));
	}

	@Test
	public void test() throws Exception {
		// Workaround for failing tests. When these tests are executed in
		// separate test methods, only the first one passes successfully.
		
		testLogDebug();
		testLogError();
		testLogWarn();
		testLogInfo();
		testLogTrace();
		
		testLogDebugWithThrowable();
		testLogErrorWithThrowable();
		testLogWarnWithThrowable();
		testLogInfoWithThrowable();
		testLogTraceWithThrowable();
	}

	protected void testLogError() throws Exception {
		logger.error(ERROR);
		assertContains(ERROR, getOutput());
	}

	protected void testLogErrorWithThrowable() throws Exception {
		logger.error(ERROR, THROWABLE);
		String output = getOutput();
		
		assertContains(ERROR, output);
		assertContains(THROWABLE, output);
	}

	protected void testLogDebug() throws Exception {
		logger.debug(DEBUG);
		assertContains(DEBUG, getOutput());
	}

	protected void testLogDebugWithThrowable() throws Exception {
		logger.debug(DEBUG, THROWABLE);
		String output = getOutput();

		assertContains(DEBUG, output);
		assertContains(THROWABLE, output);
	}

	protected void testLogWarn() throws Exception {
		logger.warn(WARN);
		assertContains(WARN, getOutput());
	}

	protected void testLogWarnWithThrowable() throws Exception {
		logger.warn(WARN, THROWABLE);
		String output = getOutput();

		assertContains(WARN, output);
		assertContains(THROWABLE, output);
	}
	
	protected void testLogInfo() throws Exception {
		logger.info(INFO);
		assertContains(INFO, getOutput());
	}

	protected void testLogInfoWithThrowable() throws Exception {
		logger.info(INFO, THROWABLE);
		String output = getOutput();

		assertContains(INFO, output);
		assertContains(THROWABLE, output);
	}

	protected void testLogTrace() throws Exception {
		logger.trace(TRACE);
		assertContains(TRACE, getOutput());
	}

	protected void testLogTraceWithThrowable() throws Exception {
		logger.trace(TRACE, THROWABLE);
		String output = getOutput();

		assertContains(TRACE, output);
		assertContains(THROWABLE, output);
	}
}
