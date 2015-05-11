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

package org.eclipse.dirigible.repository.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.IRepository;

/**
 * Utility class which imports all the content from a given zip
 * 
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
	public static void importZip(IRepository repository,
			ZipInputStream zipInputStream, String relativeRoot)
			throws IOException {
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
	public static void importZip(IRepository repository,
			ZipInputStream zipInputStream, String relativeRoot, boolean override)
			throws IOException {
		try {

			byte[] buffer = new byte[2048];

			ZipEntry entry;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String outpath = relativeRoot + IRepository.SEPARATOR + entry.getName();
				ByteArrayOutputStream output = null;
				try {
					output = new ByteArrayOutputStream();
					int len = 0;
					while ((len = zipInputStream.read(buffer)) > 0) {
						output.write(buffer, 0, len);
					}

					if (output.toByteArray().length > 0) {
						// TODO filter for binary extensions
						String mimeType = null;
						String extension = ContentTypeHelper.getExtension(entry
								.getName());
						if ((mimeType = ContentTypeHelper
								.getContentType(extension)) != null) {
							repository.createResource(outpath,
									output.toByteArray(),
									ContentTypeHelper.isBinary(mimeType),
									mimeType, override);
						} else {
							repository.createResource(outpath,
									output.toByteArray());
						}
					} else {
						if (outpath.endsWith(Messages.getString("ZipImporter.1"))) { //$NON-NLS-1$
							repository.createCollection(outpath);
						}
					}

				} finally {
					if (output != null)
						output.close();
				}
			}
		} finally {
			zipInputStream.close();
		}
	}

}
