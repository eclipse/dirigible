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
package org.eclipse.dirigible.core.migrations.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.definition.MigrationStatusDefinition;

/**
 * The Migrations Core Service interface.
 */
public interface IMigrationsCoreService extends ICoreService {

	public static final String FILE_EXTENSION_MIGRATE = ".migrate";
	
	

	// Migrations

	/**
	 * Creates the migration.
	 *
	 * @param location
	 *            the location
	 * @param project
	 *            the project
	 * @param major
	 *            the major segment of the version
	 * @param minor
	 *            the minor segment of the version
	 * @param micro
	 *            the micro segment of the version
	 * @param handler
	 *            the handler
	 * @param engine
	 *            the engine
	 * @param description
	 *            the description
	 * @return the migration definition
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public MigrationDefinition createMigration(String location, String project, int major, int minor, int micro, String handler, String engine, String description) throws MigrationsException;

	/**
	 * Gets the migration.
	 *
	 * @param location
	 *            the location
	 * @return the migration
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public MigrationDefinition getMigration(String location) throws MigrationsException;

	/**
	 * Exists migration.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public boolean existsMigration(String location) throws MigrationsException;

	/**
	 * Removes the migration.
	 *
	 * @param location
	 *            the location
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public void removeMigration(String location) throws MigrationsException;

	/**
	 * Update migration.
	 *
	 * @param location
	 *            the location
	 * @param project
	 *            the project
	 * @param major
	 *            the major segment of the version
	 * @param minor
	 *            the minor segment of the version
	 * @param micro
	 *            the micro segment of the version
	 * @param handler
	 *            the handler
	 * @param engine
	 *            the engine
	 * @param description
	 *            the description
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public void updateMigration(String location, String project, int major, int minor, int micro, String handler, String engine, String description) throws MigrationsException;

	/**
	 * Gets the migrations.
	 *
	 * @return the migrations
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public List<MigrationDefinition> getMigrations() throws MigrationsException;
	
	/**
	 * Gets the migrations per project.
	 *
	 * @param project
	 *            the project
	 * @return the migrations
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public List<MigrationDefinition> getMigrationsPerProject(String project) throws MigrationsException;

	/**
	 * Parses the migration.
	 *
	 * @param json
	 *            the json
	 * @return the migration definition
	 */
	public MigrationDefinition parseMigration(String json);

	/**
	 * Parses the migration.
	 *
	 * @param json
	 *            the json
	 * @return the migration definition
	 */
	public MigrationDefinition parseMigration(byte[] json);

	/**
	 * Serialize migration.
	 *
	 * @param migration
	 *            the migration
	 * @return the string
	 */
	public String serializeMigration(MigrationDefinition migration);
	
	
	
	// Migrations Status

	/**
	 * Creates the migration status.
	 *
	 * @param project
	 *            the project
	 * @param major
	 *            the major segment of the version
	 * @param minor
	 *            the minor segment of the version
	 * @param micro
	 *            the micro segment of the version
	 * @param location
	 *            the location
	 * @return the migration status definition
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public MigrationStatusDefinition createMigrationStatus(String project, int major, int minor, int micro, String location) throws MigrationsException;

	/**
	 * Gets the migration status.
	 *
	 * @param project
	 *            the project
	 * @return the migration status
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public MigrationStatusDefinition getMigrationStatus(String project) throws MigrationsException;

	/**
	 * Exists migration status.
	 *
	 * @param project
	 *            the project
	 * @return true, if successful
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public boolean existsMigrationStatus(String project) throws MigrationsException;

	/**
	 * Removes the migration status.
	 *
	 * @param project
	 *            the project
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public void removeMigrationStatus(String project) throws MigrationsException;

	/**
	 * Update migrations status.
	 *
	 * @param project
	 *            the project
	 * @param major
	 *            the major segment of the version
	 * @param minor
	 *            the minor segment of the version
	 * @param micro
	 *            the micro segment of the version
	 * @param location
	 *            the location
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public void updateMigrationStatus(String project, int major, int minor, int micro, String location) throws MigrationsException;

	/**
	 * Gets the migrations status.
	 *
	 * @return the migrations
	 * @throws MigrationsException
	 *             the migrations exception
	 */
	public List<MigrationStatusDefinition> getMigrationsStatus() throws MigrationsException;

}
