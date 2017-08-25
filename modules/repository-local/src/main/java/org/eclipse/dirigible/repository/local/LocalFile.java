/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.fs.FileSystemRepository;

/**
 * Internal representation of a File/Resource kind of object
 */
public class LocalFile extends LocalObject {

	private boolean binary = false;

	private String contentType;

	public LocalFile(FileSystemRepository repository, boolean isBinary, String contentType) {
		super(repository);
		this.binary = isBinary;
		this.contentType = contentType;
	}

	public void delete() throws LocalRepositoryException {
		getRepository().getRepositoryDao().removeFileByPath(getPath());
	}

	public void rename(String newPath) throws LocalRepositoryException {
		getRepository().getRepositoryDao().renameFile(getPath(), newPath);
	}

	public void copyTo(String newPath) throws LocalRepositoryException {
		getRepository().getRepositoryDao().copyFile(getPath(), newPath);
	}

	public byte[] getData() throws LocalRepositoryException {
		return getRepository().getRepositoryDao().getFileContent(this);
	}

	public void setData(byte[] content) throws LocalRepositoryException {
		getRepository().getRepositoryDao().setFileContent(this, content);
	}

	public boolean isBinary() {
		return binary;
	}

	public String getContentType() {
		return contentType;
	}
}
