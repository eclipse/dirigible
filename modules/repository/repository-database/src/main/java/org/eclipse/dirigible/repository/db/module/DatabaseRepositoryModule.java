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
package org.eclipse.dirigible.repository.db.module;

import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Database Repository instantiation and binding.
 */
public class DatabaseRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseRepositoryModule.class);

	private static final String MODULE_NAME = "Database Repository Module";
	private static final String DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_TYPE = "DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_TYPE";
	private static final String DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_NAME";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public void configure() {
		Configuration.loadModuleConfig("/dirigible-repository-database.properties");
		String repositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER);

		if (repositoryProvider == null) {
			throw new RuntimeException("No repository provider is configured, set the DIRIGIBLE_REPOSITORY_PROVIDER property. See more at: https://www.dirigible.io/help/setup_environment_variables.html");
		}

		if (DatabaseRepository.TYPE.equals(repositoryProvider)) {
			String dataSourceType = Configuration.get(DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_TYPE, IDatabase.DIRIGIBLE_DATABASE_PROVIDER_MANAGED);
			String dataSourceName = Configuration.get(DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_NAME, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
			DatabaseRepository databaseRepository = createInstance(dataSourceType, dataSourceName);
			StaticObjects.set(StaticObjects.DATABASE_REPOSITORY, databaseRepository);
			StaticObjects.set(StaticObjects.REPOSITORY, databaseRepository);
			logger.info("Bound Database Repository as the Repository for this instance.");
			
			logger.info("No master repository provider supported in case of a database repository setup.");
			String masterType = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);
			if (masterType == null) {
				StaticObjects.set(StaticObjects.MASTER_REPOSITORY, new DummyMasterRepository());
			}
		}
	}

	/**
	 * Creates the instance.
	 * @param dataSourceName 
	 * @param dataSourceType2 
	 *
	 * @return the repository
	 */
	private DatabaseRepository createInstance(String dataSourceType, String dataSourceName) {
		logger.debug("creating Database Repository...");
		logger.debug("Data source name [{}]", dataSourceName);
		DatabaseRepository databaseRepository = null;
		ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);
		for (IDatabase next : DATABASES) {
			if (dataSourceType.equals(next.getType())) {
				if (!StringUtils.isEmpty(dataSourceName)) {
					databaseRepository = new DatabaseRepository(next.getDataSource(dataSourceName));
				} else {
					databaseRepository = new DatabaseRepository(next.getDataSource());
				}
				break;
			}
		}
		logger.debug("Database Repository created.");
		return databaseRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_REPOSITORY;
	}
}
