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
import org.eclipse.dirigible.repository.fs.FileSystemRepository;

/**
 * Internal representation of a Folder/Collection kind of object
 */
public class LocalFolder extends LocalObject {

	public LocalFolder(FileSystemRepository repository) {
		super(repository);
	}

	public void deleteTree() throws LocalRepositoryException {
		getRepository().getRepositoryDao().removeFolderByPath(getPath());
	}

	public List<LocalObject> getChildren() throws LocalRepositoryException {
		List<LocalObject> result = getRepository().getRepositoryDao().getChildrenByFolder(getPath());
		return result;
	}

	public void createFolder(String name) throws LocalRepositoryException {
		getRepository().getRepositoryDao().createFolder(RepositoryPath.normalizePath(getPath(), name));
	}

	public void createFile(String name, byte[] content, boolean isBinary, String contentType) throws LocalRepositoryException {
		getRepository().getRepositoryDao().createFile(RepositoryPath.normalizePath(getPath(), name), content, isBinary, contentType);
	}

	public void renameFolder(String newPath) throws LocalRepositoryException {
		getRepository().getRepositoryDao().renameFolder(getPath(), newPath);
	}

	public void copyFolder(String newPath) throws LocalRepositoryException {
		getRepository().getRepositoryDao().copyFolder(getPath(), newPath);
	}

}
