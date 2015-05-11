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

import java.util.List;

import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;

/**
 * Internal representation of a Folder/Collection kind of object
 * 
 */
public class DBFolder extends DBObject {

	public DBFolder(DBRepository repository) {
		super(repository);
	}

	public void deleteTree() throws DBBaseException {
		getRepository().getRepositoryDAO().removeFolderByPath(getPath());
	}

	public List<DBObject> getChildren() throws DBBaseException {
		List<DBObject> result = getRepository().getRepositoryDAO()
				.getChildrenByFolder(getPath());
		return result;
	}

	public void createFolder(String name) throws DBBaseException {
		getRepository().getRepositoryDAO().createFolder(
				RepositoryPath.normalizePath(getPath(), name));
	}

	public void createFile(String name, byte[] content, boolean isBinary,
			String contentType) throws DBBaseException {
		getRepository().getRepositoryDAO().createFile(
				RepositoryPath.normalizePath(getPath(), name),
				content, isBinary, contentType);
	}
	
	public void renameFolder(String newPath) throws DBBaseException {
		getRepository().getRepositoryDAO().renameFolder(getPath(), newPath);
	}

}
