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
package org.eclipse.dirigible.database.databases.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.database.databases.definition.DatabaseDefinition;

/**
 * Manage the databases definitions.
 */
public interface IDatabasesCoreService extends ICoreService {
	
	/**
	 * Create database.
	 *
	 * @param name the name
	 * @param driver the driver
	 * @param url the url
	 * @param username the username
	 * @param password the password
	 * @param parameters the parameters
	 * @return the database
	 * @throws DatabasesException in case of error
	 */
	public DatabaseDefinition createDatabase(String name, String driver, String url, String username, String password, String parameters) throws DatabasesException;
	
	/**
	 * Create database.
	 *
	 * @param definition the definition
	 * @return the database
	 * @throws DatabasesException in case of error
	 */
	public DatabaseDefinition createDatabase(DatabaseDefinition definition) throws DatabasesException;
	
	/**
	 * Get the database by id.
	 *
	 * @param id the id
	 * @return the database
	 * @throws DatabasesException in case of error
	 */
	public DatabaseDefinition getDatabase(long id) throws DatabasesException;
	
	/**
	 * Get the database by name.
	 *
	 * @param name the name
	 * @return the database
	 * @throws DatabasesException in case of error
	 */
	public DatabaseDefinition getDatabaseByName(String name) throws DatabasesException;
	
	/**
	 * Remove the database.
	 *
	 * @param id the id
	 * @throws DatabasesException in case of error
	 */
	public void removeDatabase(long id) throws DatabasesException;
	
	/**
	 * Update existing database.
	 *
	 * @param id the id
	 * @param name the name
	 * @param driver the driver
	 * @param url the url
	 * @param username the username
	 * @param password the password
	 * @param parameters the parameters
	 * @throws DatabasesException in case of error
	 */
	public void updateDatabase(long id, String name, String driver, String url, String username, String password, String parameters) throws DatabasesException;
	
	/**
	 * Update database.
	 *
	 * @param definition the definition
	 * @throws DatabasesException in case of error
	 */
	public void updateDatabase(DatabaseDefinition definition) throws DatabasesException;
	
	/**
	 * List all the databases.
	 *
	 * @return the list of databases
	 * @throws DatabasesException in case of error
	 */
	public List<DatabaseDefinition> getDatabases() throws DatabasesException;
	

}
