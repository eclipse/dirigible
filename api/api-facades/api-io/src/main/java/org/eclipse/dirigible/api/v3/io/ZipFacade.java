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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ZipFacade.
 */
public class ZipFacade {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ZipFacade.class);
	
	/**
	 * Creates the zip input stream.
	 *
	 * @param in the in
	 * @return the zip input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ZipInputStream createZipInputStream(InputStream in) throws IOException {
		return new ZipInputStream(in);
	}
	
	/**
	 * Creates the zip output stream.
	 *
	 * @param out the out
	 * @return the zip output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ZipOutputStream createZipOutputStream(OutputStream out) throws IOException {
		return new ZipOutputStream(out);
	}
	
	/**
	 * Creates the zip entry.
	 *
	 * @param name the name
	 * @return the zip entry
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final ZipEntry createZipEntry(String name) throws IOException {
		return new ZipEntry(name);
	}
	
	/**
	 * Write.
	 *
	 * @param output the output
	 * @param bytes the bytes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void write(ZipOutputStream output, byte[] bytes) throws IOException {
		output.write(bytes);
	}
	
	/**
	 * Write.
	 *
	 * @param output the output
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void write(ZipOutputStream output, String data) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(data);
		write(output, bytes);
	}
	
	/**
	 * Write text.
	 *
	 * @param output the output
	 * @param text the text
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void writeText(ZipOutputStream output, String text) throws IOException {
		write(output, text.getBytes());
	}
	
	/**
	 * Read.
	 *
	 * @param input the input
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String read(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return GsonHelper.GSON.toJson(bytes);
	}
	
	/**
	 * Read text.
	 *
	 * @param input the input
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String readText(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return new String(bytes);
	}

}
