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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	 * 
	 * @return if the project is git aware
	 */
	public static boolean getGitAware(IRepository repository, String repositoryPath) {
		File gitFolder = getGitFolderForProject(repository, repositoryPath);
		return gitFolder != null && gitFolder.exists();
	}
	
	/**
	 * Get the git folder
	 * 
	 * @param repository the repository
	 * @param repositoryPath the path
	 * 
	 * @return the git folder
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

	/**
	 * Describe project.
	 *
	 * @param rootFolder
	 *            the collection
	 *
	 * @return the project descriptor
	 */
	public static ProjectDescriptor describeProject(File rootFolder) {
		ProjectDescriptor project = new ProjectDescriptor();
		project.setName(rootFolder.getName());
		project.setPath(rootFolder.getPath());
		project.setGit(true);

		List<File> allFiles = Arrays.asList(rootFolder.listFiles());
		List<File> folders = allFiles.stream().filter(e -> !e.isFile()).collect(Collectors.toList());
		List<File> files = allFiles.stream().filter(e -> e.isFile()).collect(Collectors.toList());
		for (File next : folders) {
			if (!next.isFile() && !next.getName().equals(DOT_GIT)) {				
				project.getFolders().add(describeFolder(next));
			}
		}

		for (File next : files) {
			FileDescriptor file = new FileDescriptor();
			file.setName(next.getName());
			file.setPath(next.getPath());
			project.getFiles().add(file);
		}

		return project;
	}
	
	/**
	 * Describe folder.
	 *
	 * @param rootFolder
	 *            the collection
	 * @return the folder descriptor
	 */
	public static FolderDescriptor describeFolder(File rootFolder) {
		FolderDescriptor folder = new FolderDescriptor();
		folder.setName(rootFolder.getName());
		folder.setPath(rootFolder.getPath());
		List<File> allFiles = Arrays.asList(rootFolder.listFiles());
		List<File> folders = allFiles.stream().filter(e -> !e.isFile()).collect(Collectors.toList());
		List<File> files = allFiles.stream().filter(e -> e.isFile()).collect(Collectors.toList());
		for (File next : folders) {
			folder.getFolders().add(describeFolder(next));
		}

		for (File next : files) {
			FileDescriptor file = new FileDescriptor();
			file.setName(next.getName());
			file.setPath(next.getPath());
			folder.getFiles().add(file);
		}

		return folder;
	}
}
