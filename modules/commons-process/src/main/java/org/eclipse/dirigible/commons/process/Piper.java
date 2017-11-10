/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.process;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.input.BoundedInputStream;
import org.eclipse.dirigible.commons.config.Configuration;

// TODO: Auto-generated Javadoc
/**
 * The Class Piper.
 */
public class Piper implements java.lang.Runnable {

	/** The Constant BROKEN_PIPE. */
	private static final String BROKEN_PIPE = "Broken pipe";

	/** The Constant MAX_COMMAND_OUTPUT_LENGTH. */
	private static final long MAX_COMMAND_OUTPUT_LENGTH = 2097152;

	/** The input. */
	private java.io.InputStream input;

	/** The output. */
	private java.io.OutputStream output;

	/**
	 * Instantiates a new piper.
	 *
	 * @param input the input
	 * @param output the output
	 */
	public Piper(java.io.InputStream input, java.io.OutputStream output) {
		this.input = new BoundedInputStream(input, MAX_COMMAND_OUTPUT_LENGTH);
		this.output = output;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			byte[] b = new byte[512];
			int read = 1;
			int sum = 0;
			while (read > -1) {
				read = input.read(b, 0, b.length);
				sum += read;
				if (read > -1) {
					// Write bytes to output
					output.write(b, 0, read);
				}
			}
			if (sum >= (MAX_COMMAND_OUTPUT_LENGTH - 1)) {
				output.write("\n...".getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			// Something happened while reading or writing streams; pipe is broken
			throw new RuntimeException(BROKEN_PIPE, e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
			}
			try {
				output.close();
			} catch (Exception e) {
			}
		}
	}

}
