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

package org.eclipse.dirigible.repository.rcp;

import java.util.List;

import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Internal representation of a Folder/Collection kind of object
 * 
 */
public class RCPFolder extends RCPObject {

	public RCPFolder(RCPRepository repository) {
		super(repository);
	}

	public void deleteTree() throws RCPBaseException {
		getRepository().getRepositoryDAO().removeFolderByPath(getPath());
	}

	public List<RCPObject> getChildren() throws RCPBaseException {
		List<RCPObject> result = getRepository().getRepositoryDAO()
				.getChildrenByFolder(getPath());
		return result;
	}

	public void createFolder(String name) throws RCPBaseException {
		getRepository().getRepositoryDAO().createFolder(
				RepositoryPath.normalizePath(getPath(), name));
	}

	public void createFile(String name, byte[] content, boolean isBinary,
			String contentType) throws RCPBaseException {
		getRepository().getRepositoryDAO().createFile(
				RepositoryPath.normalizePath(getPath(), name),
				content, isBinary, contentType);
	}
	
	public void renameFolder(String newPath) throws RCPBaseException {
		getRepository().getRepositoryDAO().renameFolder(getPath(), newPath);
	}

}
