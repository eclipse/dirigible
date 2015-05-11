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

package org.eclipse.dirigible.repository.db.dao;

import java.util.Arrays;

import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;

/**
 * Internal representation of a File/Resource kind of object
 * 
 */
public class DBFile extends DBObject {

	private boolean binary = false;

	private String contentType;

	public DBFile(DBRepository repository, boolean isBinary, String contentType) {
		super(repository);
		this.binary = isBinary;
		this.contentType = contentType;
	}

	public void delete() throws DBBaseException {
		getRepository().getRepositoryDAO().removeFileByPath(getPath());
	}

	 public void rename(String newPath) throws DBBaseException {
		 getRepository().getRepositoryDAO().renameFile(getPath(), newPath);
	 }

	public byte[] getData() throws DBBaseException {
		if (isBinary()) {
			byte[] data = getRepository().getRepositoryDAO().getBinary(this);
			return data;
		} else {
			byte[] data = getRepository().getRepositoryDAO().getDocument(this);
			return data;
		}
	}

	public void setData(byte[] content) throws DBBaseException {
		byte[] old = getData();
		if (old != null) {
			if (Arrays.equals(old, content)) {
				return;
			}
		}
		if (isBinary()) {
			getRepository().getRepositoryDAO().setBinary(this, content,
					getContentType());
		} else {
			getRepository().getRepositoryDAO().setDocument(this, content);
		}
	}

	public boolean isBinary() {
		return binary;
	}

	public String getContentType() {
		return contentType;
	}
}
