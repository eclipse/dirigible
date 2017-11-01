/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);

	public static void saveFile(String workspacePath, byte[] content) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePath);
		Path path = FileSystems.getDefault().getPath(workspacePath);
		if (content == null) {
			content = new byte[] {};
		}
		Files.write(path, content);
	}

	public static byte[] loadFile(String workspacePath) throws FileNotFoundException, IOException {
		Path path = FileSystems.getDefault().getPath(workspacePath);
		// if (Files.exists(path)) {
		if (path.toFile().exists()) {
			return Files.readAllBytes(path);
		}
		return null;
	}

	public static void moveFile(String workspacePathOld, String workspacePathNew) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePathNew);
		Path pathOld = FileSystems.getDefault().getPath(workspacePathOld);
		Path pathNew = FileSystems.getDefault().getPath(workspacePathNew);
		Files.move(pathOld, pathNew);
	}

	public static void copyFile(String srcPath, String destPath) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(destPath);
		Path srcFile = FileSystems.getDefault().getPath(srcPath);
		Path destFile = FileSystems.getDefault().getPath(destPath);
		FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
	}

	public static void copyFolder(String srcPath, String destPath) throws FileNotFoundException, IOException {
		createFoldersIfNecessary(destPath);
		Path srcDir = FileSystems.getDefault().getPath(srcPath);
		Path destDir = FileSystems.getDefault().getPath(destPath);
		FileUtils.copyDirectory(srcDir.toFile(), destDir.toFile(), true);
	}

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

	public static boolean createFolder(String workspacePath) {
		File folder = new File(workspacePath);
		if (!folder.exists()) {
			return folder.mkdirs();
		}
		return true;
	}

	public static boolean createFile(String workspacePath) throws IOException {
		createFoldersIfNecessary(workspacePath);
		File file = new File(workspacePath);
		if (!file.exists()) {
			return file.createNewFile();
		}
		return true;
	}

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

	public static String getOwner(String workspacePath) throws IOException {
		String convertedPath = convertToWorkspacePath(workspacePath);
		Path path = FileSystems.getDefault().getPath(convertedPath);
		if (new File(convertedPath).exists()) {
			return Files.getOwner(path).getName();
		} else {
			return "none";
		}
	}

	public static Date getModifiedAt(String workspacePath) throws IOException {
		String convertedPath = convertToWorkspacePath(workspacePath);
		Path path = FileSystems.getDefault().getPath(convertedPath);
		if (new File(convertedPath).exists()) {
			return new Date(Files.getLastModifiedTime(path).toMillis());
		} else {
			return new Date();
		}
	}

	public static void createFoldersIfNecessary(String workspacePath) {
		int lastIndexOf = workspacePath.lastIndexOf(File.separator);
		if (lastIndexOf > 0) {
			String directory = workspacePath.substring(0, lastIndexOf);
			createFolder(directory);
		}
	}

	private static String convertToWorkspacePath(String path) {
		String workspacePath = null;
		if (path.startsWith(IRepository.SEPARATOR)) {
			workspacePath = path.substring(IRepository.SEPARATOR.length());
		} else {
			workspacePath = path;
		}
		workspacePath = workspacePath.replace(IRepository.SEPARATOR, File.separator);
		return workspacePath;
	}

	public static boolean exists(String location) {
		if ((location == null) || "".equals(location)) {
			return false;
		}
		Path path;
		try {
			path = FileSystems.getDefault().getPath(location);
		} catch (java.nio.file.InvalidPathException e) {
			return false;
		}
		return path.toFile().exists();
		// return Files.exists(path);
	}

	public static boolean directoryExists(String location) {
		return exists(location) && isDirectory(location);
	}

	public static boolean fileExists(String location) {
		return exists(location) && !isDirectory(location);
	}

	public static boolean isDirectory(String location) {
		Path path;
		try {
			path = FileSystems.getDefault().getPath(location);
		} catch (java.nio.file.InvalidPathException e) {
			return false;
		}
		return path.toFile().isDirectory();
		// return Files.isDirectory(path);
	}
}
