/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.IRepositoryConstants;
import org.eclipse.dirigible.repository.api.RepositoryInitializationException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;

public class ZipRepository extends FileSystemRepository {

	private String zipRepositoryRootFolder;

	public ZipRepository(String zip) throws LocalRepositoryException {

		File zipFile = new File(zip);
		if (zipFile.exists()) {
			try {
				Path rootFolder = Files.createTempDirectory("zip_repository");
				unpackZip(new FileInputStream(zip), rootFolder.toString());
				String zipFileName = zipFile.getName();
				zipRepositoryRootFolder = zipFileName.substring(0, zipFileName.lastIndexOf(IRepositoryConstants.DOT));
				createRepository(rootFolder.toString(), true);
			} catch (IOException e) {
				throw new LocalRepositoryException(e);
			}
		} else {
			throw new LocalRepositoryException(String.format("Zip file containing Repository content does not exist at path: %s", zip));
		}
	}

	protected void unpackZip(InputStream zip, String folder) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(zip);
		try {
			ZipEntry entry;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				File entryDestination = new File(folder, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(zipInputStream, out);
					out.close();
				}
			}
		} finally {
			zipInputStream.close();
		}
	}

	// disable usage
	protected ZipRepository(String rootFolder, boolean absolute) throws LocalRepositoryException {
		super(rootFolder, absolute);
	}

	// disable usage
	protected ZipRepository() throws LocalRepositoryException {
		super();
	}

	@Override
	protected String getRepositoryRootFolder() {
		return this.zipRepositoryRootFolder;
	}

	@Override
	public void initialize() throws RepositoryInitializationException {
		// TODO Auto-generated method stub
		
	}
}
