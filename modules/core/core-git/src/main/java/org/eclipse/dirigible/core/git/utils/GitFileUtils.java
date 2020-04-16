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
package org.eclipse.dirigible.core.git.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.config.Configuration;
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
	
	private static final String DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER = "target" + File.separator + DOT_GIT; //$NON-NLS-1$

	private static final int MINIMUM_URL_LENGTH = 25;


//	public static final String TEMP_DIRECTORY_PREFIX = "dirigible_git_"; //$NON-NLS-1$
	
	public static final String TEMP_DIRECTORY_PREFIX = "dirigible_git_";

	private static String GIT_ROOT_FOLDER;

	/** The repository. */
	@Inject
	private IRepository repository;

	static {
//		try {
			// ProxyUtils.setProxySettings();
			if (!StringUtils.isEmpty(Configuration.get(DIRIGIBLE_GIT_ROOT_FOLDER))) {
				GIT_ROOT_FOLDER = Configuration.get(DIRIGIBLE_GIT_ROOT_FOLDER);
			} else if (!StringUtils.isEmpty(Configuration.get(DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER))) {
				GIT_ROOT_FOLDER = Configuration.get(DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER) + File.separator + DOT_GIT;
			} else {
				GIT_ROOT_FOLDER = DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER;
			}
//			deleteGitDirectories();
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}
	}

