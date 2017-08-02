/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.core.git.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitFileUtils {

	private static final String DOT_GIT = ".git"; //$NON-NLS-1$
	private static final int MINIMUM_URL_LENGTH = 25;

	private static final Logger logger = LoggerFactory.getLogger(GitFileUtils.class);

	public static final String TEMP_DIRECTORY_PREFIX = "dirigible.jgit."; //$NON-NLS-1$

	@Inject
	private IRepository repository;

	static {
		try {
			// ProxyUtils.setProxySettings();
			deleteTempDirectories();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static void deleteTempDirectories() throws IOException {
		File file = GitFileUtils.createTempDirectory("DeleteDirectory");
		File tempDirectory = file.getParentFile();
		for (File temp : tempDirectory.listFiles()) {
			if (temp.isDirectory() && temp.getName().startsWith(TEMP_DIRECTORY_PREFIX)) {
				GitFileUtils.deleteDirectory(temp);
			}
		}
		GitFileUtils.deleteDirectory(file);
	}

	public static boolean isValidRepositoryURI(String repositoryURI) {
		return (repositoryURI.endsWith(DOT_GIT)) && (repositoryURI.length() > MINIMUM_URL_LENGTH);
	}

	public static File createTempDirectory(String directory) throws IOException {
		return RepositoryFileUtils.createTempDirectory(directory);
	}

	public static File createTempDirectory(String directory, String suffix) throws IOException {
		return RepositoryFileUtils.createTempDirectory(directory, suffix);
	}

	public List<String> importProject(File gitDirectory, String basePath, String user, String workspace, GitProjectProperties properties)
			throws IOException {
		List<String> importedProjects = new ArrayList<String>(gitDirectory.listFiles().length);
		for (File file : gitDirectory.listFiles()) {
			String project = file.getName();
			if (file.isDirectory() && !project.equalsIgnoreCase(DOT_GIT)) {
				importProjectFromGitRepositoryToWorkspace(file, basePath + project);
				saveGitPropertiesFile(properties, user, workspace, project);
				importedProjects.add(project);
			}
		}
		return importedProjects;
	}

	private void importProjectFromGitRepositoryToWorkspace(File gitRepositoryFile, String path) throws IOException {
		if (gitRepositoryFile.isDirectory()) {
			for (File file : gitRepositoryFile.listFiles()) {
				importProjectFromGitRepositoryToWorkspace(file, path + File.separator + file.getName());
			}
		}
		if (!gitRepositoryFile.isDirectory()) {
			repository.createResource(path).setContent(readFile(gitRepositoryFile));
		}
	}

	public static byte[] readFile(File file) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final InputStream in = new FileInputStream(file);
		try {
			IOUtils.copy(in, out);
		} finally {
			in.close();
		}
		return out.toByteArray();
	}

	public void deleteRepositoryProject(IProject project) throws IOException {
		project.delete();
	}

	public void saveGitPropertiesFile(GitProjectProperties properties, String user, String workspace, String project) throws IOException {
		String dirigibleGitFolderPath = String.format(GitProjectProperties.PATTERN_USERS_GIT_REPOSITORY, user, workspace, project);
		if (repository.hasCollection(dirigibleGitFolderPath)) {
			ICollection propertiesFolder = repository.getCollection(dirigibleGitFolderPath);

			if (propertiesFolder.getResource(GitProjectProperties.PROJECT_GIT_PROPERTY).exists()) {
				IResource propertiesFile = propertiesFolder.getResource(GitProjectProperties.PROJECT_GIT_PROPERTY);
				propertiesFile.setContent(properties.getContent());
			} else {
				propertiesFolder.createResource(GitProjectProperties.PROJECT_GIT_PROPERTY, properties.getContent(), false,
						ContentTypeHelper.DEFAULT_CONTENT_TYPE);
			}
		} else {
			repository.createCollection(dirigibleGitFolderPath).createResource(GitProjectProperties.PROJECT_GIT_PROPERTY, properties.getContent(),
					false, ContentTypeHelper.DEFAULT_CONTENT_TYPE);
		}

	}

	public static void deleteProjectFolderFromDirectory(File parentDirectory, String selectedProject) {
		for (File next : parentDirectory.listFiles()) {
			if (next.getName().equals(selectedProject)) {
				deleteFiles(next);
				next.delete();
			}
		}
	}

	public static void deleteDirectory(File directory) {
		if (directory != null) {
			deleteFiles(directory);
			directory.delete();
		}
	}

	private static void deleteFiles(File directory) {
		if (directory != null) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				}
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		}
	}

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
			new File(tempGitDirectory, resourceDirectory.toString()).mkdirs();
			String resourcePath = resourceDirectory + file.getPath().substring(path.getParentPath().getPath().length() + 1);

			InputStream in = new ByteArrayInputStream(file.getContent());
			File outputFile = new File(tempGitDirectory, resourcePath);
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
			FileOutputStream out = new FileOutputStream(outputFile);
			try {
				IOUtils.copy(in, out);
			} finally {
				in.close();
				out.flush();
				out.close();
			}
		}
		for (IFolder folder : source.getFolders()) {
			copyProjectToDirectory(folder, tempGitDirectory);
		}

	}

	public GitProjectProperties getGitPropertiesForProject(final IWorkspace workspace, final IProject project) throws IOException {

		String workspaceName = workspace.getName();
		String projectName = project.getName();
		String userName = UserFacade.getName();
		IResource resource = repository
				.getResource(String.format(GitProjectProperties.GIT_PROPERTY_FILE_LOCATION, userName, workspaceName, projectName));
		InputStream in = new ByteArrayInputStream(resource.getContent());
		GitProjectProperties gitProperties = new GitProjectProperties(in);
		return gitProperties;
	}

	public static String[] getValidProjectFolders(File gitDirectory) throws IOException {
		List<String> valid = new ArrayList<String>();
		String[] all = gitDirectory.list();
		for (String name : all) {
			if (name.equals(DOT_GIT)) {
				continue;
			}
			File file = new File(gitDirectory.getCanonicalPath() + File.separator + name);
			if (file.isDirectory()) {
				valid.add(name);
			}
		}
		return valid.toArray(new String[] {});
	}

}
