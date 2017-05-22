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
	 * @param user
	 * @throws LocalBaseException
	 */
	public LocalRepository(String user) throws LocalBaseException {
		this(user, null);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user
	 * @param rootFolder
	 * @throws LocalBaseException
	 */
	public LocalRepository(String user, String rootFolder) throws LocalBaseException {
		super(user, rootFolder);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user
	 * @param rootFolder
	 * @param absolute
	 * @throws LocalBaseException
	 */
	public LocalRepository(String user, String rootFolder, boolean absolute) throws LocalBaseException {
		super(user, rootFolder, absolute);
	}

}
