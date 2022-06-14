/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Git file utility.
 */
public class GitFileUtils {

	private static final Logger logger = LoggerFactory.getLogger(GitFileUtils.class);
	
	private static final String DIRIGIBLE_GIT_ROOT_FOLDER = "DIRIGIBLE_GIT_ROOT_FOLDER"; //$NON-NLS-1$
	private static final String DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER = "DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER"; //$NON-NLS-1$ 

	public static final String SLASH = "/"; //$NON-NLS-1$
	public static final String DOT_GIT = ".git"; //$NON-NLS-1$
	
	/** The Constant PATTERN_USERS_WORKSPACE. */
	public static final String PATTERN_USERS_WORKSPACE = IRepositoryStructure.PATH_USERS + "/%s/%s/"; // /users/john/workspace1

	private static final String REPOSITORY_GIT_FOLDER = "dirigible" + File.separator + "repository" + File.separator + DOT_GIT;
	private static final String DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER = "target" + File.separator + REPOSITORY_GIT_FOLDER; //$NON-NLS-1$

	private static final int MINIMUM_URL_LENGTH = 25;

	public static final String TEMP_DIRECTORY_PREFIX = "dirigible_git_";

	private static String GIT_ROOT_FOLDER;

	/** The repository. */
	private static IRepository REPOSITORY = null;
	
