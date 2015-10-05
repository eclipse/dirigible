/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.IRepository;

/**
 * Utility class which imports all the content from a given zip
 */
public class ZipImporter {

	/**
	 * Import all the content from a given zip to the target repository instance
	 * within the given path
	 *
	 * @param repository
	 * @param zipInputStream
	 * @param relativeRoot
	 * @throws IOException
	 */
	public static void importZip(IRepository repository, ZipInputStream zipInputStream, String relativeRoot) throws IOException {
		importZip(repository, zipInputStream, relativeRoot, false);
	}

	/**
	 * Import all the content from a given zip to the target repository instance
	 * within the given path, overrides files during the pass
	 *
	 * @param repository
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @throws IOException
	 */
	public static void importZip(IRepository repository, ZipInputStream zipInputStream, String relativeRoot, boolean override) throws IOException {
		importZip(repository, zipInputStream, relativeRoot, override, false);
	}

	/**
	 * Import all the content from a given zip to the target repository instance
	 * within the given path, overrides files during the pass and removes the root folder name
	 *
	 * @param repository
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @param excludeRootFolderName
	 * @throws IOException
	 */
	public static void importZip(IRepository repository, ZipInputStream zipInputStream, String relativeRoot, boolean override,
			boolean excludeRootFolderName) throws IOException {
		importZip(repository, zipInputStream, relativeRoot, override, excludeRootFolderName, null);
	}

	/**
	 * Import all the content from a given zip to the target repository instance
	 * within the given path, overrides files during the pass and removes the root folder name
	 *
	 * @param repository
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @param excludeRootFolderName
	 * @param filter
	 *            map of old/new string for replacement in paths
	 * @throws IOException
	 */
	public static void importZip(IRepository repository, ZipInputStream zipInputStream, String relativeRoot, boolean override,
			boolean excludeRootFolderName, Map<String, String> filter) throws IOException {
		try {
			ZipEntry entry;
			String parentFolder = null;
			while ((entry = zipInputStream.getNextEntry()) != null) {

				if (excludeRootFolderName && (parentFolder == null)) {
					parentFolder = entry.getName();
					continue;
				}

				String entryName = getEntryName(entry, parentFolder, excludeRootFolderName);

				String outpath = relativeRoot + IRepository.SEPARATOR + entryName;

				if (filter != null) {
					for (Map.Entry<String, String> forReplacement : filter.entrySet()) {
						outpath = outpath.replace(forReplacement.getKey(), forReplacement.getValue());
					}
				}

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				try {

					IOUtils.copy(zipInputStream, output);

					if (output.toByteArray().length > 0) {
						// TODO filter for binary extensions
						String mimeType = null;
						String extension = ContentTypeHelper.getExtension(entry.getName());
						if ((mimeType = ContentTypeHelper.getContentType(extension)) != null) {
							repository.createResource(outpath, output.toByteArray(), ContentTypeHelper.isBinary(mimeType), mimeType, override);
						} else {
							repository.createResource(outpath, output.toByteArray());
						}
					} else {
						if (outpath.endsWith(Messages.getString("ZipImporter.1"))) { //$NON-NLS-1$
							repository.createCollection(outpath);
						}
					}

				} finally {
					output.close();
				}
			}
		} finally {
			zipInputStream.close();
		}
	}

	private static String getEntryName(ZipEntry entry, String parentFolder, boolean excludeParentFolder) {
		return excludeParentFolder ? entry.getName().substring(parentFolder.length()) : entry.getName();
	}

}
