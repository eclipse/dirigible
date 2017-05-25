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
import org.eclipse.dirigible.repository.api.IRepositoryProvider;

/**
 * Local Repository Provider for simple File System based Repository instance
 *
 */
public class LocalRepositoryProvider implements IRepositoryProvider {

	public static final String TYPE = "local";
	
	/**
	 * Getter for the type of the repository
	 */
	@Override
	public String getType() {
		return TYPE;
	}
	
	/**
	 * Creates the Repository instance of this type
	 */
	@Override
	public IRepository createRepository() {
		
		String rootFolder = Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE));

		return new LocalRepository(rootFolder, absolute);
	}

}
