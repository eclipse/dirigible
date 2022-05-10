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
import java.util.List;

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

	private static final String REPOSITORY_GIT_FOLDER = "dirigible" + File.separator + "repository" + File.separator + DOT_GIT;
	private static final String DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER = "target" + File.separator + REPOSITORY_GIT_FOLDER; //$NON-NLS-1$

	private static final int MINIMUM_URL_LENGTH = 25;

	public static final String TEMP_DIRECTORY_PREFIX = "dirigible_git_";

	private static String GIT_ROOT_FOLDER;

	/** The repository. */
	private IRepository repository = null;
	
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
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
	 * @param gitDirectory
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
	public List<String> importProject(File gitDirectory, String basePath, String user, String workspace, String projectName)
			throws IOException {
		File[] listFiles = FileSystemUtils.listFiles(gitDirectory);
		List<String> importedProjects = new ArrayList<String>(listFiles.length-1);
		if (listFiles.length == 1) { // only .git folder
			if (projectName == null) {
				projectName = gitDirectory.getName();
			}
			File implicitProject = new File(gitDirectory, projectName);
			FileUtils.forceMkdir(implicitProject);
		}
		for (File file : listFiles) {
			String project = file.getName();
			if (file.isDirectory() && !project.startsWith(".")) {
				importProjectFromGitRepositoryToWorkspace(file, basePath + project);
//				saveGitPropertiesFile(properties, user, workspace, project);
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
	public void importProjectFromGitRepositoryToWorkspace(File gitRepositoryFile, String path) throws IOException {
		getRepository().linkPath(path, gitRepositoryFile.getCanonicalPath());
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
	 * @param gitDirectory
	 *            the git directory
	 * @return the valid project folders
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String[] getValidProjectFolders(File gitDirectory) throws IOException {
		List<String> valid = new ArrayList<String>();
		String[] all = gitDirectory.list();
		for (String name : all) {
			if (name.startsWith(".")) {
				continue;
			}
			File file = new File(gitDirectory.getCanonicalPath() + File.separator + name);
			if (file.isDirectory()) {
				valid.add(name);
			}
		}
		return valid.toArray(new String[] {});
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
		return FileSystemUtils.getGitDirectoryByRepositoryName(user, workspace, repositoryName);
	}

	public static List<String> getGitRepositoryProjects(String workspace, String repositoryName) {
		return FileSystemUtils.getGitRepositoryProjects(UserFacade.getName(), workspace, repositoryName);
	}
}
