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

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;

public abstract class AbstractDatabase implements IDatabase {
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return getDataSource(getDefaultDataSourceName());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getSystemDataSource() {
		return getDataSource(getSystemDataSourceName());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDefaultDataSourceName()
	 */
	@Override
	public String getDefaultDataSourceName() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getSystemDataSourceName()
	 */
	@Override
	public String getSystemDataSourceName() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
	}

}
