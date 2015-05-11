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

package org.eclipse.dirigible.runtime.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractConsoleOutputTest {

	private PrintStream backupOut;
	private PrintStream backupErr;

	private ByteArrayOutputStream baos;
	private PrintStream printStream;

	@Before
	public void setUp() throws Exception {
		backupOut = System.out;
		backupErr = System.err;
		
		baos = new ByteArrayOutputStream();
		printStream = new PrintStream(baos);
		
		System.setOut(printStream);
		System.setErr(printStream);
	}

	@After
	public void tearDown() throws Exception {
		if (baos != null) {
			baos.close();
		}
		if(printStream != null){
			printStream.close();
		}
		System.setOut(backupOut);
		System.setErr(backupErr);
	}

	protected String getOutput() throws IOException {
		String output = null;
		if(baos != null){
			output = baos.toString();
			baos.reset();
		}
		return output;
	}
}
