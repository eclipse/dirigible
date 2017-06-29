/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

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
 * Module for managing Database instantiation and binding
 */
public class DatabaseModule extends AbstractDirigibleModule {
	
	private static final ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseModule.class);
	
	private static final String MODULE_NAME = "Database Module";
	
	@Override
	protected void configure() {
		Configuration.load("/dirigible-database.properties");
		
		String databaseProvider = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, IDatabase.DIRIGIBLE_DATABASE_PROVIDER_LOCAL);
		for (IDatabase next : DATABASES) {
			logger.info(format("Installing Database Provider [{0}] ...", next.getType()));
			if (next.getType().equals(databaseProvider)) {
				bind(IDatabase.class).toInstance(next);
				logger.info(format("Bound Database - [{0}].", next.getType()));
				try {
					logger.info(format("Creating Datasource - [{0}] ...", next.getType()));
					bind(DataSource.class).toInstance(next.getDataSource());
					logger.info(format("Done creating Datasource - [{0}].", next.getType()));
				} catch (Exception e) {
					logger.error(format("Failed creating Datasource - [{0}].", next.getType()), e);
				}
				logger.info(format("Bound Datasource - [{0}].", next.getType()));
			}
			logger.info(format("Done installing Database Provider [{0}].", next.getType()));
		}
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}
	
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

	public static List<String> getDatabaseTypes() {
		List<String> result = new ArrayList<String>();
		for (IDatabase next : DATABASES) {
			result.add(next.getType());
		}
		return result;
	}
	
	public static Set<String> getDataSources(String type) {
		for (IDatabase next : DATABASES) {
			if (next.getType().equals(type)) {
				return next.getDataSources().keySet();
			}
		}
		return null;
	}
	
}
