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
/**
 * 
 */
package org.eclipse.dirigible.database.databases.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.databases.api.DatabasesException;
import org.eclipse.dirigible.database.databases.api.IDatabasesCoreService;
import org.eclipse.dirigible.database.databases.definition.DatabaseDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Databases Core Service
 *
 */
public class DatabasesCoreService implements IDatabasesCoreService {
	
	private DataSource dataSource = null;
	
	private PersistenceManager<DatabaseDefinition> databasePersistenceManager = new PersistenceManager<DatabaseDefinition>();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	@Override
	public DatabaseDefinition createDatabase(String name, String driver, String url, String username, String password,
			String parameters) throws DatabasesException {
		DatabaseDefinition databaseDefinition = new DatabaseDefinition();
		databaseDefinition.setName(name);
		databaseDefinition.setDriver(driver);
		databaseDefinition.setUrl(url);
		databaseDefinition.setUsername(username);
		databaseDefinition.setPassword(password);
		databaseDefinition.setParameters(parameters);
		
		return createDatabase(databaseDefinition);
	}
	
	@Override
	public DatabaseDefinition createDatabase(DatabaseDefinition definition) throws DatabasesException {
		
		try (Connection connection = getDataSource().getConnection()) {
			databasePersistenceManager.insert(connection, definition);
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}
		
		return definition;
	}

	@Override
	public DatabaseDefinition getDatabase(long id) throws DatabasesException {
		try (Connection connection = getDataSource().getConnection()) {
			return databasePersistenceManager.find(connection, DatabaseDefinition.class, id);
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}
	}
	
	@Override
	public DatabaseDefinition getDatabaseByName(String name) throws DatabasesException {
		try (Connection connection = getDataSource().getConnection()) {
			String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATABASES").where("DATABASE_NAME = ?").toString();
			List<DatabaseDefinition> list = databasePersistenceManager.query(connection, DatabaseDefinition.class, sql, Arrays.asList(name));
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}
		return null;
	}

	@Override
	public void removeDatabase(long id) throws DatabasesException {
		try (Connection connection = getDataSource().getConnection()) {
			databasePersistenceManager.delete(connection, DatabaseDefinition.class, id);
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}

	}

	@Override
	public void updateDatabase(long id, String name, String driver, String url, String username, String password,
			String parameters) throws DatabasesException {
		DatabaseDefinition databaseDefinition = new DatabaseDefinition();
		databaseDefinition.setId(id);
		databaseDefinition.setName(name);
		databaseDefinition.setDriver(driver);
		databaseDefinition.setUrl(url);
		databaseDefinition.setUsername(username);
		databaseDefinition.setPassword(password);
		databaseDefinition.setParameters(parameters);
		
		updateDatabase(databaseDefinition);
	}
	
	@Override
	public void updateDatabase(DatabaseDefinition definition) throws DatabasesException {
		try (Connection connection = getDataSource().getConnection()) {
			databasePersistenceManager.update(connection, definition);
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}
	}

	@Override
	public List<DatabaseDefinition> getDatabases() throws DatabasesException {
		try (Connection connection = getDataSource().getConnection()) {
			return databasePersistenceManager.findAll(connection, DatabaseDefinition.class);
		} catch (SQLException e) {
			throw new DatabasesException(e);
		}
	}

}
