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
package org.eclipse.dirigible.database.ds.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureChangelogModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataAppendModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataDeleteModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataReplaceModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataUpdateModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.model.DataStructureSchemaModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;

/**
 * The Data Structures Core Service interface.
 */
public interface IDataStructuresCoreService extends ICoreService {

	// Tables

	/**
	 * Creates the table.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure table model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureTableModel createTable(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the table.
	 *
	 * @param location
	 *            the location
	 * @return the table
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureTableModel getTable(String location) throws DataStructuresException;

	/**
	 * Gets the table by name.
	 *
	 * @param name
	 *            the name
	 * @return the table by name
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureTableModel getTableByName(String name) throws DataStructuresException;

	/**
	 * Exists table.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsTable(String location) throws DataStructuresException;

	/**
	 * Removes the table.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeTable(String location) throws DataStructuresException;

	/**
	 * Update table.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateTable(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the tables.
	 *
	 * @return the tables
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureTableModel> getTables() throws DataStructuresException;

	/**
	 * Parses the table.
	 *
	 * @param json
	 *            the json
	 * @return the data structure table model
	 */
	public DataStructureTableModel parseTable(String json);

	/**
	 * Parses the table.
	 *
	 * @param json
	 *            the json
	 * @return the data structure table model
	 */
	public DataStructureTableModel parseTable(byte[] json);

	/**
	 * Serialize table.
	 *
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
	public String serializeTable(DataStructureTableModel tableModel);

	// Views

	/**
	 * Creates the view.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure view model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureViewModel createView(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the view.
	 *
	 * @param location
	 *            the location
	 * @return the view
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureViewModel getView(String location) throws DataStructuresException;

	/**
	 * Gets the view by name.
	 *
	 * @param name
	 *            the name
	 * @return the view by name
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureViewModel getViewByName(String name) throws DataStructuresException;

	/**
	 * Exists view.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsView(String location) throws DataStructuresException;

	/**
	 * Removes the view.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeView(String location) throws DataStructuresException;

	/**
	 * Update view.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateView(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the views.
	 *
	 * @return the views
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureViewModel> getViews() throws DataStructuresException;

	/**
	 * Parses the view.
	 *
	 * @param json
	 *            the json
	 * @return the data structure view model
	 */
	public DataStructureViewModel parseView(String json);

	/**
	 * Parses the view.
	 *
	 * @param json
	 *            the json
	 * @return the data structure view model
	 */
	public DataStructureViewModel parseView(byte[] json);

	/**
	 * Serialize view.
	 *
	 * @param viewModel
	 *            the view model
	 * @return the string
	 */
	public String serializeView(DataStructureViewModel viewModel);

	// Replace

	/**
	 * Creates the replace.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure replace model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataReplaceModel createReplace(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the replace.
	 *
	 * @param location
	 *            the location
	 * @return the replace
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataReplaceModel getReplace(String location) throws DataStructuresException;

	/**
	 * Exists replace.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsReplace(String location) throws DataStructuresException;

	/**
	 * Removes the replace.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeReplace(String location) throws DataStructuresException;

	/**
	 * Update replace.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateReplace(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the replaces.
	 *
	 * @return the replaces
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureDataReplaceModel> getReplaces() throws DataStructuresException;

	/**
	 * Parses the replace data.
	 *
	 * @param location
	 *            the location
	 * @param data
	 *            the date
	 * @return the data structure replace model
	 */
	public DataStructureDataReplaceModel parseReplace(String location, String data);

	// Append

	/**
	 * Creates the append.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure append model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataAppendModel createAppend(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the append.
	 *
	 * @param location
	 *            the location
	 * @return the append
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataAppendModel getAppend(String location) throws DataStructuresException;

	/**
	 * Exists append.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsAppend(String location) throws DataStructuresException;

	/**
	 * Removes the append.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeAppend(String location) throws DataStructuresException;

	/**
	 * Update append.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateAppend(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the appends.
	 *
	 * @return the appends
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureDataAppendModel> getAppends() throws DataStructuresException;

	/**
	 * Parses the append data.
	 *
	 * @param location
	 *            the location
	 * @param data
	 *            the date
	 * @return the data structure append model
	 */
	public DataStructureDataAppendModel parseAppend(String location, String data);

