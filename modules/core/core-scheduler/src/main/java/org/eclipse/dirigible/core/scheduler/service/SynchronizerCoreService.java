/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.scheduler.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateLogDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Synchronizer Core Service.
 */
public class SynchronizerCoreService implements ISynchronizerCoreService {

	private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);

	private PersistenceManager<SynchronizerStateDefinition> synchronizerStatePersistenceManager = new PersistenceManager<SynchronizerStateDefinition>();
	
	private PersistenceManager<SynchronizerStateLogDefinition> synchronizerStateLogPersistenceManager = new PersistenceManager<SynchronizerStateLogDefinition>();
	
	private static AtomicBoolean SYNCHRONIZATION_ENABLED = new AtomicBoolean(true);

	// State

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#createSynchronizerState(java.lang.String, int,
	 * java.lang.String, long, long, long, long)
	 */
	@Override
	public SynchronizerStateDefinition createSynchronizerState(String name, int state, String message, long firstTimeTriggered, long firstTimeFinished, long lastTimeTriggered, long lastTimeFinished) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = new SynchronizerStateDefinition();
		synchronizerStateDefinition.setName(name);
		synchronizerStateDefinition.setState(state);
		synchronizerStateDefinition.setMessage(message);
		synchronizerStateDefinition.setFirstTimeTriggered(firstTimeTriggered);
		synchronizerStateDefinition.setFirstTimeFinished(firstTimeFinished);
		synchronizerStateDefinition.setLastTimeTriggered(lastTimeTriggered);
		synchronizerStateDefinition.setLastTimeFinished(lastTimeFinished);

		return createSynchronizerState(synchronizerStateDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#createSynchronizerState(org.eclipse.dirigible.core.scheduler.
	 * service.definition.JobDefinition)
	 */
	@Override
	public SynchronizerStateDefinition createSynchronizerState(SynchronizerStateDefinition synchronizerStateDefinition) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				SynchronizerStateDefinition existing = getSynchronizerState(synchronizerStateDefinition.getName());
				if (existing != null && !synchronizerStateDefinition.equals(existing)) {
					synchronizerStatePersistenceManager.update(connection, synchronizerStateDefinition);
				} else {
					synchronizerStatePersistenceManager.insert(connection, synchronizerStateDefinition);
				}
				SynchronizerStateLogDefinition synchronizerStateLogDefinition = new SynchronizerStateLogDefinition(
						synchronizerStateDefinition.getName(), synchronizerStateDefinition.getState(), synchronizerStateDefinition.getMessage(), System.currentTimeMillis());
				synchronizerStateLogPersistenceManager.insert(connection, synchronizerStateLogDefinition);
				return synchronizerStateDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerState(java.lang.String)
	 */
	@Override
	public SynchronizerStateDefinition getSynchronizerState(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return synchronizerStatePersistenceManager.find(connection, SynchronizerStateDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#removeSynchronizerState(java.lang.String)
	 */
	@Override
	public void removeSynchronizerState(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				synchronizerStatePersistenceManager.delete(connection, SynchronizerStateDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#updateSynchronizerState(java.lang.String, int,
	 * java.lang.String, long, long, long, long)
	 */
	@Override
	public void updateSynchronizerState(String name, int state, String message, long firstTimeTriggered, long firstTimeFinished, long lastTimeTriggered, long lastTimeFinished) throws SchedulerException {
		SynchronizerStateDefinition synchronizerStateDefinition = new SynchronizerStateDefinition();
		synchronizerStateDefinition.setName(name);
		synchronizerStateDefinition.setState(state);
		synchronizerStateDefinition.setMessage(message);
		synchronizerStateDefinition.setFirstTimeTriggered(firstTimeTriggered);
		synchronizerStateDefinition.setFirstTimeFinished(firstTimeFinished);
		synchronizerStateDefinition.setLastTimeTriggered(lastTimeTriggered);
		synchronizerStateDefinition.setLastTimeFinished(lastTimeFinished);
		updateSynchronizerState(synchronizerStateDefinition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#updateSynchronizerState(java.lang.String, int,
	 * java.lang.String, long, long, long, long)
	 */
	@Override
	public void updateSynchronizerState(SynchronizerStateDefinition synchronizerStateDefinition) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				synchronizerStatePersistenceManager.update(connection, synchronizerStateDefinition);
				
				SynchronizerStateLogDefinition synchronizerStateLogDefinition = new SynchronizerStateLogDefinition(
						synchronizerStateDefinition.getName(), synchronizerStateDefinition.getState(), synchronizerStateDefinition.getMessage(), System.currentTimeMillis());
				synchronizerStateLogPersistenceManager.insert(connection, synchronizerStateLogDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerStates()
	 */
	@Override
	public List<SynchronizerStateDefinition> getSynchronizerStates() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return synchronizerStatePersistenceManager.findAll(connection, SynchronizerStateDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerStateLogs()
	 */
	@Override
	public List<SynchronizerStateLogDefinition> getSynchronizerStateLogs(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SYNCHRONIZER_STATE_LOG")
						.where("SYNCHRONIZER_LOG_NAME = ?")
						.build();
				return synchronizerStateLogPersistenceManager.query(connection, SynchronizerStateLogDefinition.class, sql, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#deleteOldSynchronizerStateLogs()
	 */
	@Override
	public void deleteOldSynchronizerStateLogs() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SYNCHRONIZER_STATE_LOG")
						.where("SYNCHRONIZER_LOG_TIMESTAMP < ?")
						.build();
				synchronizerStateLogPersistenceManager.execute(connection, sql, System.currentTimeMillis() - 60*60*1000); // older than an hour
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#existsSynchronizerState(java.lang.String)
	 */
	@Override
	public boolean existsSynchronizerState(String name) throws SchedulerException {
		return getSynchronizerState(name) != null;
	}

	@Override
	public void initializeSynchronizersStates() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SYNCHRONIZER_STATE").build();
				synchronizerStatePersistenceManager.tableCheck(connection, SynchronizerStateDefinition.class);
				synchronizerStatePersistenceManager.execute(connection, sql);
				synchronizerStatePersistenceManager.tableCheck(connection, SynchronizerStateLogDefinition.class);
				sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SYNCHRONIZER_STATE_LOG").build();
				synchronizerStateLogPersistenceManager.execute(connection, sql);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	@Override
	public void disableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(false);
	}

	@Override
	public void enableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(true);
	}
	
	public static boolean isSynchronizationEnabled() {
		return SYNCHRONIZATION_ENABLED.get();
	}

}
