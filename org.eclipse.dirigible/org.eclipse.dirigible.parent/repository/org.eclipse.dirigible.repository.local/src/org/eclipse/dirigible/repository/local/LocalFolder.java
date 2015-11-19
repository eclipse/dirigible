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

import java.util.List;

import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Internal representation of a Folder/Collection kind of object
 */
public class LocalFolder extends LocalObject {

	public LocalFolder(FileSystemRepository repository) {
		super(repository);
	}

	public void deleteTree() throws LocalBaseException {
		getRepository().getRepositoryDAO().removeFolderByPath(getPath());
	}

	public List<LocalObject> getChildren() throws LocalBaseException {
		List<LocalObject> result = getRepository().getRepositoryDAO().getChildrenByFolder(getPath());
		return result;
	}

	public void createFolder(String name) throws LocalBaseException {
		getRepository().getRepositoryDAO().createFolder(RepositoryPath.normalizePath(getPath(), name));
	}

	public void createFile(String name, byte[] content, boolean isBinary, String contentType) throws LocalBaseException {
		getRepository().getRepositoryDAO().createFile(RepositoryPath.normalizePath(getPath(), name), content, isBinary, contentType);
	}

	public void renameFolder(String newPath) throws LocalBaseException {
		getRepository().getRepositoryDAO().renameFolder(getPath(), newPath);
	}

}
