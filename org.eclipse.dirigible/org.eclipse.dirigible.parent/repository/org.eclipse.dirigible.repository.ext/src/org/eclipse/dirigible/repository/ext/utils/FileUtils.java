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

package org.eclipse.dirigible.repository.ext.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IResource;

public class FileUtils {
	
	private static final String COULD_NOT_DELETE_TEMP_FILE_S = "Could not delete temp file: %s";
	private static final String COULD_NOT_CREATE_TEMP_DIRECTORY_S = "Could not create temp directory: %s";
	private static final String SLASH = "/"; //$NON-NLS-1$
	
	public static File createTempDirectory(String directory) throws IOException {
		String suffix = Long.toString(System.nanoTime());
		return createTempDirectory(directory, suffix);
	}
	
	public static File createTempDirectory(String directory, String suffix) throws IOException {
		final File temp = File.createTempFile(directory, suffix);
		if (!(temp.delete())) {
			throw new IOException(String.format(COULD_NOT_DELETE_TEMP_FILE_S, temp.getAbsolutePath()));
		}
		if (!(temp.mkdir())) {
			throw new IOException(String.format(COULD_NOT_CREATE_TEMP_DIRECTORY_S,
					temp.getAbsolutePath()));
		}
		return temp;
	}
	
	public static void copyCollectionToDirectory(ICollection source, File tempDirectory) 
			throws IOException {
		copyCollectionToDirectory(source, tempDirectory, new String[]{});
	}
	
	public static void copyCollectionToDirectory(ICollection source, File tempDirectory, String...roots)
			throws IOException {
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
				resourceDirectory.append(SLASH);
				
				String directoryPath = resourceDirectory.toString();
				for (int i = 0; i < roots.length; i++) {
					if (directoryPath.startsWith(roots[i])) {
						directoryPath = directoryPath.substring(roots[i].length());
						break;
					}
				}
				new File(tempDirectory, directoryPath).mkdirs();
				
				
				String resourcePath = entity.getPath();
				for (int i = 0; i < roots.length; i++) {
					if (resourcePath.startsWith(roots[i])) {
						resourcePath = resourcePath.substring(roots[i].length());
						break;
					}
				}
				InputStream in = new ByteArrayInputStream(((IResource) entity).getContent());
				File outputFile = new File(tempDirectory, resourcePath);

				FileOutputStream out = new FileOutputStream(outputFile);
				IOUtils.copy(in, out);

				in.close();
				out.flush();
				out.close();
			}
		}
	}

}
