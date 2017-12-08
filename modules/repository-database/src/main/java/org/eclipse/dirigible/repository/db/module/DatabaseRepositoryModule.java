/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.db.module;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
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

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-database.properties");
		String repositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER, IRepository.DIRIGIBLE_REPOSITORY_PROVIDER_DATABASE);

		DatabaseRepository databaseRepository = createInstance();
		bind(DatabaseRepository.class).toInstance(databaseRepository);
		if (DatabaseRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(databaseRepository);
			logger.info("Bound Database Repository as the Repository for this instance.");
		}
	}

	/**
	 * Creates the instance.
	 *
	 * @return the repository
	 */
	private DatabaseRepository createInstance() {
		logger.debug("creating Database Repository...");
		DatabaseRepository databaseRepository = null;
		ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);
		for (IDatabase next : DATABASES) {
			if (IDatabase.DIRIGIBLE_DATABASE_PROVIDER_MANAGED.equals(next.getType())) {
				databaseRepository = new DatabaseRepository(next.getDataSource());
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
}