	private static synchronized IRepository getRepository() {
		if (REPOSITORY == null) {
			REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return REPOSITORY;
	}

	static {
		if (!StringUtils.isEmpty(Configuration.get(DIRIGIBLE_GIT_ROOT_FOLDER))) {
			GIT_ROOT_FOLDER = Configuration.get(DIRIGIBLE_GIT_ROOT_FOLDER);
		} else if (!StringUtils.isEmpty(Configuration.get(DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER))) {
			GIT_ROOT_FOLDER = Configuration.get(DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER) + File.separator + REPOSITORY_GIT_FOLDER;
		} else {
			GIT_ROOT_FOLDER = DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER;
		}
	}

	/**
	 * Checks if is valid repository URI.
	 *
	 * @param repositoryURI
	 *            the repository URI
	 * @return true, if is valid repository URI
	 */
	public static boolean isValidRepositoryURI(String repositoryURI) {
		return (repositoryURI.endsWith(DOT_GIT)) && (repositoryURI.length() > MINIMUM_URL_LENGTH);
	}
	
	/**
	 * Create the directory for git
	 * 
	 * @param user the logged-in user
	 * @param workspace the workspace
	 * @param repositoryURI the repository URI
	 * @return the directory
	 * @throws IOException IO error
	 */
	public static File createGitDirectory(String user, String workspace, String repositoryURI) throws IOException {
		String repositoryName = generateGitRepositoryName(repositoryURI);
		return FileSystemUtils.forceCreateDirectory(GIT_ROOT_FOLDER, user, workspace, repositoryName);
	}

	public static void deleteGitDirectory(String user, String workspace, String repositoryURI) throws IOException {
		String repositoryName = generateGitRepositoryName(repositoryURI);

		FileSystemUtils.deleteDirectory(GIT_ROOT_FOLDER, user, workspace, repositoryName);
	}

	/**
	 * Import project.
	 *
	 * @param workspace the workspace
	 * @param repository the repository
	 * @return the imported project
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static List<String> importProject(String workspace, String repository) throws IOException {
		String user = UserFacade.getName();
		File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace, repository);
		String workspacePath = String.format(GitFileUtils.PATTERN_USERS_WORKSPACE, user, workspace);
		return GitFileUtils.importProject(gitDirectory, workspacePath, user, workspace, null);
	}

	/**
	 * Import project.
	 *
	 * @param gitRepository
	 *            the git directory
	 * @param basePath
	 *            the base path
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param projectName
	 *            an optional project name in case of an empty repository
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List<String> importProject(File gitRepository, String basePath, String user, String workspace, String projectName)
			throws IOException {
		List<File> projects = FileSystemUtils.getGitRepositoryProjects(gitRepository);
		List<String> importedProjects = new ArrayList<String>();
		if (projects.size() == 0) {
			// Empty Git repository, using it as a root project
			projects.add(gitRepository);
		}
		for (File file : projects) {
			String project = file.getName();
			if (file.isDirectory() && !project.startsWith(".")) {
				importProjectFromGitRepositoryToWorkspace(file, basePath + project);
				importedProjects.add(project);
			}
		}
		return importedProjects;
	}

	/**
	 * Import project from git repository to workspace.
	 *
	 * @param gitRepositoryFile
	 *            the git repository file
	 * @param path
	 *            the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void importProjectFromGitRepositoryToWorkspace(File gitRepositoryFile, String path) throws IOException {
		// Skip already linked paths
		if (!getRepository().isLinkedPath(path)) {
			getRepository().linkPath(path, gitRepositoryFile.getCanonicalPath());
		}
	}

	/**
	 * Delete repository project.
	 *
	 * @param project
	 *            the project
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void deleteRepositoryProject(IProject project) throws IOException {
		project.delete();
	}
	
	/**
	 * Returns the local absolute path of a Repository path
	 * @param path the Repository path
	 * @return the local absolute path
	 */
	public String getAbsolutePath(String path) {
		if (this.getRepository() instanceof FileSystemRepository) {
			String absolutePath = LocalWorkspaceMapper.getMappedName((FileSystemRepository) getRepository(), path);
			return absolutePath;
		}
		throw new IllegalArgumentException("Repository must be file based to use git utilities: " + path);
	}

	/**
	 * Copy project to directory.
	 *
	 * @param source
	 *            the source
	 * @param tempGitDirectory
	 *            the temp git directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyProjectToDirectory(IFolder source, File tempGitDirectory) throws IOException {
		if (!source.exists()) {
			return;
		}
		for (IFile file : source.getFiles()) {
			RepositoryPath path = new RepositoryPath(source.getPath());
			StringBuilder resourceDirectory = new StringBuilder();
			for (int i = 3; i < (path.getSegments().length - 1); i++) {
				resourceDirectory.append(File.separator + path.getSegments()[i]);
			}
			resourceDirectory.append(File.separator);
			File fileResource = new File(tempGitDirectory, resourceDirectory.toString());
			FileUtils.forceMkdir(fileResource.getCanonicalFile());
			String resourcePath = resourceDirectory + file.getPath().substring(path.getParentPath().getPath().length() + 1);

			InputStream in = null;
			FileOutputStream out = null;
			try {
				in = new ByteArrayInputStream(file.getContent());
				File outputFile = new File(tempGitDirectory, resourcePath);
				FileUtils.forceMkdir(outputFile.getParentFile().getCanonicalFile());
				boolean fileCreated = outputFile.createNewFile();
				if (!fileCreated) {
					throw new IOException("Error in creating the file: " + outputFile.getCanonicalPath());
				}
				out = new FileOutputStream(outputFile);
				IOUtils.copy(in, out);
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			}
		}
		for (IFolder folder : source.getFolders()) {
			copyProjectToDirectory(folder, tempGitDirectory);
		}

	}

	/**
	 * Gets the valid project folders.
	 *
	 * @param gitRepository
	 *            the git directory
	 * @return the valid project folders
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String[] getValidProjectFolders(File gitRepository) throws IOException {
		List<File> projects = FileSystemUtils.getGitRepositoryProjects(gitRepository);
		List<String> projectsNames = projects.stream().map(e -> e.getName()).collect(Collectors.toList());
		return projectsNames.toArray(new String[]{});
	}

	public static boolean isGitProject(IRepository repository, String repositoryPath) {
		try {
			if (repository instanceof FileSystemRepository) {
				String path = LocalWorkspaceMapper.getMappedName((FileSystemRepository) repository, repositoryPath);
				File gitDirectory = new File(path).getCanonicalFile();
				IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());
				gitConnector.getBranch();
				return true;
			}
			logger.error("Not a file system based repository used with git");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return false;
	}

	/**
	 * Generate the local repository name
	 * 
	 * @param repositoryURI the URI odf the repository
	 * @return the generated local name
	 */
	public static String generateGitRepositoryName(String repositoryURI) {
		return FileSystemUtils.generateGitRepositoryName(repositoryURI);
	}

	/**
	 * Get the directory for git
	 * 
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @return the directory
	 */
	public static File getGitDirectory(String user, String workspace) {
		return FileSystemUtils.getGitDirectory(user, workspace);
	}

	/**
	 * Get the directory for git
	 * 
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryURI the repository URI
	 * @return the directory
	 */
	public static File getGitDirectory(String user, String workspace, String repositoryURI) {
		return FileSystemUtils.getGitDirectory(user, workspace, repositoryURI);
	}

	/**
	 * Get the directory for git
	 * 
	 * @param workspace the workspace
	 * @param repositoryName the repository URI
	 * @return the directory
	 */
	public static File getGitDirectoryByRepositoryName(String workspace, String repositoryName) {
		return getGitDirectoryByRepositoryName(UserFacade.getName(), workspace, repositoryName);
	}

	/**
	 * Get the directory for git
	 * 
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryName the repository URI
	 * @return the directory
	 */
	public static File getGitDirectoryByRepositoryName(String user, String workspace, String repositoryName) {
		File gitDirectoryByRepositoryName = FileSystemUtils.getGitDirectoryByRepositoryName(user, workspace, repositoryName);
		if (gitDirectoryByRepositoryName == null) {
			return getGitDeepDirectoryByRepositoryName(UserFacade.getName(), workspace, repositoryName);
		}
		return gitDirectoryByRepositoryName;
	}
	
	/**
	 * Get the directory for git for deep projects
	 * 
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryName the repository URI
	 * @return the directory
	 */
	private static File getGitDeepDirectoryByRepositoryName(String user, String workspace, String repositoryName) {
		return FileSystemUtils.getGitDeepDirectoryByRepositoryName(user, workspace, repositoryName);
	}

	/**
	 * Get the projects in that git repository.
	 * @param workspace the workspace
	 * @param repositoryName the repository
	 * @return the projects
	 */
	public static List<String> getGitRepositoryProjects(String workspace, String repositoryName) {
		return FileSystemUtils.getGitRepositoryProjects(UserFacade.getName(), workspace, repositoryName);
	}

	/**
	 * Get the projects in that git repository.
	 * @param workspace the workspace
	 * @param repositoryName the repository
	 * @return the projects
	 */
	public static List<File> getGitRepositoryProjectsFiles(String workspace, String repositoryName) {
		return FileSystemUtils.getGitRepositoryProjectsFiles(UserFacade.getName(), workspace, repositoryName);
	}
}
