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
package org.eclipse.dirigible.core.scheduler.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;

/**
 * The Class CustomJobStore.
 */
public class CustomJobStore extends JobStoreTX {
	
	/** The runtime data source. */
	private DataSource runtimeDataSource;
	
	/** The data source type. */
	private String dataSourceType;
	
	/** The data source name. */
	private String dataSourceName;

	/**
	 * Initialize.
	 *
	 * @param classLoadHelper the class load helper
	 * @param schedSignaler the sched signaler
	 * @throws SchedulerConfigException the scheduler config exception
	 */
	/* (non-Javadoc)
	 * @see org.quartz.impl.jdbcjobstore.JobStoreTX#initialize(org.quartz.spi.ClassLoadHelper, org.quartz.spi.SchedulerSignaler)
	 */
	@Override
	public void initialize(ClassLoadHelper classLoadHelper, SchedulerSignaler schedSignaler)
			throws SchedulerConfigException {
		
		Configuration.loadModuleConfig("/dirigible-scheduler.properties");
		String dataSourceType = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_TYPE);
		String dataSourceName = Configuration.get(SchedulerManager.DIRIGIBLE_SCHEDULER_DATABASE_DATASOURCE_NAME);
		if (dataSourceType != null && dataSourceName != null) {
			this.runtimeDataSource = DatabaseModule.getDataSource(dataSourceType, dataSourceName);
		}
		super.initialize(classLoadHelper, schedSignaler);
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	/* (non-Javadoc)
	 * @see org.quartz.impl.jdbcjobstore.JobStoreSupport#getDataSource()
	 */
	@Override
	public String getDataSource() {
		if (this.runtimeDataSource != null) {
			return this.dataSourceName;
		}
		return super.getDataSource();
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws JobPersistenceException the job persistence exception
	 */
	/* (non-Javadoc)
	 * @see org.quartz.impl.jdbcjobstore.JobStoreSupport#getConnection()
	 */
	@Override
	protected Connection getConnection() throws JobPersistenceException {
		if (this.runtimeDataSource != null) {
			try {
				Connection conn = runtimeDataSource.getConnection();
				// Protect connection attributes we might change.
		        conn = getAttributeRestoringConnection(conn);

		        // Set any connection connection attributes we are to override.
		        try {
		            if (!isDontSetAutoCommitFalse()) {
		                conn.setAutoCommit(false);
		            }

		            if(isTxIsolationLevelSerializable()) {
		                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		            }
		        } catch (SQLException sqle) {
		            getLog().warn("Failed to override connection auto commit/transaction isolation.", sqle);
		        } catch (Throwable e) {
		            try { conn.close(); } catch(Throwable ignored) {}
		            
		            throw new JobPersistenceException(
		                "Failure setting up connection.", e);
		        }
		        return conn;
			} catch (SQLException e) {
				throw new JobPersistenceException(e.getMessage());
			}
		}
		return super.getConnection();
	}

	

}
