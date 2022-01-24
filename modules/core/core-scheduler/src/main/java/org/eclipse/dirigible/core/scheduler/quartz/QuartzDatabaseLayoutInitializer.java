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
 * The Quartz Database Layout Initializer.
 */
public class QuartzDatabaseLayoutInitializer extends AbstractDatabaseLayoutInitializer {

	private static final Logger logger = LoggerFactory.getLogger(QuartzDatabaseLayoutInitializer.class);

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
			logger.debug("Starting to create the database layout for Quartz...");
			@SuppressWarnings("rawtypes")
			SqlFactory sqlFactory = SqlFactory.getNative(connection);
			if (!sqlFactory.exists(connection, "QUARTZ_JOB_DETAILS")) {
				createTable(connection, "/quartz/QUARTZ_JOB_DETAILS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SIMPLE_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_SIMPLE_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_CRON_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_CRON_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SIMPROP_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_SIMPROP_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_BLOB_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_BLOB_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_CALENDARS")) {
				createTable(connection, "/quartz/QUARTZ_CALENDARS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_PAUSED_TRIGGER_GRPS")) {
				createTable(connection, "/quartz/QUARTZ_PAUSED_TRIGGER_GRPS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_FIRED_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_FIRED_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SCHEDULER_STATE")) {
				createTable(connection, "/quartz/QUARTZ_SCHEDULER_STATE.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_LOCKS")) {
				createTable(connection, "/quartz/QUARTZ_LOCKS.json");
			}

			logger.debug("Done creating the database layout for Quartz.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
