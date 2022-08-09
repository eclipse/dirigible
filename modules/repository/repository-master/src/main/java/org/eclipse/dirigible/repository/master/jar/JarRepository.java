/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.master.jar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.master.zip.ZipRepository;

/**
 * The Jar Repository.
 */
public class JarRepository extends ZipRepository {

	/** The jar repository root folder. */
	private String jarRepositoryRootFolder;

	/**
	 * Instantiates a new jar repository.
	 *
	 * @param zip            the zip
	 * @throws LocalRepositoryException             the local repository exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JarRepository(String zip) throws LocalRepositoryException, IOException {

		InputStream in = JarRepository.class.getClassLoader().getSystemResourceAsStream(zip);
		try {
			if (in == null) {
				in = JarRepository.class.getClassLoader().getParent().getResourceAsStream(zip);
			}
			if (in == null) {
				in = JarRepository.class.getResourceAsStream(zip);
			}
			if (in != null) {
				try {
					Path rootFolder = Files.createTempDirectory("jar_repository");
					unpackZip(in, rootFolder.toString());
					String zipFileName = zip.substring(zip.lastIndexOf(IRepository.SEPARATOR) + 1);
					jarRepositoryRootFolder = zipFileName.substring(0, zipFileName.lastIndexOf("."));
					createRepository(rootFolder.toString(), true);
				} catch (IOException e) {
					throw new LocalRepositoryException(e);
				}
			} else {
				throw new LocalRepositoryException(
						String.format("Zip file containing Repository content does not exist at path: %s", zip));
			} 
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Instantiates a new jar repository.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @param absolute
	 *            the absolute
	 * @throws LocalRepositoryException
	 *             the local repository exception
	 */
	// disable usage
	protected JarRepository(String rootFolder, boolean absolute) throws LocalRepositoryException {
		super(rootFolder, absolute);
	}

	/**
	 * Instantiates a new jar repository.
	 *
	 * @throws LocalRepositoryException
	 *             the local repository exception
	 */
	// disable usage
	protected JarRepository() throws LocalRepositoryException {
		super();
	}

	/**
	 * Gets the repository root folder.
	 *
	 * @return the repository root folder
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.master.zip.ZipRepository#getRepositoryRootFolder()
	 */
	@Override
	protected String getRepositoryRootFolder() {
		return this.jarRepositoryRootFolder;
	}
}
