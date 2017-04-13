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

class RepositoryFactoryNonOSGi {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactoryNonOSGi.class.getCanonicalName());

	static IRepositoryProvider localRepositoryProvider;

	static IMasterRepositoryProvider masterRepositoryProvider;

	static List<IRepositoryProvider> repositoryProviders = new ArrayList<IRepositoryProvider>();

	static List<IMasterRepositoryProvider> masterRepositoryProviders = new ArrayList<IMasterRepositoryProvider>();

	static String localRepositoryProviderClass = "org.eclipse.dirigible.repository.local.LocalRepositoryProvider";
	static String dbRepositoryProviderClass = "org.eclipse.dirigible.repository.db.DBRepositoryProvider";

	static String dbMasterRepositoryProviderClass = "org.eclipse.dirigible.repository.db.DBMasterRepositoryProvider";
	static String filesystemMasterRepositoryProviderClass = "org.eclipse.dirigible.repository.local.FileSystemMasterRepositoryProvider";
	static String gitMasterRepositoryProviderClass = "org.eclipse.dirigible.repository.git.GitMasterRepositoryProvider";
	static String zipMasterRepositoryProviderClass = "org.eclipse.dirigible.repository.local.ZipMasterRepositoryProvider";

	static {
		repositoryProviders.add(createRepositoryProvider(localRepositoryProviderClass));
		repositoryProviders.add(createRepositoryProvider(dbRepositoryProviderClass));

		masterRepositoryProviders.add(createMasterRepositoryProvider(dbMasterRepositoryProviderClass));
		masterRepositoryProviders.add(createMasterRepositoryProvider(filesystemMasterRepositoryProviderClass));
		masterRepositoryProviders.add(createMasterRepositoryProvider(gitMasterRepositoryProviderClass));
		masterRepositoryProviders.add(createMasterRepositoryProvider(zipMasterRepositoryProviderClass));
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

		IRepository repository = localRepositoryProvider.createRepository(parameters);
		return repository;
	}

	/**
	 * Create a Master Repository from which the content can be synchronized as initial load or reset
	 *
	 * @param parameters
	 * @return master repository
	 * @throws RepositoryCreationException
	 */
	public static IMasterRepository createMasterRepository(Map<String, Object> parameters) throws RepositoryCreationException {

		String defaultMasterRepositoryProvider = System.getProperty(ICommonConstants.INIT_PARAM_REPOSITORY_PROVIDER_MASTER);

		for (IMasterRepositoryProvider repositoryProvider : masterRepositoryProviders) {
			if (repositoryProvider.getType().equals(defaultMasterRepositoryProvider)) {
				logger.info(String.format("Master Repository Provider used is: %s"), repositoryProvider.getType());
				masterRepositoryProvider = repositoryProvider;
			}
		}

		if (masterRepositoryProvider == null) {
			if (masterRepositoryProviders.size() == 0) {
				logger.info("Master Repository Provider has NOT been registered");
			}
			return null;
		}

		IMasterRepository masterRepository = masterRepositoryProvider.createRepository(parameters);
		return masterRepository;
	}

	private static IRepositoryProvider createRepositoryProvider(String clazz) {
		try {
			return (IRepositoryProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private static IMasterRepositoryProvider createMasterRepositoryProvider(String clazz) {
		try {
			return (IMasterRepositoryProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
