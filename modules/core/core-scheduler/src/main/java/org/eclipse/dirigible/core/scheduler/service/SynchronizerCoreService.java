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
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateArtefactDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateLogDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Synchronizer Core Service.
 */
public class SynchronizerCoreService implements ISynchronizerCoreService, ICleanupService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The synchronizer state persistence manager. */
	private PersistenceManager<SynchronizerStateDefinition> synchronizerStatePersistenceManager = new PersistenceManager<SynchronizerStateDefinition>();
	
	/** The synchronizer state log persistence manager. */
	private PersistenceManager<SynchronizerStateLogDefinition> synchronizerStateLogPersistenceManager = new PersistenceManager<SynchronizerStateLogDefinition>();
	
	/** The synchronizer state artefact persistence manager. */
	private PersistenceManager<SynchronizerStateArtefactDefinition> synchronizerStateArtefactPersistenceManager = new PersistenceManager<SynchronizerStateArtefactDefinition>();
	
	/** The synchronization enabled. */
	private static AtomicBoolean SYNCHRONIZATION_ENABLED = new AtomicBoolean(true);
	
	/** The artefact types. */
	private static Map<String, ISynchronizerArtefactType> artefactTypes = new HashMap<String, ISynchronizerArtefactType>();
	
	static {
		ServiceLoader<ISynchronizerArtefactType> artefactTypesProviders = ServiceLoader.load(ISynchronizerArtefactType.class);
		for (Iterator iterator = artefactTypesProviders.iterator(); iterator.hasNext();) {
			ISynchronizerArtefactType type = (ISynchronizerArtefactType) iterator.next();
			artefactTypes.put(type.getId(), type);
		}
	}
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// State

	/**
	 * Creates the synchronizer state.
	 *
	 * @param name the name
	 * @param state the state
	 * @param message the message
	 * @param firstTimeTriggered the first time triggered
	 * @param firstTimeFinished the first time finished
	 * @param lastTimeTriggered the last time triggered
	 * @param lastTimeFinished the last time finished
	 * @return the synchronizer state definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Creates the synchronizer state.
	 *
	 * @param synchronizerStateDefinition the synchronizer state definition
	 * @return the synchronizer state definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Gets the synchronizer state.
	 *
	 * @param name the name
	 * @return the synchronizer state
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Removes the synchronizer state.
	 *
	 * @param name the name
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Update synchronizer state.
	 *
	 * @param name the name
	 * @param state the state
	 * @param message the message
	 * @param firstTimeTriggered the first time triggered
	 * @param firstTimeFinished the first time finished
	 * @param lastTimeTriggered the last time triggered
	 * @param lastTimeFinished the last time finished
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Update synchronizer state.
	 *
	 * @param synchronizerStateDefinition the synchronizer state definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Gets the synchronizer states.
	 *
	 * @return the synchronizer states
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Gets the synchronizer state logs.
	 *
	 * @param name the name
	 * @return the synchronizer state logs
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Delete old synchronizer state logs.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Exists synchronizer state.
	 *
	 * @param name the name
	 * @return true, if successful
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#existsSynchronizerState(java.lang.String)
	 */
	@Override
	public boolean existsSynchronizerState(String name) throws SchedulerException {
		return getSynchronizerState(name) != null;
	}

	/**
	 * Initialize synchronizers states.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Disable synchronization.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#disableSynchronization()
	 */
	@Override
	public void disableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(false);
	}

	/**
	 * Enable synchronization.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#enableSynchronization()
	 */
	@Override
	public void enableSynchronization() throws SchedulerException {
		SYNCHRONIZATION_ENABLED.set(true);
	}
	
	/**
	 * Returns the current state whether the synchronization is enabled.
	 *
	 * @return true if enabled
	 */
	public static boolean isSynchronizationEnabled() {
		return SYNCHRONIZATION_ENABLED.get();
	}

	/**
	 * Creates the synchronizer state artefact.
	 *
	 * @param name the name
	 * @param location the location
	 * @param type the type
	 * @param state the state
	 * @param message the message
	 * @return the synchronizer state artefact definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Creates the synchronizer state artefact.
	 *
	 * @param synchronizerStateArtefactDefinition the synchronizer state artefact definition
	 * @return the synchronizer state artefact definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Gets the synchronizer state artefact.
	 *
	 * @param name the name
	 * @param location the location
	 * @return the synchronizer state artefact
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Removes the synchronizer state artefact.
	 *
	 * @param name the name
	 * @param location the location
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Update synchronizer state artefact.
	 *
	 * @param name the name
	 * @param location the location
	 * @param type the type
	 * @param state the state
	 * @param message the message
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Update synchronizer state artefact.
	 *
	 * @param synchronizerStateArtefactDefinition the synchronizer state artefact definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Gets the synchronizer state artefacts.
	 *
	 * @return the synchronizer state artefacts
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Gets the synchronizer state artefacts by location.
	 *
	 * @param location the location
	 * @return the synchronizer state artefacts by location
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Exists synchronizer state artefact.
	 *
	 * @param name the name
	 * @param location the location
	 * @return true, if successful
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService#existsSynchronizerStateArtefact(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean existsSynchronizerStateArtefact(String name, String location) throws SchedulerException {
		SynchronizerStateArtefactDefinition existing = getSynchronizerStateArtefact(name, location);
		return (existing != null);
	}

	/**
	 * Cleanup.
	 */
	@Override
	public void cleanup() {
		try {
			deleteOldSynchronizerStateLogs();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
