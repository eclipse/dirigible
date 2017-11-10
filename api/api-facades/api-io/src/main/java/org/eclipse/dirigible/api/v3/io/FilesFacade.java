/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class FilesFacade.
 */
public class FilesFacade {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(FilesFacade.class);
	
	/**
	 * Exists.
	 *
	 * @param path the path
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean exists(String path) throws IOException {
//		return Files.exists(Paths.get(path));
		return Paths.get(path).toFile().exists();
	}
	
	/**
	 * Checks if is executable.
	 *
	 * @param path the path
	 * @return true, if is executable
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isExecutable(String path) throws IOException {
		return Files.isExecutable(Paths.get(path));
	}
	
	/**
	 * Checks if is readable.
	 *
	 * @param path the path
	 * @return true, if is readable
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isReadable(String path) throws IOException {
		return Files.isReadable(Paths.get(path));
	}
	
	/**
	 * Checks if is writable.
	 *
	 * @param path the path
	 * @return true, if is writable
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isWritable(String path) throws IOException {
		return Files.isReadable(Paths.get(path));
	}
	
	/**
	 * Checks if is hidden.
	 *
	 * @param path the path
	 * @return true, if is hidden
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isHidden(String path) throws IOException {
		return Files.isHidden(Paths.get(path));
	}
	
	/**
	 * Checks if is directory.
	 *
	 * @param path the path
	 * @return true, if is directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isDirectory(String path) throws IOException {
		return Files.isDirectory(Paths.get(path));
	}
	
	/**
	 * Checks if is file.
	 *
	 * @param path the path
	 * @return true, if is file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isFile(String path) throws IOException {
		return Files.isRegularFile(Paths.get(path));
	}
	
	/**
	 * Checks if is same file.
	 *
	 * @param path1 the path 1
	 * @param path2 the path 2
	 * @return true, if is same file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final boolean isSameFile(String path1, String path2) throws IOException {
		return Files.isSameFile(Paths.get(path1), Paths.get(path2));
	}
	
	/**
	 * Gets the canonical path.
	 *
	 * @param path the path
	 * @return the canonical path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getCanonicalPath(String path) throws IOException {
		return new File(path).getCanonicalPath();
	}
	
	/**
	 * Gets the name.
	 *
	 * @param path the path
	 * @return the name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getName(String path) throws IOException {
		return new File(path).getName();
	}
	
	/**
	 * Gets the parent path.
	 *
	 * @param path the path
	 * @return the parent path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getParentPath(String path) throws IOException {
		return new File(path).getParentFile().getPath();
	}
	
	/**
	 * Read bytes.
	 *
	 * @param path the path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final byte[] readBytes(String path) throws IOException {
		return Files.readAllBytes(Paths.get(path));
	}
	
	/**
	 * Read text.
	 *
	 * @param path the path
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String readText(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}
	
	/**
	 * Write bytes.
	 *
	 * @param path the path
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void writeBytes(String path, String input) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		Files.write(Paths.get(path), bytes);
	}
	
	/**
	 * Write text.
	 *
	 * @param path the path
	 * @param text the text
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void writeText(String path, String text) throws IOException {
		Files.write(Paths.get(path), text.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Gets the last modified.
	 *
	 * @param path the path
	 * @return the last modified
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final long getLastModified(String path) throws IOException {
		return Files.getLastModifiedTime(Paths.get(path)).toMillis();
	}
	
	/**
	 * Sets the last modified.
	 *
	 * @param path the path
	 * @param time the time
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void setLastModified(String path, long time) throws IOException {
		Files.setLastModifiedTime(Paths.get(path), FileTime.fromMillis(time));
	}
	
	/**
	 * Sets the last modified.
	 *
	 * @param path the path
	 * @param time the time
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void setLastModified(String path, Double time) throws IOException {
		setLastModified(path, time.longValue());
	}
	
	/**
	 * Gets the owner.
	 *
	 * @param path the path
	 * @return the owner
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getOwner(String path) throws IOException {
		return Files.getOwner(Paths.get(path)).getName();
	}
	
	/**
	 * Sets the owner.
	 *
	 * @param path the path
	 * @param owner the owner
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void setOwner(String path, String owner) throws IOException {
		UserPrincipal userPrincipal = Paths.get(path).getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(owner);
		Files.setOwner(Paths.get(path), userPrincipal);
	}
	
	/**
	 * Gets the permissions.
	 *
	 * @param path the path
	 * @return the permissions
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getPermissions(String path) throws IOException {
		return PosixFilePermissions.toString(Files.getPosixFilePermissions(Paths.get(path)));
	}
	
	/**
	 * Sets the permissions.
	 *
	 * @param path the path
	 * @param permissions the permissions
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void setPermissions(String path, String permissions) throws IOException {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
	    Files.setPosixFilePermissions(Paths.get(path), perms);
	}
	
	/**
	 * Size.
	 *
	 * @param path the path
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final long size(String path) throws IOException {
		return Files.size(Paths.get(path));
	}
	
	/**
	 * Creates the file.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void createFile(String path) throws IOException {
		Files.createFile(Paths.get(path));
	}
	
	/**
	 * Creates the directory.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void createDirectory(String path) throws IOException {
		Files.createDirectories(Paths.get(path));
	}
	
	/**
	 * Copy.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void copy(String source, String target) throws IOException {
		Path sourcePath = Paths.get(source);
		Path targetPath = Paths.get(target);
		Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
				Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	/**
	 * Move.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void move(String source, String target) throws IOException {
		Files.move(Paths.get(source), Paths.get(target));
	}
	
	/**
	 * Delete file.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void deleteFile(String path) throws IOException {
		Files.deleteIfExists(Paths.get(path));
	}
	
	/**
	 * Delete directory.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void deleteDirectory(String path) throws IOException {
		Files.deleteIfExists(Paths.get(path));
	}
	
	/**
	 * Delete directory.
	 *
	 * @param path the path
	 * @param forced the forced
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void deleteDirectory(String path, boolean forced) throws IOException {
		if (forced) {
			Files.walkFileTree(Paths.get(path), new FileVisitor<Path>() {
	
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	
//					if (Files.exists(dir)) {
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
//					if (Files.exists(file)) {
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
		} else {
			Files.deleteIfExists(Paths.get(path));
		}
	}
	
	/**
	 * Creates the temp file.
	 *
	 * @param prefix the prefix
	 * @param suffix the suffix
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String createTempFile(String prefix, String suffix) throws IOException {
		return Files.createTempFile(prefix, suffix).toString();
	}
	
	/**
	 * Creates the temp directory.
	 *
	 * @param prefix the prefix
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String createTempDirectory(String prefix) throws IOException {
		return Files.createTempDirectory(prefix).toString();
	}
	
	
	/**
	 * Creates the input stream.
	 *
	 * @param path the path
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final InputStream createInputStream(String path) throws IOException {
		return Files.newInputStream(Paths.get(path));
	}
	
	/**
	 * Creates the output stream.
	 *
	 * @param path the path
	 * @return the output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final OutputStream createOutputStream(String path) throws IOException {
		return Files.newOutputStream(Paths.get(path));
	}

}
