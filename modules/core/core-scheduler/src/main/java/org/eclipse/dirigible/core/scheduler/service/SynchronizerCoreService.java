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
package org.eclipse.dirigible.core.scheduler.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.service.ICleanupService;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateArtefactDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateLogDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Synchronizer Core Service.
 */
public class SynchronizerCoreService implements ISynchronizerCoreService, ICleanupService {

	private DataSource dataSource = null;

	private PersistenceManager<SynchronizerStateDefinition> synchronizerStatePersistenceManager = new PersistenceManager<SynchronizerStateDefinition>();
	
	private PersistenceManager<SynchronizerStateLogDefinition> synchronizerStateLogPersistenceManager = new PersistenceManager<SynchronizerStateLogDefinition>();
	
	private PersistenceManager<SynchronizerStateArtefactDefinition> synchronizerStateArtefactPersistenceManager = new PersistenceManager<SynchronizerStateArtefactDefinition>();
	
	private static AtomicBoolean SYNCHRONIZATION_ENABLED = new AtomicBoolean(true);
	
	private static Map<String, ISynchronizerArtefactType> artefactTypes = new HashMap<String, ISynchronizerArtefactType>();
	
	static {
		ServiceLoader<ISynchronizerArtefactType> artefactTypesProviders = ServiceLoader.load(ISynchronizerArtefactType.class);
		for (Iterator iterator = artefactTypesProviders.iterator(); iterator.hasNext();) {
			ISynchronizerArtefactType type = (ISynchronizerArtefactType) iterator.next();
			artefactTypes.put(type.getId(), type);
		}
	}
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
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
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SYNCHRONIZER_STATE_LOG")
						.where("SYNCHRONIZER_LOG_TIMESTAMP < ?")
						.build();
				synchronizerStateLogPersistenceManager.tableCheck(connection, SynchronizerStateLogDefinition.class);
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#initializeSynchronizersStates()
	 */
	@Override
	public void initializeSynchronizersStates() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#disableSynchronization()
	 */
	@Override
	public void disableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#enableSynchronization()
	 */
	@Override
	public void enableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(true);
	}
	
	/**
	 * Returns the current state whether the synchronization is enabled
	 * 
	 * @return true if enabled
	 */
	public static boolean isSynchronizationEnabled() {
		return SYNCHRONIZATION_ENABLED.get();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#createSynchronizerStateArtefact(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SynchronizerStateArtefactDefinition createSynchronizerStateArtefact(String name, String location, String type,
			String state, String message) throws SchedulerException {
		SynchronizerStateArtefactDefinition synchronizerStateArtefactDefinition = new SynchronizerStateArtefactDefinition();
		synchronizerStateArtefactDefinition.setName(name);
		synchronizerStateArtefactDefinition.setLocation(location);
		synchronizerStateArtefactDefinition.setType(type);
		synchronizerStateArtefactDefinition.setState(state);
		synchronizerStateArtefactDefinition.setMessage(message);
		synchronizerStateArtefactDefinition.setTimestamp(System.currentTimeMillis());

		return createSynchronizerStateArtefact(synchronizerStateArtefactDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#createSynchronizerStateArtefact(SynchronizerStateArtefactDefinition)
	 */
	@Override
	public SynchronizerStateArtefactDefinition createSynchronizerStateArtefact(
			SynchronizerStateArtefactDefinition synchronizerStateArtefactDefinition) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				SynchronizerStateArtefactDefinition existing = getSynchronizerStateArtefact(synchronizerStateArtefactDefinition.getName(), synchronizerStateArtefactDefinition.getLocation());
				if (existing != null && !synchronizerStateArtefactDefinition.equals(existing)) {
					synchronizerStateArtefactPersistenceManager.update(connection, synchronizerStateArtefactDefinition);
				} else {
					synchronizerStateArtefactPersistenceManager.insert(connection, synchronizerStateArtefactDefinition);
				}
				return synchronizerStateArtefactDefinition;
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerStateArtefact(java.lang.String, java.lang.String)
	 */
	@Override
	public SynchronizerStateArtefactDefinition getSynchronizerStateArtefact(String name, String location)
			throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SYNCHRONIZER_STATE_ARTEFACTS")
						.where("SYNCHRONIZER_ARTEFACT_NAME = ? AND SYNCHRONIZER_ARTEFACT_LOCATION = ?")
						.build();
				List<SynchronizerStateArtefactDefinition> list = synchronizerStateArtefactPersistenceManager.query(
						connection, SynchronizerStateArtefactDefinition.class, sql, name, location);
				if (list.size() > 0) {
					return list.get(0);
				}
				return null;
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#removeSynchronizerStateArtefact(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeSynchronizerStateArtefact(String name, String location) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				SynchronizerStateArtefactDefinition existing = getSynchronizerStateArtefact(name, location);
				if (existing != null) {
					connection = getDataSource().getConnection();
					synchronizerStateArtefactPersistenceManager.delete(
							connection, SynchronizerStateArtefactDefinition.class, existing.getId());
				}
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#updateSynchronizerStateArtefact(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateSynchronizerStateArtefact(String name, String location, String type, String state, String message)
			throws SchedulerException {
		SynchronizerStateArtefactDefinition existing = getSynchronizerStateArtefact(name, location);
		if (existing != null) {
			existing.setType(type);
			existing.setState(state);
			existing.setMessage(message);
			existing.setTimestamp(System.currentTimeMillis());
	
			updateSynchronizerStateArtefact(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#updateSynchronizerStateArtefact(SynchronizerStateArtefactDefinition)
	 */
	@Override
	public void updateSynchronizerStateArtefact(SynchronizerStateArtefactDefinition synchronizerStateArtefactDefinition)
			throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				synchronizerStateArtefactPersistenceManager.update(connection, synchronizerStateArtefactDefinition);
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerStateArtefacts()
	 */
	@Override
	public List<SynchronizerStateArtefactDefinition> getSynchronizerStateArtefacts() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				List<SynchronizerStateArtefactDefinition> list = synchronizerStateArtefactPersistenceManager.findAll(
						connection, SynchronizerStateArtefactDefinition.class);
				
				return list;
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#getSynchronizerStateArtefactsByLocation()
	 */
	@Override
	public List<SynchronizerStateArtefactDefinition> getSynchronizerStateArtefactsByLocation(String location) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SYNCHRONIZER_STATE_ARTEFACTS")
						.where("SYNCHRONIZER_ARTEFACT_LOCATION = ?")
						.build();
				synchronizerStateArtefactPersistenceManager.tableCheck(connection, SynchronizerStateArtefactDefinition.class);
				List<SynchronizerStateArtefactDefinition> list = synchronizerStateArtefactPersistenceManager.query(
						connection, SynchronizerStateArtefactDefinition.class, sql, location);
				
				return list;
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#existsSynchronizerStateArtefact(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean existsSynchronizerStateArtefact(String name, String location) throws SchedulerException {
		SynchronizerStateArtefactDefinition existing = getSynchronizerStateArtefact(name, location);
		return (existing != null);
	}

	@Override
	public void cleanup() {
		try {
			deleteOldSynchronizerStateLogs();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
