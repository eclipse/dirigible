/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.datasource.api;

import java.nio.charset.Charset;

import javax.sql.DataSource;

/**
 * This interface represents a Datasource. It allows for querying, modifying and
 * navigating through collections and resources.
 */
public interface IDatasource  {

	
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	public static final String DIRIGIBLE_DATASOURCE_PROVIDER = "DIRIGIBLE_DATASOURCE_PROVIDER"; //$NON-NLS-1$
	public static final String DIRIGIBLE_DATASOURCE_PROVIDER_LOCAL = "local"; //$NON-NLS-1$
	
	public static final String DIRIGIBLE_DATASOURCE_SET_AUTO_COMMIT = "DIRIGIBLE_DATASOURCE_SET_AUTO_COMMIT";
	public static final String DIRIGIBLE_DATASOURCE_MAX_CONNECTIONS_COUNT = "DIRIGIBLE_DATASOURCE_MAX_CONNECTIONS_COUNT";
	public static final String DIRIGIBLE_DATASOURCE_WAIT_TIMEOUT = "DIRIGIBLE_DATASOURCE_WAIT_TIMEOUT";
	public static final String DIRIGIBLE_DATASOURCE_WAIT_COUNT = "DIRIGIBLE_DATASOURCE_WAIT_COUNT";

	public void initialize();
	
	public DataSource getDataSource();

}
