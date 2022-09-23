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
package org.eclipse.dirigible.database.api;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Database instantiation and binding.
 */
public class DatabaseModule extends AbstractDirigibleModule {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseModule.class);

	/** The Constant DATABASES. */
	private static final ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);

	/** The Constant MODULE_NAME. */
	private static final String MODULE_NAME = "Database Module";

	/**
	 * Configure.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#configure()
	 */
	@Override
	public void configure() {
		Configuration.loadModuleConfig("/dirigible-database.properties");

		boolean databaseProviderIsSelected = false;
		String databaseProvider = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_PROVIDER);
		String dataSourceName = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT,
				IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
		String systemDataSourceName = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM,
				IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
		if (databaseProvider != null) {
			databaseProviderIsSelected = true;
		} else {
			databaseProvider = IDatabase.DIRIGIBLE_DATABASE_PROVIDER_LOCAL;
		}
		
		for (IDatabase next : DATABASES) {
			if (logger.isTraceEnabled()) {logger.trace(format("Installing System Database Provider [{0}:{1}] ...", next.getType(), next.getName()));}
			if (next.getType().equals(IDatabase.DIRIGIBLE_DATABASE_PROVIDER_LOCAL)) {
				bindSystemDatasource(next, systemDataSourceName);
			}
			if (logger.isTraceEnabled()) {logger.trace(format("Done installing System Database Provider [{0}:{1}].", next.getType(), next.getName()));}
		}
		
		for (IDatabase next : DATABASES) {
			if (logger.isTraceEnabled()) {logger.trace(format("Installing Database Provider [{0}:{1}] ...", next.getType(), next.getName()));}
			if (databaseProviderIsSelected && next.getType().equals(databaseProvider)) {
				// bind the selected if any
				bindDatasource(next, dataSourceName);
				break;
			} else if (!databaseProviderIsSelected) {
				// bind the first present, because there is no selected one
				bindDatasource(next, dataSourceName);
				break;
			}
			if (logger.isTraceEnabled()) {logger.trace(format("Done installing Database Provider [{0}:{1}].", next.getType(), next.getName()));}
		}
	}
	
	/**
	 * Bind system datasource.
	 *
	 * @param next the next
	 * @param dataSourceName the data source name
	 */
	private void bindSystemDatasource(IDatabase next, String dataSourceName) {
		if (logger.isTraceEnabled()) {logger.trace(format("Binding System Database - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
		try {
			if (logger.isTraceEnabled()) {logger.trace(format("Creating System Datasource - [{0}:{1}:{2}] ...", next.getType(), next.getName(), dataSourceName));}
			StaticObjects.set(StaticObjects.SYSTEM_DATASOURCE, next.getDataSource(dataSourceName));
			if (logger.isInfoEnabled()) {logger.info(format("Bound System Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
			if (logger.isTraceEnabled()) {logger.trace(format("Done creating System Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(format("Failed creating System Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName), e);}
		}
		if (logger.isTraceEnabled()) {logger.trace(format("Done binding System Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
	}

	/**
	 * Bind datasource.
	 *
	 * @param next the next
	 * @param dataSourceName the data source name
	 */
	private void bindDatasource(IDatabase next, String dataSourceName) {
		StaticObjects.set(StaticObjects.DATABASE, next);
		if (logger.isTraceEnabled()) {logger.trace(format("Binding Database - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
		try {
			if (logger.isTraceEnabled()) {logger.trace(format("Creating Datasource - [{0}:{1}:{2}] ...", next.getType(), next.getName(), dataSourceName));}
			DataSource dataSource = next.getDataSource(dataSourceName);
			StaticObjects.set(StaticObjects.DATASOURCE, dataSource);
			if (!StaticObjects.exists(StaticObjects.SYSTEM_DATASOURCE)) {
				if (logger.isTraceEnabled()) {logger.trace(format("Reusing Datasource as a System Datasource - [{0}:{1}:{2}] ...", next.getType(), next.getName(), dataSourceName));}
				StaticObjects.set(StaticObjects.SYSTEM_DATASOURCE, dataSource);
				if (logger.isInfoEnabled()) {logger.info(format("Bound System Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
			}
			if (logger.isInfoEnabled()) {logger.info(format("Bound Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
			if (logger.isTraceEnabled()) {logger.trace(format("Done creating Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
		} catch (Exception e) {
			logger.error(format("Failed creating Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName), e);
		}
		if (logger.isTraceEnabled()) {logger.trace(format("Done binding Datasource - [{0}:{1}:{2}].", next.getType(), next.getName(), dataSourceName));}
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
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
		Set<String> result = new HashSet<String>();
		for (IDatabase next : DATABASES) {
			result.add(next.getType());
		}
		return new ArrayList<String>(result);
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
	
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

}
