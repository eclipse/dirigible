/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.managed;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Managed Database
 */
public class ManagedDatabase implements IDatabase {

	private static final Logger logger = LoggerFactory.getLogger(ManagedDatabase.class);

	/** The Constant TYPE. */
	public static final String TYPE = "managed";

	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	private static final String JNDI_DEFAULT_PREFIX = "java:comp/env/jdbc/";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.load("/dirigible-database-managed.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return getDataSource(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource(java.lang.String)
	 */
	@Override
	public DataSource getDataSource(String name) {
		DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			return dataSource;
		}
		dataSource = lookupDataSource(name);
		DATASOURCES.put(name, dataSource);
		return dataSource;
	}

	/**
	 * Lookup data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	private DataSource lookupDataSource(String name) {
		try {
			final InitialContext ctx = new InitialContext();
			if (name != null) {
				return (DataSource) ctx.lookup(JNDI_DEFAULT_PREFIX + name);
			}
			return null;
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			throw new ManagedDatabaseException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getType()
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSources()
	 */
	@Override
	public Map<String, DataSource> getDataSources() {
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
