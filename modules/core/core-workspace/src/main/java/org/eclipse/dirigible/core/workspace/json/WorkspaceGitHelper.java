/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.workspace.json;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;

public class WorkspaceGitHelper {
	
	private static final String DOT_GIT = ".git";
	
	/**
	 * Get the git flag
	 * 
	 * @param collection the collection
	 * @param repositoryPath the path
	 */
	public static boolean getGitAware(ICollection collection, String repositoryPath) {
		File gitFolder = getGitFolderForProject(collection, repositoryPath);
		if (gitFolder != null
				&& gitFolder.exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the git folder
	 * 
	 * @param collection the collection
	 * @param repositoryPath the path
	 */
	public static File getGitFolderForProject(ICollection collection, String repositoryPath) {
		try {
			if (collection.getRepository() instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) collection.getRepository(), repositoryPath);
				String gitDirectory = new File(path).getCanonicalPath();
				return Paths.get(Paths.get(gitDirectory).getParent().toString(), DOT_GIT).toFile();
			}
		} catch (Throwable e) {
			return null;
		}
		return null;
	}

}