	// Delete

	/**
	 * Creates the delete.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure delete model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataDeleteModel createDelete(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the delete.
	 *
	 * @param location
	 *            the location
	 * @return the delete
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataDeleteModel getDelete(String location) throws DataStructuresException;

	/**
	 * Exists delete.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsDelete(String location) throws DataStructuresException;

	/**
	 * Removes the delete.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeDelete(String location) throws DataStructuresException;

	/**
	 * Update delete.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateDelete(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the deletes.
	 *
	 * @return the deletes
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureDataDeleteModel> getDeletes() throws DataStructuresException;

	/**
	 * Parses the delete data.
	 *
	 * @param location
	 *            the location
	 * @param data
	 *            the date
	 * @return the data structure delete model
	 */
	public DataStructureDataDeleteModel parseDelete(String location, String data);

	// Update

	/**
	 * Creates the update.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure update model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataUpdateModel createUpdate(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the update.
	 *
	 * @param location
	 *            the location
	 * @return the update
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureDataUpdateModel getUpdate(String location) throws DataStructuresException;

	/**
	 * Exists update.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsUpdate(String location) throws DataStructuresException;

	/**
	 * Removes the update.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeUpdate(String location) throws DataStructuresException;

	/**
	 * Update update.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateUpdate(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the updates.
	 *
	 * @return the updates
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureDataUpdateModel> getUpdates() throws DataStructuresException;

	/**
	 * Parses the update data.
	 *
	 * @param location
	 *            the location
	 * @param data
	 *            the data
	 * @return the data structure update model
	 */
	public DataStructureDataUpdateModel parseUpdate(String location, String data);
	
	// Schema

	/**
	 * Creates the schema.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure schema model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureSchemaModel createSchema(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the schema.
	 *
	 * @param location
	 *            the location
	 * @return the schema
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureSchemaModel getSchema(String location) throws DataStructuresException;

	/**
	 * Exists schema.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsSchema(String location) throws DataStructuresException;

	/**
	 * Removes the schema.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeSchema(String location) throws DataStructuresException;

	/**
	 * Update schema.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateSchema(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the schemas.
	 *
	 * @return the schemas
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureSchemaModel> getSchemas() throws DataStructuresException;

	/**
	 * Parses the schema data.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the schema content
	 * @return the data structure schema model
	 */
	public DataStructureSchemaModel parseSchema(String location, String content);

	/**
	 * Get all the data structure definitions
	 * 
	 * @return the list of the definitions
	 * @throws DataStructuresException in case of an error
	 */
	public List<DataStructureModel> getDataStructures() throws DataStructuresException;

	
	// Changelog

	/**
	 * Creates the changelog.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @return the data structure changelog model
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureChangelogModel createChangelog(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the changelog.
	 *
	 * @param location
	 *            the location
	 * @return the changelog
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public DataStructureChangelogModel getChangelog(String location) throws DataStructuresException;

	/**
	 * Exists changelog.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public boolean existsChangelog(String location) throws DataStructuresException;

	/**
	 * Removes the changelog.
	 *
	 * @param location
	 *            the location
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void removeChangelog(String location) throws DataStructuresException;

	/**
	 * Update changelog.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param hash
	 *            the hash
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public void updateChangelog(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the changelogs.
	 *
	 * @return the changelogs
	 * @throws DataStructuresException
	 *             the data structures exception
	 */
	public List<DataStructureChangelogModel> getChangelogs() throws DataStructuresException;

	/**
	 * Parses the changelog data.
	 *
	 * @param location
	 *            the location
	 * @param data
	 *            the data
	 * @return the data structure changelog model
	 */
	public DataStructureChangelogModel parseChangelog(String location, String data);
}
