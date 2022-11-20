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
package org.eclipse.dirigible.core.migrations.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.eclipse.dirigible.core.migrations.api.MigrationsException;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.definition.MigrationStatusDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Migrations Core Service.
 */
public class MigrationsCoreService implements IMigrationsCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The migrations persistence manager. */
	private PersistenceManager<MigrationDefinition> migrationsPersistenceManager = new PersistenceManager<MigrationDefinition>();
	
	/** The migrations status persistence manager. */
	private PersistenceManager<MigrationStatusDefinition> migrationsStatusPersistenceManager = new PersistenceManager<MigrationStatusDefinition>();
	
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

	// Migrations

	/**
	 * Creates the migration.
	 *
	 * @param location the location
	 * @param project the project
	 * @param major the major
	 * @param minor the minor
	 * @param micro the micro
	 * @param handler the handler
	 * @param engine the engine
	 * @param description the description
	 * @return the migration definition
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#createMigration(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public MigrationDefinition createMigration(String location, String project, int major, int minor, int micro, String handler, String engine, String description) throws MigrationsException {
		MigrationDefinition migrationDefinition = new MigrationDefinition();
		migrationDefinition.setLocation(location);
		migrationDefinition.setProject(project);
		migrationDefinition.setMajor(major);
		migrationDefinition.setMinor(minor);
		migrationDefinition.setMicro(micro);
		migrationDefinition.setHandler(handler);
		migrationDefinition.setEngine(engine);
		migrationDefinition.setDescription(description);
		migrationDefinition.setCreatedBy(UserFacade.getName());
		migrationDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				migrationsPersistenceManager.insert(connection, migrationDefinition);
				return migrationDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Gets the migration.
	 *
	 * @param location the location
	 * @return the migration
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#getMigration(java.lang.String)
	 */
	@Override
	public MigrationDefinition getMigration(String location) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return migrationsPersistenceManager.find(connection, MigrationDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Exists migration.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#existsMigration(java.lang.String)
	 */
	@Override
	public boolean existsMigration(String location) throws MigrationsException {
		return getMigration(location) != null;
	}

	/**
	 * Removes the migration.
	 *
	 * @param location the location
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#removeMigration(java.lang.String)
	 */
	@Override
	public void removeMigration(String location) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				migrationsPersistenceManager.delete(connection, MigrationDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Update migration.
	 *
	 * @param location the location
	 * @param project the project
	 * @param major the major
	 * @param minor the minor
	 * @param micro the micro
	 * @param handler the handler
	 * @param engine the engine
	 * @param description the description
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#updateMigration(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void updateMigration(String location, String project, int major, int minor, int micro, String handler, String engine, String description) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				MigrationDefinition migrationDefinition = getMigration(location);
				migrationDefinition.setProject(project);
				migrationDefinition.setMajor(major);
				migrationDefinition.setMinor(minor);
				migrationDefinition.setMicro(micro);
				migrationDefinition.setHandler(handler);
				migrationDefinition.setHandler(engine);
				migrationDefinition.setDescription(description);
				migrationsPersistenceManager.update(connection, migrationDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Gets the migrations.
	 *
	 * @return the migrations
	 * @throws MigrationsException the migrations exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#getMigrations()
	 */
	@Override
	public List<MigrationDefinition> getMigrations() throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return migrationsPersistenceManager.findAll(connection, MigrationDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}
	
	/**
	 * Gets the migrations per project.
	 *
	 * @param project the project
	 * @return the migrations per project
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#getMigrationsPerProject(java.lang.String)
	 */
	@Override
	public List<MigrationDefinition> getMigrationsPerProject(String project) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_MIGRATIONS").where("MIGRATION_PROJECT = ?")
						.order("MIGRATION_MAJOR").order("MIGRATION_MINOR").order("MIGRATION_MICRO").toString();
				return migrationsPersistenceManager.query(connection, MigrationDefinition.class, sql, project);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Parses the migration.
	 *
	 * @param json the json
	 * @return the migration definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#parseMigrations(java.lang.String)
	 */
	@Override
	public MigrationDefinition parseMigration(String json) {
		return GsonHelper.fromJson(json, MigrationDefinition.class);
	}

	/**
	 * Parses the migration.
	 *
	 * @param json the json
	 * @return the migration definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#parseMigrations(byte[])
	 */
	@Override
	public MigrationDefinition parseMigration(byte[] json) {
		return GsonHelper.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), MigrationDefinition.class);
	}

	/**
	 * Serialize migration.
	 *
	 * @param migration the migration
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#serializeMigrations(org.eclipse.dirigible.core.migrations.
	 * definition.MigrationDefinition[])
	 */
	@Override
	public String serializeMigration(MigrationDefinition migration) {
		return GsonHelper.toJson(migration);
	}
	
	
	
	// Migrations Status

	/**
	 * Creates the migration status.
	 *
	 * @param project the project
	 * @param major the major
	 * @param minor the minor
	 * @param micro the micro
	 * @param location the location
	 * @return the migration status definition
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#createMigrationStatus(java.lang.String, int, int, int, java.lang.String)
	 */
	@Override
	public MigrationStatusDefinition createMigrationStatus(String project, int major, int minor, int micro,
			String location) throws MigrationsException {
		MigrationStatusDefinition migrationStatusDefinition = new MigrationStatusDefinition();
		migrationStatusDefinition.setProject(project);
		migrationStatusDefinition.setMajor(major);
		migrationStatusDefinition.setMinor(minor);
		migrationStatusDefinition.setMicro(micro);
		migrationStatusDefinition.setLocation(location);
		migrationStatusDefinition.setCreatedBy(UserFacade.getName());
		migrationStatusDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				migrationsStatusPersistenceManager.insert(connection, migrationStatusDefinition);
				return migrationStatusDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Gets the migration status.
	 *
	 * @param project the project
	 * @return the migration status
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#getMigrationStatus(java.lang.String)
	 */
	@Override
	public MigrationStatusDefinition getMigrationStatus(String project) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return migrationsStatusPersistenceManager.find(connection, MigrationStatusDefinition.class, project);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

	/**
	 * Exists migration status.
	 *
	 * @param project the project
	 * @return true, if successful
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#existsMigrationStatus(java.lang.String)
	 */
	@Override
	public boolean existsMigrationStatus(String project) throws MigrationsException {
		return getMigrationStatus(project) != null;
	}

	/**
	 * Removes the migration status.
	 *
	 * @param project the project
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#removeMigrationStatus(java.lang.String)
	 */
	@Override
	public void removeMigrationStatus(String project) throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				migrationsStatusPersistenceManager.delete(connection, MigrationStatusDefinition.class, project);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}		
	}

	/**
	 * Update migration status.
	 *
	 * @param project the project
	 * @param major the major
	 * @param minor the minor
	 * @param micro the micro
	 * @param location the location
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#updateMigrationStatus(java.lang.String, int, int, int, java.lang.String)
	 */
	@Override
	public void updateMigrationStatus(String project, int major, int minor, int micro, String location)
			throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				MigrationStatusDefinition migrationStatusDefinition = getMigrationStatus(project);
				migrationStatusDefinition.setMajor(major);
				migrationStatusDefinition.setMinor(minor);
				migrationStatusDefinition.setMicro(micro);
				migrationStatusDefinition.setLocation(location);
				migrationsStatusPersistenceManager.update(connection, migrationStatusDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}		
	}

	/**
	 * Gets the migrations status.
	 *
	 * @return the migrations status
	 * @throws MigrationsException the migrations exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService#getMigrationsStatus()
	 */
	@Override
	public List<MigrationStatusDefinition> getMigrationsStatus() throws MigrationsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return migrationsStatusPersistenceManager.findAll(connection, MigrationStatusDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new MigrationsException(e);
		}
	}

}
