/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.api;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;

/**
 * The Abstract Database.
 */
public abstract class AbstractDatabase implements IDatabase {
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return getDataSource(getDefaultDataSourceName());
	}
	
	/**
	 * Gets the system data source.
	 *
	 * @return the system data source
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getSystemDataSource() {
		return getDataSource(getSystemDataSourceName());
	}
	
	/**
	 * Gets the default data source name.
	 *
	 * @return the default data source name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDefaultDataSourceName()
	 */
	@Override
	public String getDefaultDataSourceName() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}
	
	/**
	 * Gets the system data source name.
	 *
	 * @return the system data source name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getSystemDataSourceName()
	 */
	@Override
	public String getSystemDataSourceName() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
	}

}
