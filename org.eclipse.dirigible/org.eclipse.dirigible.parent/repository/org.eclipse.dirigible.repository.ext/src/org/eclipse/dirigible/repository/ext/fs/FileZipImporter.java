/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileZipImporter {

	public void unzip(String destinationFolder, ZipInputStream zipInput, Map<String, String> filter) throws IOException {
		String workspaceFolder = getMappedLocation(destinationFolder);
		File directory = new File(workspaceFolder);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		byte[] buffer = new byte[2048];

		try {
			ZipEntry entry = zipInput.getNextEntry();

			while (entry != null) {
				String entryName = entry.getName();
				File file = new File(workspaceFolder + File.separator + entryName);

				String outpath = file.getCanonicalPath();

				if (filter != null) {
					for (Map.Entry<String, String> forReplacement : filter.entrySet()) {
						outpath = outpath.replace(forReplacement.getKey(), forReplacement.getValue());
					}
				}

				if (entry.isDirectory()) {
					File newDir = new File(outpath);
					if (!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if (success == false) {
							System.out.println("Problem creating Folder");
						}
					}
				} else {
					FileSystemUtils.createFoldersIfNecessary(outpath);
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}

			zipInput.closeEntry();

			zipInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getMappedLocation(String destinationFolder) throws IOException {
		return destinationFolder;
	}

}
