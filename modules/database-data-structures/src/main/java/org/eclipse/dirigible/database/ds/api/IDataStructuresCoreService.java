/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDataStructuresCoreService.
 */
public interface IDataStructuresCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_TABLE. */
	public static final String FILE_EXTENSION_TABLE = ".table";

	/** The Constant FILE_EXTENSION_VIEW. */
	public static final String FILE_EXTENSION_VIEW = ".view";

	/** The Constant TYPE_TABLE. */
	public static final String TYPE_TABLE = "TABLE";

	/** The Constant TYPE_VIEW. */
	public static final String TYPE_VIEW = "VIEW";

	// Tables

	/**
	 * Creates the table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure table model
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureTableModel createTable(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the table.
	 *
	 * @param location the location
	 * @return the table
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureTableModel getTable(String location) throws DataStructuresException;

	/**
	 * Gets the table by name.
	 *
	 * @param name the name
	 * @return the table by name
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureTableModel getTableByName(String name) throws DataStructuresException;

	/**
	 * Exists table.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	public boolean existsTable(String location) throws DataStructuresException;

	/**
	 * Removes the table.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	public void removeTable(String location) throws DataStructuresException;

	/**
	 * Update table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	public void updateTable(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the tables.
	 *
	 * @return the tables
	 * @throws DataStructuresException the data structures exception
	 */
	public List<DataStructureTableModel> getTables() throws DataStructuresException;

	/**
	 * Parses the table.
	 *
	 * @param json the json
	 * @return the data structure table model
	 */
	public DataStructureTableModel parseTable(String json);

	/**
	 * Parses the table.
	 *
	 * @param json the json
	 * @return the data structure table model
	 */
	public DataStructureTableModel parseTable(byte[] json);

	/**
	 * Serialize table.
	 *
	 * @param tableModel the table model
	 * @return the string
	 */
	public String serializeTable(DataStructureTableModel tableModel);

	// Views

	/**
	 * Creates the view.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure view model
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureViewModel createView(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the view.
	 *
	 * @param location the location
	 * @return the view
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureViewModel getView(String location) throws DataStructuresException;

	/**
	 * Gets the view by name.
	 *
	 * @param name the name
	 * @return the view by name
	 * @throws DataStructuresException the data structures exception
	 */
	public DataStructureViewModel getViewByName(String name) throws DataStructuresException;

	/**
	 * Exists view.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	public boolean existsView(String location) throws DataStructuresException;

	/**
	 * Removes the view.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	public void removeView(String location) throws DataStructuresException;

	/**
	 * Update view.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	public void updateView(String location, String name, String hash) throws DataStructuresException;

	/**
	 * Gets the views.
	 *
	 * @return the views
	 * @throws DataStructuresException the data structures exception
	 */
	public List<DataStructureViewModel> getViews() throws DataStructuresException;

	/**
	 * Parses the view.
	 *
	 * @param json the json
	 * @return the data structure view model
	 */
	public DataStructureViewModel parseView(String json);

	/**
	 * Parses the view.
	 *
	 * @param json the json
	 * @return the data structure view model
	 */
	public DataStructureViewModel parseView(byte[] json);

	/**
	 * Serialize view.
	 *
	 * @param viewModel the view model
	 * @return the string
	 */
	public String serializeView(DataStructureViewModel viewModel);

}
