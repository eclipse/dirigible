/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.jar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.local.LocalBaseException;
import org.eclipse.dirigible.repository.master.zip.ZipRepository;

public class JarRepository extends ZipRepository {

	private String jarRepositoryRootFolder;

	public JarRepository(String user, String zip) throws LocalBaseException {

		InputStream in = JarRepository.class.getClassLoader().getSystemResourceAsStream(zip);
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
				String zipFileName = zip.substring(zip.lastIndexOf(ICommonConstants.SEPARATOR) + 1);
				jarRepositoryRootFolder = zipFileName.substring(0, zipFileName.lastIndexOf(ICommonConstants.DOT));
				createRepository(user, rootFolder.toString(), true);
			} catch (IOException e) {
				throw new LocalBaseException(e);
			}
		} else {
			throw new LocalBaseException(String.format("Zip file containing Repository content does not exist at path: %s", zip));
		}
	}

	// disable usage
	protected JarRepository(String user, String rootFolder, boolean absolute) throws LocalBaseException {
		super(user, rootFolder, absolute);
	}

	// disable usage
	protected JarRepository(String user) throws LocalBaseException {
		super(user);
	}

	// disable usage
	protected JarRepository() throws LocalBaseException {
		super();
	}

	@Override
	protected String getRepositoryRootFolder() {
		return this.jarRepositoryRootFolder;
	}
}
