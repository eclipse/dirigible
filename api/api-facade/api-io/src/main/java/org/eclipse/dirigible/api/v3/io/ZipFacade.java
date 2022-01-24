/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * Facade for working with ZIP files
 */
public class ZipFacade {
	
	/**
	 * Create a {@link ZipInputStream} from the provided {@link InputStream}
	 * @param in the provided input stream
	 * @return the created ZipInputStream
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final ZipInputStream createZipInputStream(InputStream in) throws IOException {
		return new ZipInputStream(in);
	}

	/**
	 * Create a {@link ZipOutputStream} from the provided {@link OutputStream}
	 * @param out the provided output stream
	 * @return the created ZipOutputStream
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final ZipOutputStream createZipOutputStream(OutputStream out) throws IOException {
		return new ZipOutputStream(out);
	}

	/**
	 * Create a {@link ZipEntry} with the provided name
	 * @param name the name of teh ZipEntry
	 * @return the created ZipEntry
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final ZipEntry createZipEntry(String name) throws IOException {
		return new ZipEntry(name);
	}

	/**
	 * Write data to the provided zip output stream
	 * @param output OutputStream to write to
	 * @param bytes The data to be written
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final void write(ZipOutputStream output, byte[] bytes) throws IOException {
		output.write(bytes);
	}

	/**
	 * Write data to the provided zip output stream
	 * @param output OutputStream to write to
	 * @param data The data to be written
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final void write(ZipOutputStream output, String data) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(data);
		write(output, bytes);
	}
	
	/**
	 * Write data to the provided zip output stream
	 * @param output OutputStream to write to
	 * @param data The data to be written
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final void writeNative(ZipOutputStream output, byte[] data) throws IOException {
		write(output, data);
	}

	/**
	 * Write text to the provided zip output stream
	 * @param output OutputStream to write to
	 * @param text The text to be written
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final void writeText(ZipOutputStream output, String text) throws IOException {
		write(output, text.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Read data from the provided {@link InputStream}
	 * @param input The input stream to read from
	 * @return The read data
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final String read(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return GsonHelper.GSON.toJson(bytes);
	}
	
	/**
	 * Read data from the provided {@link InputStream}
	 * @param input The input stream to read from
	 * @return The read data
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final byte[] readNative(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return bytes;
	}

	/**
	 * Read text from the provided {@link InputStream}
	 * @param input The input stream to read from
	 * @return The read text
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final String readText(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
