/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.api;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Database instantiation and binding.
 */
public class DatabaseModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseModule.class);

	private static final ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);

	private static final String MODULE_NAME = "Database Module";

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		Configuration.load("/dirigible-database.properties");

		String databaseProvider = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, IDatabase.DIRIGIBLE_DATABASE_PROVIDER_LOCAL);
		for (IDatabase next : DATABASES) {
			logger.trace(format("Installing Database Provider [{0}:{1}] ...", next.getType(), next.getName()));
			if (next.getType().equals(databaseProvider)) {
				bind(IDatabase.class).toInstance(next);
				logger.trace(format("Binding Database - [{0}:{1}].", next.getType(), next.getName()));
				try {
					logger.trace(format("Creating Datasource - [{0}:{1}] ...", next.getType(), next.getName()));
					bind(DataSource.class).toInstance(next.getDataSource());
					logger.info(format("Bound Datasource - [{0}:{1}].", next.getType(), next.getName()));
					logger.trace(format("Done creating Datasource - [{0}:{1}].", next.getType(), next.getName()));
				} catch (Exception e) {
					logger.error(format("Failed creating Datasource - [{0}:{1}].", next.getType(), next.getName()), e);
				}
				logger.trace(format("Done binding Datasource - [{0}:{1}].", next.getType(), next.getName()));
			}
			logger.trace(format("Done installing Database Provider [{0}:{1}].", next.getType(), next.getName()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

	/**
	 * Gets the data source.
	 *
	 * @param type
	 *            the type
	 * @param datasource
	 *            the datasource
	 * @return the data source
	 */
	public static DataSource getDataSource(String type, String datasource) {
		DataSource dataSource = null;
		for (IDatabase next : DATABASES) {
			if (next.getType().equals(type)) {
				if (datasource == null) {
					dataSource = next.getDataSource();
				} else {
					dataSource = next.getDataSource(datasource);
				}
				break;
			}
		}
		return dataSource;
	}

	/**
	 * Gets the database types.
	 *
	 * @return the database types
	 */
	public static List<String> getDatabaseTypes() {
		List<String> result = new ArrayList<String>();
		for (IDatabase next : DATABASES) {
			result.add(next.getType());
		}
		return result;
	}

	/**
	 * Gets the data sources.
	 *
	 * @param type
	 *            the type
	 * @return the data sources
	 */
	public static Set<String> getDataSources(String type) {
		for (IDatabase next : DATABASES) {
			if (next.getType().equals(type)) {
				return next.getDataSources().keySet();
			}
		}
		return null;
	}

}
