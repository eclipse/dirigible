/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.fs;

import org.eclipse.dirigible.repository.api.RepositoryInitializationException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.master.IMasterRepository;

public class FileSystemMasterRepository extends FileSystemRepository implements IMasterRepository {

	public static final String TYPE = "filesystem";
	public static final String DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER = "DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER";

	private static final String DIRIGIBLE_MASTER_ROOT_FOLDER_DEFAULT = "dirigible_master";

	public FileSystemMasterRepository(String rootFolder) throws LocalRepositoryException {
		super(rootFolder);
	}

	public FileSystemMasterRepository() throws LocalRepositoryException {
		super();
	}

	@Override
	protected String getRepositoryRootFolder() {
		return DIRIGIBLE_MASTER_ROOT_FOLDER_DEFAULT;
	}

	@Override
	public void initialize() throws RepositoryInitializationException {
		// TODO Auto-generated method stub

	}

}
