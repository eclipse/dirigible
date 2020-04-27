/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.workspace.json;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;

public class WorkspaceGitHelper {
	
	private static final String DOT_GIT = ".git";
	
	/**
	 * Get the git flag
	 * 
	 * @param repository the repository
	 * @param repositoryPath the path
	 */
	public static boolean getGitAware(IRepository repository, String repositoryPath) {
		File gitFolder = getGitFolderForProject(repository, repositoryPath);
		if (gitFolder != null
				&& gitFolder.exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the git folder
	 * 
	 * @param repository the repository
	 * @param repositoryPath the path
	 */
	public static File getGitFolderForProject(IRepository repository, String repositoryPath) {
		try {
			if (repository instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) repository, repositoryPath);
				String gitDirectory = new File(path).getCanonicalPath();
				return Paths.get(Paths.get(gitDirectory).getParent().toString(), DOT_GIT).toFile();
			}
		} catch (Throwable e) {
			return null;
		}
		return null;
	}

}
