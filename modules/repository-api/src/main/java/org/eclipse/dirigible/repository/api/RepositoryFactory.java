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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class.getCanonicalName());

	private static IRepositoryProvider localRepositoryProvider;

	private static List<IRepositoryProvider> repositoryProviders = new ArrayList<IRepositoryProvider>();

	private static final String LOCAL_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.local.LocalRepositoryProvider";
//	private static final String DB_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.db.DBRepositoryProvider";

	static {
		repositoryProviders.add(createRepositoryProvider(LOCAL_REPOSITORY_PROVIDER));
//		repositoryProviders.add(createRepositoryProvider(DB_REPOSITORY_PROVIDER));
	}

	private RepositoryFactory () {
		//
	}

	/**
	 * Create a Repository instance used for local operations
	 *
	 * @param parameters
	 * @return local repository
	 * @throws RepositoryCreationException
	 */
	public static IRepository createRepository(Map<String, Object> parameters) throws RepositoryCreationException {
		String defaultRepositoryProvider = System.getProperty(ICommonConstants.INIT_PARAM_REPOSITORY_PROVIDER);
		if (defaultRepositoryProvider == null) {
			defaultRepositoryProvider = ICommonConstants.INIT_PARAM_REPOSITORY_PROVIDER_LOCAL;
		}

		for (IRepositoryProvider repositoryProvider : repositoryProviders) {
			if (repositoryProvider.getType().equals(defaultRepositoryProvider)) {
				logger.info(String.format("Repository Provider used is: %s", repositoryProvider.getType()));
				localRepositoryProvider = repositoryProvider;
			}
		}

		return localRepositoryProvider.createRepository(parameters);
	}

	private static IRepositoryProvider createRepositoryProvider(String className) {
		try {
			return (IRepositoryProvider) Class.forName(className).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e); 
		}
	}

}