//	/**
//	 * Delete git directories.
//	 *
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	private static void deleteGitDirectories() throws IOException {
//		File gitDirectory = createGitDirectory("DeleteDirectory");
//		deleteDirectories(gitDirectory);
//	}
//
//	private static void deleteDirectories(File file) throws IOException {
//		File tempDirectory = file.getParentFile().getCanonicalFile();
//		if (tempDirectory != null) {
//			File[] listFiles = FileSystemUtils.listFiles(tempDirectory);
//			if (listFiles != null) {
//				for (File temp : listFiles) {
//					if (temp != null && temp.isDirectory() && temp.getName().startsWith(TEMP_DIRECTORY_PREFIX)) {
//						deleteDirectory(temp);
//					}
//				}
//				deleteDirectory(file);
//			}
//		}
//	}

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
	 * Generate the local repository name
	 * 
	 * @param repositoryURI the URI odf the repository
	 * @return the generated local name
	 */
	public static String generateGitRepositoryName(String repositoryURI) {
		String repositoryName = repositoryURI.substring(repositoryURI.lastIndexOf(SLASH) + 1, repositoryURI.lastIndexOf(DOT_GIT));
		return repositoryName;
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
	
	/**
	 * Get the directory for git
	 * 
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryURI the repository URI
	 * @return the directory
	 */
	public static File getGitDirectory(String user, String workspace, String repositoryURI) {
		String repositoryName = generateGitRepositoryName(repositoryURI);
		return FileSystemUtils.getDirectory(GIT_ROOT_FOLDER, user, workspace, repositoryName);
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
			if (file.isDirectory() && !project.equalsIgnoreCase(DOT_GIT)) {
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
//		if (gitRepositoryFile.isDirectory()) {
//			for (File file : FileSystemUtils.listFiles(gitRepositoryFile)) {
//				importProjectFromGitRepositoryToWorkspace(file, path + File.separator + file.getName());
//			}
//		}
//		if (!gitRepositoryFile.isDirectory()) {
//			repository.createResource(path).setContent(readFile(gitRepositoryFile));
//		}
		repository.linkPath(path, gitRepositoryFile.getCanonicalPath());
	}

//	/**
//	 * Read file.
//	 *
//	 * @param file
//	 *            the file
//	 * @return the byte[]
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	public static byte[] readFile(File file) throws IOException {
//		final ByteArrayOutputStream out = new ByteArrayOutputStream();
//		InputStream in = null;
//		try {
//			in = new FileInputStream(file);
//			IOUtils.copy(in, out);
//		} finally {
//			if (in != null) {
//				in.close();
//			}
//		}
//		return out.toByteArray();
//	}

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
		if (this.repository instanceof FileSystemRepository) {
			String absolutePath = LocalWorkspaceMapper.getMappedName((FileSystemRepository) repository, path);
			return absolutePath;
		}
		throw new IllegalArgumentException("Repository must be file based to use git utilities: " + path);
	}

//	/**
//	 * Save git properties file.
//	 *
//	 * @param properties
//	 *            the properties
//	 * @param user
//	 *            the user
//	 * @param workspace
//	 *            the workspace
//	 * @param project
//	 *            the project
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	public void saveGitPropertiesFile(GitProjectProperties properties, String user, String workspace, String project) throws IOException {
//		String dirigibleGitFolderPath = generateRepositoryGitFolder(user, workspace, project);
//		if (repository.hasCollection(dirigibleGitFolderPath)) {
//			ICollection propertiesFolder = repository.getCollection(dirigibleGitFolderPath);
//
//			if (propertiesFolder.getResource(GitProjectProperties.PROJECT_GIT_PROPERTY).exists()) {
//				IResource propertiesFile = propertiesFolder.getResource(GitProjectProperties.PROJECT_GIT_PROPERTY);
//				propertiesFile.setContent(properties.getContent());
//			} else {
//				propertiesFolder.createResource(GitProjectProperties.PROJECT_GIT_PROPERTY, properties.getContent(), false,
//						ContentTypeHelper.DEFAULT_CONTENT_TYPE);
//			}
//		} else {
//			repository.createCollection(dirigibleGitFolderPath).createResource(GitProjectProperties.PROJECT_GIT_PROPERTY, properties.getContent(),
//					false, ContentTypeHelper.DEFAULT_CONTENT_TYPE);
//		}
//
//	}

	/**
	 * Generates the git folder per user, workspace and project
	 * 
	 * @param user the logged-n user
	 * @param workspace the workspace
	 * @param project the project
	 * @return generated folder path
	 */
	public String generateRepositoryGitFolder(String user, String workspace, String project) {
		String dirigibleGitFolderPath = String.format(GitProjectProperties.PATTERN_USERS_GIT_REPOSITORY, user, workspace, project);
		return dirigibleGitFolderPath;
	}

//	/**
//	 * Delete project folder from directory.
//	 *
//	 * @param parentDirectory
//	 *            the parent directory
//	 * @param selectedProject
//	 *            the selected project
//	 * @throws IOException 
//	 */
//	public static void deleteProjectFolderFromDirectory(File parentDirectory, String selectedProject) throws IOException {
//		for (File file : FileSystemUtils.listFiles(parentDirectory)) {
//			if (file.getName().equals(selectedProject)) {
//				deleteFiles(file);
//				if (!file.delete()) {
//					logger.error(String.format("File [%s] deletion failed.", file.getAbsolutePath()));
//				}
//			}
//		}
//	}

//	/**
//	 * Delete directory.
//	 *
//	 * @param directory
//	 *            the directory
//	 * @throws IOException IO error
//	 */
//	public static void deleteDirectory(File directory) throws IOException {
//		if (directory != null) {
//			deleteFiles(directory);
//			if (!directory.delete()) {
//				logger.error(String.format("Directory [%s] deletion failed.", directory.getAbsolutePath()));
//			}
//		}
//	}

//	/**
//	 * Delete files.
//	 *
//	 * @param directory
//	 *            the directory
//	 * @throws IOException 
//	 */
//	private static void deleteFiles(File directory) throws IOException {
//		if (directory != null) {
//			File[] listFiles = FileSystemUtils.listFiles(directory);
//			if (listFiles != null) {
//				for (File file : listFiles) {
//					if (file.isDirectory()) {
//						deleteDirectory(file);
//					}
//					if (!file.delete()) {
//						file.deleteOnExit();
//					}
//				}
//			}
//		}
//	}

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

//	/**
//	 * Gets the git properties for project.
//	 *
//	 * @param workspace
//	 *            the workspace
//	 * @param project
//	 *            the project
//	 * @return the git properties for project
//	 * @throws IOException
//	 *             Signals that an I/O exception has occurred.
//	 */
//	public GitProjectProperties getGitPropertiesForProject(final IWorkspace workspace, final IProject project) throws IOException {
//
//		String workspaceName = workspace.getName();
//		String projectName = project.getName();
//		String userName = UserFacade.getName();
//		IResource resource = repository
//				.getResource(String.format(GitProjectProperties.GIT_PROPERTY_FILE_LOCATION, userName, workspaceName, projectName));
//		InputStream in = new ByteArrayInputStream(resource.getContent());
//		GitProjectProperties gitProperties = new GitProjectProperties(in);
//		return gitProperties;
//	}

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

}
