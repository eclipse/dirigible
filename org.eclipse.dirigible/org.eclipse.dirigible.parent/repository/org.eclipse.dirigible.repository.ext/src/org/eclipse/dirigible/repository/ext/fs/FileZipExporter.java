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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.dirigible.repository.api.IRepository;

public class FileZipExporter {

	public void zip(List<String> inputFolders, ZipOutputStream zipOutputStream) throws IOException {
		for (String inputFolder : inputFolders) {
			zip(inputFolder, zipOutputStream, true);
		}
	}

	protected String getMappedLocation(String inputFolder) throws IOException {
		return inputFolder;
	}

	public void zip(String inputFolder, ZipOutputStream zipOutputStream, boolean inclusive) throws IOException {

		String workspaceFolder = getMappedLocation(inputFolder);

		File inputFile = new File(workspaceFolder);

		if (inputFile.isFile()) {
			zipFile(inputFile, "", zipOutputStream, inclusive);
		} else if (inputFile.isDirectory()) {
			zipFolder(zipOutputStream, inputFile, "", inclusive);
		}
	}

	private void zipFolder(ZipOutputStream zipOutputStream, File inputFolder, String parentName, boolean inclusive) throws IOException {

		String folderName = parentName + inputFolder.getName() + File.separator;

		String zipFolderName = inclusive ? folderName : folderName.substring(folderName.indexOf(IRepository.SEPARATOR) + 1);

		ZipEntry folderZipEntry = new ZipEntry(zipFolderName);
		zipOutputStream.putNextEntry(folderZipEntry);

		File[] contents = inputFolder.listFiles();

		for (File f : contents) {
			if (f.isFile()) {
				zipFile(f, folderName, zipOutputStream, inclusive);
			} else if (f.isDirectory()) {
				zipFolder(zipOutputStream, f, folderName, inclusive);
			}
		}
		zipOutputStream.closeEntry();
	}

	private void zipFile(File inputFile, String parentName, ZipOutputStream zipOutputStream, boolean inclusive) throws IOException {

		String fileName = parentName + inputFile.getName();

		String zipFileName = inclusive ? fileName : fileName.substring(fileName.indexOf(IRepository.SEPARATOR) + 1);

		ZipEntry zipEntry = new ZipEntry(zipFileName);
		zipOutputStream.putNextEntry(zipEntry);

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(inputFile);
			byte[] buf = new byte[1024];
			int bytesRead;

			while ((bytesRead = fileInputStream.read(buf)) > 0) {
				zipOutputStream.write(buf, 0, bytesRead);
			}

			zipOutputStream.closeEntry();
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}

	}

}
