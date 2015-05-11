/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.rcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class FileUtils {

	public static void saveFile(String workspacePath, byte[] content)
			throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePath);
		Path path = FileSystems.getDefault().getPath(workspacePath);
		Files.write(path, content);
	}
	
	public static byte[] loadFile(String workspacePath)
			throws FileNotFoundException, IOException {
		Path path = FileSystems.getDefault().getPath(workspacePath);
		return Files.readAllBytes(path);
	}
	
	public static void moveFile(String workspacePathOld, String workspacePathNew)
			throws FileNotFoundException, IOException {
		createFoldersIfNecessary(workspacePathNew);
		Path pathOld = FileSystems.getDefault().getPath(workspacePathOld);
		Path pathNew = FileSystems.getDefault().getPath(workspacePathNew);
		Files.move(pathOld, pathNew);
	}

	public static void removeFile(String workspacePath)
			throws FileNotFoundException, IOException {
		Path path = FileSystems.getDefault().getPath(workspacePath);
		Files.delete(path);
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

	    if (i > 0 &&  i < s.length() - 1) {
	        ext = s.substring(i+1).toLowerCase();
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
		if (path.startsWith(IRepositoryPaths.SEPARATOR)) {
			workspacePath = path.substring(IRepositoryPaths.SEPARATOR.length());
		} else {
			workspacePath = path;
		}
		workspacePath = workspacePath.replace(IRepositoryPaths.SEPARATOR, File.separator);
		return workspacePath;
	}

	public static boolean exists(String repositoryName) {
		if (repositoryName == null
				|| "".equals(repositoryName)) {
			return false;
		}
		Path path;
		try {
			path = FileSystems.getDefault().getPath(repositoryName);
		} catch (java.nio.file.InvalidPathException e) {
			return false;
		}
		return Files.exists(path);
	}
}