/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.quartz;

import javax.inject.Inject;
import javax.sql.DataSource;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasourceProvider.
 */
public class DatasourceProvider {

	/** The datasource. */
	@Inject
	private DataSource datasource;

	/**
	 * Gets the datasource.
	 *
	 * @return the datasource
	 */
	public DataSource getDatasource() {
		return datasource;
	}

	/**
	 * Sets the datasource.
	 *
	 * @param datasource the new datasource
	 */
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
