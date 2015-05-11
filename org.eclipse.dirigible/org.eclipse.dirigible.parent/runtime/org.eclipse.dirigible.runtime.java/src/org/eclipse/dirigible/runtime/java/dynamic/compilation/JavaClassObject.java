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

package org.eclipse.dirigible.runtime.java.dynamic.compilation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaClassObject extends SimpleJavaFileObject {

	private static final char SLASH = '/';
	private static final char DOT = '.';
	private static final String STRING_URI = "string:///";
	
	private final String name;
	private final String content;
	private final long lastModified;
	private final ByteArrayOutputStream bos;

	public JavaClassObject(String name, Kind kind, String content, long lastModified) {
		super(URI.create(STRING_URI + name.replace(DOT, SLASH) + kind.extension), kind);
		this.name = name;
		this.content = content.trim();
		this.lastModified = lastModified;
		this.bos = new ByteArrayOutputStream();
	}

	public byte[] getBytes() {
		return bos.toByteArray();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return content != null ? content : super.getCharContent(ignoreEncodingErrors);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		bos.reset();
		return bos;
	}
}
