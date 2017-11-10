/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;

public interface IFolder extends ICollection {

	public ICollection getInternal();

	public IFolder createFolder(String path);

	public boolean existsFolder(String path);

	public IFolder getFolder(String path);

	public List<IFolder> getFolders();

	public void deleteFolder(String path);

	public IFile createFile(String path, byte[] content);

	public IFile createFile(String path, byte[] content, boolean isBinary, String contentType);

	public IFile getFile(String path);

	public boolean existsFile(String path);

	public List<IFile> getFiles();

	public void deleteFile(String path);

}
