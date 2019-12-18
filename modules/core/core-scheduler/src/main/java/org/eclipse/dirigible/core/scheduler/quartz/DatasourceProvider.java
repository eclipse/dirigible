/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.scheduler.quartz;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.database.api.DatabaseModule;

/**
 * The Datasource Provider.
 */
public class DatasourceProvider {

	@Inject
	private DataSource datasource;
	
	private DataSource runtimeDataSource;

	/**
	 * Gets the datasource.
	 *
	 * @return the datasource
	 */
	public DataSource getDatasource() {
		if (this.runtimeDataSource == null) {
			this.runtimeDataSource = this.datasource;
			Configuration.load("/dirigible-scheduler.properties");
			Configuration.load("/dirigible.properties");
			String dataSourceType = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE);
			String dataSourceName = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME);
			if (dataSourceType != null && dataSourceName != null) {
				this.runtimeDataSource = DatabaseModule.getDataSource(dataSourceType, dataSourceName);
			}
		}
		
		return this.runtimeDataSource;
	}

//	/**
//	 * Sets the datasource.
//	 *
//	 * @param datasource
//	 *            the new datasource
//	 */
//	public void setDatasource(DataSource datasource) {
//		this.datasource = datasource;
//	}

}
