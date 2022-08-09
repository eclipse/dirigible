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
package org.eclipse.dirigible.cms.db.api;

import java.io.InputStream;

/**
 * The Class ContentStream.
 */
public class ContentStream {

	/** The cmis session. */
	private CmisSession cmisSession;

	/** The filename. */
	private String filename;

	/** The length. */
	private long length;

	/** The mimetype. */
	private String mimetype;

	/** The input stream. */
	private InputStream inputStream;

	/**
	 * Instantiates a new content stream.
	 *
	 * @param cmisSession the cmis session
	 * @param filename the filename
	 * @param length the length
	 * @param mimetype the mimetype
	 * @param inputStream the input stream
	 */
	public ContentStream(CmisSession cmisSession, String filename, long length, String mimetype, InputStream inputStream) {
		super();
		this.cmisSession = cmisSession;
		this.filename = filename;
		this.length = length;
		this.mimetype = mimetype;
		this.inputStream = inputStream;
	}

	/**
	 * Returns the InputStream of this ContentStream object.
	 *
	 * @return Input Stream
	 */
	public InputStream getStream() {
		return this.inputStream;
	}

	/**
	 * Gets the cmis session.
	 *
	 * @return the cmis session
	 */
	public CmisSession getCmisSession() {
		return cmisSession;
	}

	/**
	 * Sets the cmis session.
	 *
	 * @param cmisSession the new cmis session
	 */
	public void setCmisSession(CmisSession cmisSession) {
		this.cmisSession = cmisSession;
	}

	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		return mimetype;
	}

	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

}
