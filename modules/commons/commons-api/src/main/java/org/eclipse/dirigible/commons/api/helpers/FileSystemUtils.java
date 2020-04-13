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
package org.eclipse.dirigible.commons.api.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System Utils.
 */
public class FileSystemUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);
	private static final String SEPARATOR = "/";

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
		Path path = FileSystems.getDefault().getPath(workspacePath);
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
		Path srcFile = FileSystems.getDefault().getPath(srcPath);
		Path destFile = FileSystems.getDefault().getPath(destPath);
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
		Path srcDir = FileSystems.getDefault().getPath(srcPath);
		Path destDir = FileSystems.getDefault().getPath(destPath);
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

		Files.walkFileTree(path, new FileVisitor<Path>() {

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

		if ((i > 0) && (i < (s.length() - 1))) {
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
		int lastIndexOf = workspacePath.lastIndexOf(File.separator);
		if (lastIndexOf > 0) {
			String directory = workspacePath.substring(0, lastIndexOf);
			createFolder(directory);
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
		String workspacePath = null;
		if (path.startsWith(SEPARATOR)) {
			workspacePath = path.substring(SEPARATOR.length());
		} else {
			workspacePath = path;
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
			path = FileSystems.getDefault().getPath(FilenameUtils.normalize(location));
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
			path = FileSystems.getDefault().getPath(FilenameUtils.normalize(location));
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
		Path[] paths =  Files.list(link).toArray(size -> new Path[size]);
		File[] files = new File[paths.length];
		for (int i=0; i<paths.length; i++) {
			files[i] = paths[i].toFile();
		}
		return files;
	}
}
