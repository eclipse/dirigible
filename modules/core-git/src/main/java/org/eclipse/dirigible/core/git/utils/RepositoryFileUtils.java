/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * The file system to repository transformation utility
 */
public class RepositoryFileUtils {

	/**
	 * Creates the temp directory.
	 *
	 * @param directory
	 *            the directory
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createTempDirectory(String directory) throws IOException {
		String suffix = Long.toString(System.nanoTime());
		return createTempDirectory(directory, suffix);
	}

	/**
	 * Creates the temp directory.
	 *
	 * @param directory
	 *            the directory
	 * @param suffix
	 *            the suffix
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createTempDirectory(String directory, String suffix) throws IOException {
		final File temp = File.createTempFile(directory, suffix);
		if (!(temp.delete())) {
			throw new IOException(String.format("Could not delete temp file: %s", temp.getAbsolutePath()));
		}
		if (!(temp.mkdir())) {
			throw new IOException(String.format("Could not create temp directory: %s", temp.getAbsolutePath()));
		}
		return temp;
	}

	/**
	 * Copy collection to directory.
	 *
	 * @param source
	 *            the source
	 * @param tempDirectory
	 *            the temp directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyCollectionToDirectory(ICollection source, File tempDirectory) throws IOException {
		copyCollectionToDirectory(source, tempDirectory, new String[] {});
	}

	/**
	 * Copy collection to directory.
	 *
	 * @param source
	 *            the source
	 * @param tempDirectory
	 *            the temp directory
	 * @param roots
	 *            the roots
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyCollectionToDirectory(ICollection source, File tempDirectory, String... roots) throws IOException {
		if (!source.exists()) {
			return;
		}
		for (IEntity entity : source.getChildren()) {
			if (entity instanceof ICollection) {
				copyCollectionToDirectory((ICollection) entity, tempDirectory, roots);

			}
			if (entity instanceof IResource) {
				String path = entity.getParent().getPath();
				StringBuilder resourceDirectory = new StringBuilder();
				resourceDirectory.append(path);
				resourceDirectory.append(File.separator);

				String directoryPath = resourceDirectory.toString();
				for (String root : roots) {
					if (directoryPath.startsWith(root)) {
						directoryPath = directoryPath.substring(root.length());
						break;
					}
				}
				File baseDirectory = new File(tempDirectory, directoryPath);
				boolean dirsCreated = baseDirectory.mkdirs();
				if (!dirsCreated) {
					throw new IOException("Error in creating directories for the file: " + baseDirectory.getCanonicalPath());
				}

				String resourcePath = entity.getPath();
				for (String root : roots) {
					if (resourcePath.startsWith(root)) {
						resourcePath = resourcePath.substring(root.length());
						break;
					}
				}

				InputStream in = null;
				FileOutputStream out = null;
				try {
					in = new ByteArrayInputStream(((IResource) entity).getContent());
					File outputFile = new File(tempDirectory, resourcePath);

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
		}
	}

}
