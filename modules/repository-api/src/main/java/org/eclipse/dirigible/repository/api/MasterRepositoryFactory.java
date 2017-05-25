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

public class MasterRepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(MasterRepositoryFactory.class.getCanonicalName());

	private static IMasterRepositoryProvider masterRepositoryProvider;

	private static List<IMasterRepositoryProvider> masterRepositoryProviders = new ArrayList<IMasterRepositoryProvider>();

	private static final String DB_MASTER_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.db.DBMasterRepositoryProvider";
	private static final String FS_MASTER_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.local.FileSystemMasterRepositoryProvider";
	private static final String GIT_MASTER_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.git.GitMasterRepositoryProvider";
	private static final String ZIP_MASTER_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.local.ZipMasterRepositoryProvider";
	private static final String JAR_MASTER_REPOSITORY_PROVIDER = "org.eclipse.dirigible.repository.local.JarMasterRepositoryProvider";

	static {
		masterRepositoryProviders.add(createMasterRepositoryProvider(DB_MASTER_REPOSITORY_PROVIDER));
		masterRepositoryProviders.add(createMasterRepositoryProvider(FS_MASTER_REPOSITORY_PROVIDER));
		masterRepositoryProviders.add(createMasterRepositoryProvider(GIT_MASTER_REPOSITORY_PROVIDER));
		masterRepositoryProviders.add(createMasterRepositoryProvider(ZIP_MASTER_REPOSITORY_PROVIDER));
		masterRepositoryProviders.add(createMasterRepositoryProvider(JAR_MASTER_REPOSITORY_PROVIDER));
	}

	private MasterRepositoryFactory () {
		//
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

		return masterRepositoryProvider.createRepository(parameters);
	}

	private static IMasterRepositoryProvider createMasterRepositoryProvider(String className) {
		try {
			return (IMasterRepositoryProvider) Class.forName(className).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
