/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StreamsFacade.
 */
public class StreamsFacade {
	
	/**
	 * Read.
	 *
	 * @param input the input
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final int read(InputStream input) throws IOException {
		return input.read();
	}
	
	/**
	 * Read bytes.
	 *
	 * @param input the input
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final byte[] readBytes(InputStream input) throws IOException {
		return IOUtils.toByteArray(input);
	}
	
	/**
	 * Read text.
	 *
	 * @param input the input
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String readText(InputStream input) throws IOException {
		return IOUtils.toString(input, StandardCharsets.UTF_8);
	}
	
	/**
	 * Close.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void close(InputStream input) throws IOException {
		input.close();
	}
	
	/**
	 * Write.
	 *
	 * @param output the output
	 * @param value the value
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void write(OutputStream output, int value) throws IOException {
		output.write(value);
	}
	
	/**
	 * Write bytes.
	 *
	 * @param output the output
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void writeBytes(OutputStream output, String input) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		output.write(bytes);
	}
	
	/**
	 * Write text.
	 *
	 * @param output the output
	 * @param value the value
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void writeText(OutputStream output, String value) throws IOException {
		output.write(value.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Close.
	 *
	 * @param output the output
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void close(OutputStream output) throws IOException {
		output.close();
	}
	
	/**
	 * Copy.
	 *
	 * @param input the input
	 * @param output the output
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void copy(InputStream input, OutputStream output) throws IOException {
		IOUtils.copy(input, output);
	}
	
	/**
	 * Creates the byte array input stream.
	 *
	 * @param input the input
	 * @return the byte array input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ByteArrayInputStream createByteArrayInputStream(byte[] input) throws IOException {
		return new ByteArrayInputStream(input);
	}
	
	/**
	 * Creates the byte array input stream.
	 *
	 * @param input the input
	 * @return the byte array input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ByteArrayInputStream createByteArrayInputStream(String input) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return new ByteArrayInputStream(bytes);
	}
	
	/**
	 * Creates the byte array input stream.
	 *
	 * @return the byte array input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ByteArrayInputStream createByteArrayInputStream() throws IOException {
		return new ByteArrayInputStream(new byte[]{});
	}
	
	/**
	 * Creates the byte array output stream.
	 *
	 * @return the byte array output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ByteArrayOutputStream createByteArrayOutputStream() throws IOException {
		return new ByteArrayOutputStream();
	}
	
	/**
	 * Gets the bytes.
	 *
	 * @param output the output
	 * @return the bytes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final byte[] getBytes(ByteArrayOutputStream output) throws IOException {
		byte[] bytes = output.toByteArray();
		return bytes;
	}
	
	/**
	 * Gets the text.
	 *
	 * @param output the output
	 * @return the text
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getText(ByteArrayOutputStream output) throws IOException {
		byte[] bytes = output.toByteArray();
		String text = new String(bytes, StandardCharsets.UTF_8);
		return text;
	}

}
