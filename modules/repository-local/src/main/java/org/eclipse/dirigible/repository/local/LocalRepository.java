/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based Local Repository implementation of {@link IRepository}
 */
public class LocalRepository extends FileSystemRepository {

	private static final Logger logger = LoggerFactory.getLogger(LocalRepository.class);

	public static final String TYPE = "local";
	public static final String DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER = "DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER"; //$NON-NLS-1$
	public static final String DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE = "DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE"; //$NON-NLS-1$

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @throws LocalRepositoryException in case the repository cannot be created
	 */
	public LocalRepository() throws LocalRepositoryException {
		this(null);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param rootFolder the root folder
	 * @throws LocalRepositoryException in case the repository cannot be created
	 */
	public LocalRepository(String rootFolder) throws LocalRepositoryException {
		super(rootFolder);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param rootFolder the reoot folder
	 * @param absolute whether the root folder is absolute
	 * @throws LocalRepositoryException in case the repository cannot be created
	 */
	public LocalRepository(String rootFolder, boolean absolute) throws LocalRepositoryException {
		super(rootFolder, absolute);
	}

	@Override
	public void initialize() {
		Configuration.load("/dirigible-repository-local.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

}
