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
package org.eclipse.dirigible.cms.internal;

import java.io.InputStream;

/**
 * A factory for creating Object objects.
 */
public class ObjectFactory {

	/** The session. */
	private CmisSession session;

	/**
	 * Instantiates a new object factory.
	 *
	 * @param session the session
	 */
	public ObjectFactory(CmisSession session) {
		super();
		this.session = session;
	}

	/**
	 * Creates a new Object object.
	 *
	 * @param filename the filename
	 * @param length the length
	 * @param mimetype the mimetype
	 * @param inputStream the input stream
	 * @return the content stream
	 */
	public ContentStream createContentStream(String filename, long length, String mimetype, InputStream inputStream) {
		return new ContentStream(this.session, filename, length, mimetype, inputStream);
	}

}
