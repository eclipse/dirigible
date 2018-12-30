/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.fs.FileSystemRepository;

/**
 * Internal representation of a File/Resource kind of object.
 */
public class LocalFileVersion extends LocalFile {

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
	public LocalFileVersion(FileSystemRepository repository, boolean isBinary, String contentType, int version, byte[] bytes) {
		super(repository, isBinary, contentType);
		this.version = version;
		this.bytes = bytes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.local.LocalFile#getData()
	 */
	@Override
	public byte[] getData() throws LocalRepositoryException {
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
