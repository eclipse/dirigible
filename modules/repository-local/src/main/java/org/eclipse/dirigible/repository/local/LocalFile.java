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

	public void delete() throws LocalBaseException {
		getRepository().getRepositoryDAO().removeFileByPath(getPath());
	}

	public void rename(String newPath) throws LocalBaseException {
		getRepository().getRepositoryDAO().renameFile(getPath(), newPath);
	}

	public byte[] getData() throws LocalBaseException {
		return getRepository().getRepositoryDAO().getFileContent(this);
	}

	public void setData(byte[] content) throws LocalBaseException {
		// byte[] old = getData();
		// if (old != null) {
		// if (Arrays.equals(old, content)) {
		// return;
		// }
		// }
		getRepository().getRepositoryDAO().setFileContent(this, content);
	}

	public boolean isBinary() {
		return binary;
	}

	public String getContentType() {
		return contentType;
	}
}
