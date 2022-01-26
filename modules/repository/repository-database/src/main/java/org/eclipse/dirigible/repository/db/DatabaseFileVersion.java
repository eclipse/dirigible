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
package org.eclipse.dirigible.repository.db;

/**
 * Internal representation of a File/Resource kind of object.
 */
public class DatabaseFileVersion extends DatabaseFile {

	private int version;

	private byte[] bytes;

	/**
	 * Instantiates a new local file version.
	 *
	 * @param repository
	 *            the repository
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 * @param version
	 *            the version
	 * @param bytes
	 *            the bytes
	 */
	public DatabaseFileVersion(DatabaseRepository repository, boolean isBinary, String contentType, int version, byte[] bytes) {
		super(repository, isBinary, contentType);
		this.version = version;
		this.bytes = bytes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.local.LocalFile#getData()
	 */
	@Override
	public byte[] getData() throws DatabaseRepositoryException {
		byte[] data = this.bytes;
		return data;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

}
