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
package org.eclipse.dirigible.core.scheduler.quartz;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Synchronizer Database Layout Initializer.
 */
public class SynchronizerDatabaseLayoutInitializer extends AbstractDatabaseLayoutInitializer {

	private static final Logger logger = LoggerFactory.getLogger(SynchronizerDatabaseLayoutInitializer.class);

	private DataSource datasource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);

	/**
	 * Initialize the database schema for Quartz.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void initialize() throws SQLException, IOException {
		Connection connection = null;
		try {
			Configuration.loadModuleConfig("/dirigible-scheduler.properties");
			String dataSourceType = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE);
			String dataSourceName = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME);
			if (dataSourceType != null && dataSourceName != null) {
				datasource = DatabaseModule.getDataSource(dataSourceType, dataSourceName);
			}
			connection = datasource.getConnection();
			logger.debug("Starting to create the database layout for Synchronizer...");
			@SuppressWarnings("rawtypes")
			SqlFactory sqlFactory = SqlFactory.getNative(connection);
			if (!sqlFactory.exists(connection, "DIRIGIBLE_SYNCHRONIZER_STATE")) {
				createTable(connection, "/synchronizer/DIRIGIBLE_SYNCHRONIZER_STATE.json");
			}
			if (!sqlFactory.exists(connection, "DIRIGIBLE_SYNCHRONIZER_STATE_LOG")) {
				createTable(connection, "/synchronizer/DIRIGIBLE_SYNCHRONIZER_STATE_LOG.json");
			}
			if (!sqlFactory.exists(connection, "DIRIGIBLE_SYNCHRONIZER_STATE_ARTEFACTS")) {
				createTable(connection, "/synchronizer/DIRIGIBLE_SYNCHRONIZER_STATE_ARTEFACTS.json");
			}

			logger.debug("Done creating the database layout for Synchronizer.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
