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
package org.eclipse.dirigible.commons.api.helpers;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.cxf.common.util.StringUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System Utils.
 */
public class FileSystemUtils {

	private static final String FOLDER_TARGET = "target";
	private static final String PREFIX_DOT = ".";
	private static final String PROJECT_JSON = "project.json";
	private static final Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);
	private static final String SEPARATOR = "/";
	public static final String DOT_GIT = ".git"; //$NON-NLS-1$
	public static final String PROJECT_METADATA_FILE_NAME = PROJECT_JSON; //$NON-NLS-1$

	private static String GIT_ROOT_FOLDER;
	private static final String DIRIGIBLE_GIT_ROOT_FOLDER = "DIRIGIBLE_GIT_ROOT_FOLDER"; //$NON-NLS-1$
	private static final String DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER = "DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER"; //$NON-NLS-1$
	private static final String REPOSITORY_GIT_FOLDER = "dirigible" + File.separator + "repository" + File.separator + DOT_GIT;
	private static final String DEFAULT_DIRIGIBLE_GIT_ROOT_FOLDER = FOLDER_TARGET + File.separator + REPOSITORY_GIT_FOLDER; //$NON-NLS-1$

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
	 * Save file.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @param content
	 *            the content
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void saveFile(String workspacePath, byte[] content) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePath);
		Path path = FileSystems.getDefault().getPath(FilenameUtils.normalize(workspacePath));
		if (content == null) {
			content = new byte[] {};
		}
		Files.write(path, content);
	}

	/**
	 * Load file.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return the byte[]
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] loadFile(String workspacePath) throws FileNotFoundException, IOException {
		String normalizedPath = FilenameUtils.normalize(workspacePath);
		Path path = FileSystems.getDefault().getPath(normalizedPath);
		// if (Files.exists(path)) {
		if (path.toFile().exists()) {
			return Files.readAllBytes(path);
		}
		return null;
	}

	/**
	 * Move file.
	 *
	 * @param workspacePathOld
	 *            the workspace path old
	 * @param workspacePathNew
	 *            the workspace path new
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void moveFile(String workspacePathOld, String workspacePathNew) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePathNew);
		Path pathOld = FileSystems.getDefault().getPath(workspacePathOld);
		Path pathNew = FileSystems.getDefault().getPath(workspacePathNew);
		Files.move(pathOld, pathNew);
	}

	/**
	 * Copy file.
	 *
	 * @param srcPath
	 *            the src path
	 * @param destPath
	 *            the dest path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyFile(String srcPath, String destPath) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(destPath);
		FileSystem fileSystem = FileSystems.getDefault();
		Path srcFile = fileSystem.getPath(srcPath);
		Path destFile = fileSystem.getPath(destPath);
		if (fileExists(destFile.toString())) {
			String fileName = destFile.getFileName().toString();
			String destFilePath = destPath.substring(0, destPath.lastIndexOf(fileName));
			String fileTitle = "";
			String fileExt = "";
			if (fileName.indexOf('.') != -1) {
				fileTitle = fileName.substring(0, fileName.lastIndexOf(PREFIX_DOT));
				fileExt = fileName.substring(fileName.lastIndexOf(PREFIX_DOT));
			} else {
				fileTitle = fileName;
			}
			int inc = 1;
			fileName = fileTitle + " (copy " + inc + ")" + fileExt;
			while (fileExists(destFilePath + fileName)) {
				fileName = fileTitle + " (copy " + ++inc + ")" + fileExt;
			}
			destFile = fileSystem.getPath(destFilePath + fileName);
		}
		FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
	}

	/**
	 * Copy folder.
	 *
	 * @param srcPath
	 *            the src path
	 * @param destPath
	 *            the dest path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyFolder(String srcPath, String destPath) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(destPath);
		FileSystem fileSystem = FileSystems.getDefault();
		Path srcDir = fileSystem.getPath(srcPath);
		Path destDir = fileSystem.getPath(destPath);
		FileUtils.copyDirectory(srcDir.toFile(), destDir.toFile(), true);
	}

	/**
	 * Removes the file.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void removeFile(String workspacePath) throws FileNotFoundException, IOException {
		Path path = FileSystems.getDefault().getPath(workspacePath);
		// logger.debug("Deleting file: " + file);

		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		Files.walkFileTree(path, opts, Integer.MAX_VALUE, new FileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				// if (Files.exists(dir)) {
				if (dir.toFile().exists()) {
					logger.trace(String.format("Deleting directory: %s", dir));
					try {
						Files.delete(dir);
					} catch (java.nio.file.NoSuchFileException e) {
						logger.trace(String.format("Directory already has been deleted: %s", dir));
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// if (Files.exists(file)) {
				if (file.toFile().exists()) {
					logger.trace(String.format("Deleting file: %s", file));
					try {
						Files.delete(file);
					} catch (java.nio.file.NoSuchFileException e) {
						logger.trace(String.format("File already has been deleted: %s", file));
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				logger.error(String.format("Error in file: %s", file), exc);
				return FileVisitResult.CONTINUE;
			}
		});

	}

	/**
	 * Creates the folder.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return true, if successful
	 */
	public static boolean createFolder(String workspacePath) {
		File folder = new File(FilenameUtils.normalize(workspacePath));
		if (!folder.exists()) {
			try {
				FileUtils.forceMkdir(folder.getCanonicalFile());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * Creates the file.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean createFile(String workspacePath) throws IOException {
		createFoldersIfNecessary(workspacePath);
		File file = new File(workspacePath);
		if (!file.exists()) {
			return file.createNewFile();
		}
		return true;
	}

	/**
	 * Gets the extension.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return the extension
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getExtension(String workspacePath) throws IOException {
		File f = new File(workspacePath);
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if ((i >= 0) && (i < (s.length() - 1))) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Gets the owner.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return the owner
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getOwner(String workspacePath) throws IOException {
		String convertedPath = convertToWorkspacePath(workspacePath);
		Path path = FileSystems.getDefault().getPath(convertedPath);
		if (new File(convertedPath).exists()) {
			return Files.getOwner(path).getName();
		} else {
			return "none";
		}
	}

	/**
	 * Gets the modified at.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @return the modified at
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Date getModifiedAt(String workspacePath) throws IOException {
		String convertedPath = convertToWorkspacePath(workspacePath);
		Path path = FileSystems.getDefault().getPath(convertedPath);
		if (new File(convertedPath).exists()) {
			return new Date(Files.getLastModifiedTime(path).toMillis());
		} else {
			return new Date();
		}
	}

	/**
	 * Creates the folders if necessary.
	 *
	 * @param workspacePath
	 *            the workspace path
	 */
	public static void createFoldersIfNecessary(String workspacePath) {
		String normalizedPath = FilenameUtils.normalize(workspacePath);
		int lastIndexOf = normalizedPath.lastIndexOf(File.separator);
		if (lastIndexOf > 0) {
			String directory = normalizedPath.substring(0, lastIndexOf);
			if (!directoryExists(directory)) {
				createFolder(directory);
			}
		}
	}

	/**
	 * Convert to workspace path.
	 *
	 * @param path
	 *            the path
	 * @return the string
	 */
	private static String convertToWorkspacePath(String path) {
		String normalizedPath = FilenameUtils.normalize(path);
		String workspacePath = null;
		if (normalizedPath.startsWith(SEPARATOR)) {
			workspacePath = normalizedPath.substring(SEPARATOR.length());
		} else {
			workspacePath = normalizedPath;
		}
		workspacePath = workspacePath.replace(SEPARATOR, File.separator);
		return workspacePath;
	}

	/**
	 * Exists.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 */
	public static boolean exists(String location) {
		if ((location == null) || "".equals(location)) {
			return false;
		}
		Path path;
		try {
			String normalizedPath = FilenameUtils.normalize(location);
			path = FileSystems.getDefault().getPath(normalizedPath);
			File file = path.toFile();
			return file.exists() && file.getCanonicalFile().getName().equals(file.getName());
		} catch (java.nio.file.InvalidPathException | IOException e) {
			logger.warn(e.getMessage());
			return false;
		}

		// return Files.exists(path);
	}

	/**
	 * Directory exists.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 */
	public static boolean directoryExists(String location) {
		return exists(location) && isDirectory(location);
	}

	/**
	 * File exists.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 */
	public static boolean fileExists(String location) {
		return exists(location) && !isDirectory(location);
	}

	/**
	 * Checks if is directory.
	 *
	 * @param location
	 *            the location
	 * @return true, if is directory
	 */
	public static boolean isDirectory(String location) {
		Path path;
		try {
			String normalizedPath = FilenameUtils.normalize(location);
			path = FileSystems.getDefault().getPath(normalizedPath);
		} catch (java.nio.file.InvalidPathException e) {
			return false;
		}
		return path.toFile().isDirectory();
		// return Files.isDirectory(path);
	}

	/**
	 * List files including symbolic links
	 *
	 * @param directory the source directory
	 * @return the list of files
	 * @throws IOException IO error
	 */
	public static File[] listFiles(File directory) throws IOException {
		Path link = Paths.get(directory.getAbsolutePath());
		Stream<Path> filesStream = Files.list(link);
		try {
			Path[] paths = filesStream.toArray(size -> new Path[size]);
			File[] files = new File[paths.length];
			for (int i=0; i<paths.length; i++) {
				files[i] = paths[i].toFile();
			}
			return files;
		} finally {
			filesStream.close();
		}
	}

	/**
	 * Force create directory and all its parents
	 *
	 * @param firstSegment the first segment
	 * @param segments the rest segments
	 * @return the resulting path
	 * @throws IOException IO error
	 */
	public static File forceCreateDirectory(String firstSegment, String ...segments) throws IOException {
		Path dir = Paths.get(firstSegment, segments);
		if (dir.toFile().exists()) {
			return dir.toFile();
		}
		return Files.createDirectories(dir).toFile();
	}

	public static void deleteDirectory(String firstSegment, String ...segments) throws IOException  {
		Path dir = Paths.get(firstSegment, segments);
		File file = new File(String.valueOf(dir));
		FileUtils.deleteDirectory(file);
	}

	/**
	 * Returns the directory by segments
	 *
	 * @param firstSegment the first segment
	 * @param segments the rest segments
	 * @return the resulting path
	 */
	public static File getDirectory(String firstSegment, String ...segments) {
		Path dir = Paths.get(firstSegment, segments);
		if (dir.toFile().exists()) {
			return dir.toFile();
		}
		return null;
	}

	/**
	 * Generate the local repository name
	 *
	 * @param repositoryURI the URI odf the repository
	 * @return the generated local name
	 */
	public static String generateGitRepositoryName(String repositoryURI) {
		int separatorLastIndexOf = repositoryURI.lastIndexOf(SEPARATOR) + 1;
		int gitLastIndexOf = repositoryURI.lastIndexOf(DOT_GIT);
		String repositoryName = repositoryURI;
		if (separatorLastIndexOf >= 0 && gitLastIndexOf > 0) {
			repositoryName = repositoryURI.substring(separatorLastIndexOf, gitLastIndexOf);
		} else if (separatorLastIndexOf >= 0) {
			repositoryName = repositoryURI.substring(separatorLastIndexOf);
		}
		return repositoryName;
	}

	/**
	 * Get the directory for git
	 *
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @return the directory
	 */
	public static File getGitDirectory(String user, String workspace) {
		return FileSystemUtils.getDirectory(GIT_ROOT_FOLDER, user, workspace);
	}

	public static List<String> getGitRepositories(String user, String workspace) {
		File gitRoot = getGitDirectory(user, workspace);
		if (gitRoot == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(gitRoot.listFiles())
				.stream()
				.filter(e -> !e.isFile())
				.map(e -> e.getName())
				.collect(Collectors.toList());
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
	 * Get the directory for git
	 *
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryName the repository URI
	 * @return the directory
	 */
	public static File getGitDirectoryByRepositoryName(String user, String workspace, String repositoryName) {
		File directGitDirectory = FileSystemUtils.getDirectory(GIT_ROOT_FOLDER, user, workspace, repositoryName);
		return directGitDirectory;
	}
	
	/**
	 * Get the directory for git for deep projects
	 *
	 * @param user logged-in user
	 * @param workspace the workspace
	 * @param repositoryName the repository URI
	 * @return the directory
	 */
	public static File getGitDeepDirectoryByRepositoryName(String user, String workspace, String repositoryName) {
		File workspaceGitDirectory = FileSystemUtils.getDirectory(GIT_ROOT_FOLDER, user, workspace);
		if (workspaceGitDirectory == null) {
			return null;
		}
		File[] files = workspaceGitDirectory.listFiles();
		for (File child : files) {
			if (child.isDirectory()) {
				File foundMaybe = checkSubfolder(child, repositoryName);
				if (foundMaybe != null) {
					return foundMaybe;
				}
			}
		}
		
		return null;
	}
	
	private static File checkSubfolder(File rootFolder, String repositoryName) {
		if (rootFolder.getName().startsWith(PREFIX_DOT)
				|| rootFolder.getName().equals(FOLDER_TARGET)) {
			return null;
		}
		if (rootFolder.getName().equals(repositoryName)) {
			File[] rootFiles = rootFolder.listFiles();
			for (File projectMaybe : rootFiles) {
				if (projectMaybe.isFile()) {
					if (projectMaybe.getName().equals(PROJECT_JSON)) {
						return projectMaybe.getParentFile();
					}
				} else {
					File foundMaybe = checkSubfolder(projectMaybe, repositoryName);
					if (foundMaybe != null) {
						return foundMaybe;
					}
				}
			}
		} else {
			File[] rootFiles = rootFolder.listFiles();
			for (File projectMaybe : rootFiles) {
				if (projectMaybe.isDirectory()) {
					File foundMaybe = checkSubfolder(projectMaybe, repositoryName);
					if (foundMaybe != null) {
						return foundMaybe;
					}
				}
			}
		}
		return null;
	}

	public static List<String> getGitRepositoryProjects(String user, String workspace, String repositoryName) {
		List<File> projects = getGitRepositoryProjectsFiles(user, workspace, repositoryName);
		return projects.stream().map(e -> e.getName()).collect(Collectors.toList());
	}

	public static List<File> getGitRepositoryProjectsFiles(String user, String workspace, String repositoryName) {
		File gitRepository = getGitDirectoryByRepositoryName(user, workspace, repositoryName);
		return getGitRepositoryProjects(gitRepository);
	}

    public static List<File> getGitRepositoryProjects(File gitRepository) {
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        ProjectsFinder projectsFinder = new ProjectsFinder();
        try {
            Files.walkFileTree(Paths.get(gitRepository.getAbsolutePath()), opts, Integer.MAX_VALUE, projectsFinder);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), (Throwable)e);
        }
        List<File> gitProjects = projectsFinder.getProjects();

        return gitProjects;
    }

    private static class ProjectsFinder extends SimpleFileVisitor<Path> {

    	private List<File> projects = new ArrayList<File>();

    	public List<File> getProjects() {
			return projects;
		}

    	@Override
    	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    		File file = path.toFile();
    		if (file.exists() && file.isFile() && file.getName().equals(PROJECT_METADATA_FILE_NAME)) {
    			projects.add(file.getParentFile());
    		}
    		return CONTINUE;
    	}

    	@Override
    	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    		logger.error(exc.getMessage(), exc);
    		return CONTINUE;
    	}
    }
    
	private static class Finder extends SimpleFileVisitor<Path> {

	    private final PathMatcher matcher;
	    private List<String> files = new ArrayList<String>();

	    Finder(String pattern) {
	        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	    }

	    void find(Path file) {
	        Path name = file.getFileName();
	        if (name != null && matcher.matches(name)) {
	        	files.add(file.toString());
	        }
	    }

	    void done() {
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
	        find(file);
	        return CONTINUE;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
	        find(dir);
	        return CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc) {
	        logger.error(exc.getMessage(), exc);
	        return CONTINUE;
	    }

	    public List<String> getFiles() {
			return files;
		}
	}

	public static final List<String> find(String root, String pattern) throws IOException {
		 Path startingDir = Paths.get(root);
		 Finder finder = new Finder(pattern);
		 EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
	     Files.walkFileTree(startingDir, opts, Integer.MAX_VALUE, finder);
	     finder.done();
	     return finder.getFiles();
	}
}
