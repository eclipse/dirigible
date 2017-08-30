/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.api;

import java.util.Map;

import javax.sql.DataSource;

/**
 * This interface represents a Database. It allows for querying, modifying and
 * navigating through collections and resources.
 */
public interface IDatabase {

	public static final String DIRIGIBLE_DATABASE_PROVIDER = "DIRIGIBLE_DATABASE_PROVIDER"; //$NON-NLS-1$
	public static final String DIRIGIBLE_DATABASE_PROVIDER_LOCAL = "derby"; //$NON-NLS-1$

	public static final String DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT = "DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT";
	public static final String DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT = "DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT";
	public static final String DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT = "DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT";
	public static final String DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT = "DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT";

	public static final String DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT = "DefaultDB"; //$NON-NLS-1$
	public static final String DIRIGIBLE_DATABASE_DATASOURCE_TEST = "target/tests/derby"; //$NON-NLS-1$

	public void initialize();

	public String getType();

	public DataSource getDataSource();

	public DataSource getDataSource(String name);

	public Map<String, DataSource> getDataSources();

}
