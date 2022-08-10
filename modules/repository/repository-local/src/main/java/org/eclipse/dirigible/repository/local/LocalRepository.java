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
package org.eclipse.dirigible.repository.local;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based Local Repository implementation of {@link IRepository}.
 */
public class LocalRepository extends FileSystemRepository {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(LocalRepository.class);

	/** The Constant TYPE. */
	public static final String TYPE = "local";

	/** The Constant DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER. */
	public static final String DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER = "DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER_IS_ABSOLUTE. */
	public static final String DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER_IS_ABSOLUTE = "DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER_IS_ABSOLUTE"; //$NON-NLS-1$
	
	/** The Constant lastModified. */
	private static final AtomicLong lastModified = new AtomicLong(0);

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public LocalRepository() throws LocalRepositoryException {
		this(null);
	}

	/**
	 * Constructor with root folder parameter.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public LocalRepository(String rootFolder) throws LocalRepositoryException {
		super(rootFolder);
		lastModified.set(System.currentTimeMillis());
	}

	/**
	 * Constructor with root folder parameter.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @param absolute
	 *            whether the root folder is absolute
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public LocalRepository(String rootFolder, boolean absolute) throws LocalRepositoryException {
		super(rootFolder, absolute);
		lastModified.set(System.currentTimeMillis());
	}

	/**
	 * Initialize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-repository-local.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}
	
	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	@Override
	public long getLastModified() {
		return lastModified.get();
	}
	
	/**
	 * Sets the last modified.
	 *
	 * @param time the new last modified
	 */
	void setLastModified(long time) {
		lastModified.set(time);
	}

}
