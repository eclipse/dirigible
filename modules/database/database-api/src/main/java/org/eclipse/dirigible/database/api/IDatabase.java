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

import java.util.Map;

import javax.sql.DataSource;

/**
 * This interface represents a Database. It allows for querying, modifying and
 * navigating through collections and resources.
 */
public interface IDatabase {

	/** DIRIGIBLE_DATABASE_PROVIDER. */
	public static final String DIRIGIBLE_DATABASE_PROVIDER = "DIRIGIBLE_DATABASE_PROVIDER"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_PROVIDER_LOCAL. */
	public static final String DIRIGIBLE_DATABASE_PROVIDER_LOCAL = "local"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_PROVIDER_MANAGED. */
	public static final String DIRIGIBLE_DATABASE_PROVIDER_MANAGED = "managed"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_PROVIDER_CUSTOM. */
	public static final String DIRIGIBLE_DATABASE_PROVIDER_CUSTOM = "custom"; //$NON-NLS-1$
	
	/** DIRIGIBLE_DATABASE_PROVIDER_DYNAMIC. */
	public static final String DIRIGIBLE_DATABASE_PROVIDER_DYNAMIC = "dynamic"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT. */
	public static final String DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT = "DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT. */
	public static final String DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT = "DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT. */
	public static final String DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT = "DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT. */
	public static final String DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT = "DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT. */
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT = "DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT. */
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT = "DefaultDB"; //$NON-NLS-1$
	
	/** DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM. */
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM = "DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM. */
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM = "SystemDB"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_DATASOURCE_TEST. */
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_TEST = "target/tests/derby"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES. */
	public static final String DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES = "DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES"; //$NON-NLS-1$

	/** DIRIGIBLE_DATABASE_MANAGED_DATASOURCES. */
	public static final String DIRIGIBLE_DATABASE_MANAGED_DATASOURCES = "DIRIGIBLE_DATABASE_MANAGED_DATASOURCES"; //$NON-NLS-1$

	/**
	 * Initialize.
	 */
	public void initialize();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();

	/**
	 * Gets the default data source name.
	 *
	 * @return the default data source name
	 */
	public String getDefaultDataSourceName();
	
	/**
	 * Gets the system data source name.
	 *
	 * @return the system data source name
	 */
	public String getSystemDataSourceName();

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public DataSource getDataSource();
	
	/**
	 * Gets the system data source.
	 *
	 * @return the system data source
	 */
	public DataSource getSystemDataSource();

	/**
	 * Gets the data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	public DataSource getDataSource(String name);

	/**
	 * Gets the data sources.
	 *
	 * @return the data sources
	 */
	public Map<String, DataSource> getDataSources();

}
