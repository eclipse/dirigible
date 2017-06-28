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

import org.eclipse.dirigible.repository.api.IRepository;

/**
 * The File System based Local Repository implementation of {@link IRepository}
 */
public class LocalRepository extends FileSystemRepository {

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param user the user
	 * @throws LocalBaseException Local Repository Exception
	 */
	public LocalRepository(String user) throws LocalBaseException {
		super(user);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user the user
	 * @param rootFolder the root folder
	 * @throws LocalBaseException Local Repository Exception
	 */
	public LocalRepository(String user, String rootFolder) throws LocalBaseException {
		super(user, rootFolder);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user the user
	 * @param rootFolder the root folder
	 * @param absolute whether it is an absolute path
	 * @throws LocalBaseException Local Repository Exception
	 */
	public LocalRepository(String user, String rootFolder, boolean absolute) throws LocalBaseException {
		super(user, rootFolder, absolute);
	}

	public LocalRepository(String user, String rootFolder, String repositoryRootFolderName, boolean absolute) throws LocalBaseException {
		super(user, rootFolder, repositoryRootFolderName, absolute);
	}
	
}
