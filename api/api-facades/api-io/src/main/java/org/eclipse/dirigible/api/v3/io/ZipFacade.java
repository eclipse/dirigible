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

public class ZipFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(ZipFacade.class);
	
	public static final ZipInputStream createZipInputStream(InputStream in) throws IOException {
		return new ZipInputStream(in);
	}
	
	public static final ZipOutputStream createZipOutputStream(OutputStream out) throws IOException {
		return new ZipOutputStream(out);
	}
	
	public static final ZipEntry createZipEntry(String name) throws IOException {
		return new ZipEntry(name);
	}
	
	public static final void write(ZipOutputStream output, byte[] bytes) throws IOException {
		output.write(bytes);
	}
	
	public static final void write(ZipOutputStream output, String data) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(data);
		write(output, bytes);
	}
	
	public static final void writeText(ZipOutputStream output, String text) throws IOException {
		write(output, text.getBytes());
	}
	
	public static final String read(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return GsonHelper.GSON.toJson(bytes);
	}
	
	public static final String readText(ZipInputStream input) throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return new String(bytes);
	}

}
