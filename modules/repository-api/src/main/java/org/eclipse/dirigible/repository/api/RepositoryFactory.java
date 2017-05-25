/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class.getCanonicalName());
	
	private static ServiceLoader<IRepositoryProvider> repositoryProviders = ServiceLoader.load(IRepositoryProvider.class);

	/**
	 * Create a Repository instance used for local operations
	 *
	 * @param parameters
	 * @return local repository
	 * @throws RepositoryCreationException
	 */
	public IRepository createRepository() throws RepositoryException {
		
		String defaultRepositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER);
		if (defaultRepositoryProvider == null) {
			defaultRepositoryProvider = IRepository.DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL;
		}
		
		for (IRepositoryProvider repositoryProvider : repositoryProviders) {
			if (repositoryProvider.getType().equals(defaultRepositoryProvider)) {
				logger.info(String.format("Repository Provider used is: %s", repositoryProvider.getType()));
				IRepository repository = repositoryProvider.createRepository();
				repository.initialize();
				return repository;
			}
	    }

		throw new RepositoryCreationException(String.format("Repository Provider not found: %s", defaultRepositoryProvider));
	}

}
