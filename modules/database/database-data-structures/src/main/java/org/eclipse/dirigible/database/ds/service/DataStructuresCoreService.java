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
package org.eclipse.dirigible.database.ds.service;

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureChangelogModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataAppendModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataDeleteModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataReplaceModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataUpdateModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureSchemaModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Data Structure Core Service.
 */
public class DataStructuresCoreService implements IDataStructuresCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The table persistence manager. */
	private PersistenceManager<DataStructureTableModel> tablePersistenceManager = new PersistenceManager<DataStructureTableModel>();

	/** The view persistence manager. */
	private PersistenceManager<DataStructureViewModel> viewPersistenceManager = new PersistenceManager<DataStructureViewModel>();

	/** The replace persistence manager. */
	private PersistenceManager<DataStructureDataReplaceModel> replacePersistenceManager = new PersistenceManager<DataStructureDataReplaceModel>();

	/** The append persistence manager. */
	private PersistenceManager<DataStructureDataAppendModel> appendPersistenceManager = new PersistenceManager<DataStructureDataAppendModel>();

	/** The delete persistence manager. */
	private PersistenceManager<DataStructureDataDeleteModel> deletePersistenceManager = new PersistenceManager<DataStructureDataDeleteModel>();

	/** The update persistence manager. */
	private PersistenceManager<DataStructureDataUpdateModel> updatePersistenceManager = new PersistenceManager<DataStructureDataUpdateModel>();
	
	/** The schema persistence manager. */
	private PersistenceManager<DataStructureSchemaModel> schemaPersistenceManager = new PersistenceManager<DataStructureSchemaModel>();
	
	/** The changelog persistence manager. */
	private PersistenceManager<DataStructureChangelogModel> changelogPersistenceManager = new PersistenceManager<DataStructureChangelogModel>();
	
	/** The data structure persistence manager. */
	private PersistenceManager<DataStructureModel> dataStructurePersistenceManager = new PersistenceManager<DataStructureModel>();
	
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
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createTable(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureTableModel createTable(String location, String name, String hash) throws DataStructuresException {
		DataStructureTableModel tableModel = new DataStructureTableModel();
		tableModel.setLocation(location);
		tableModel.setName(name);
		tableModel.setType(IDataStructureModel.TYPE_TABLE);
		tableModel.setHash(hash);
		tableModel.setCreatedBy(UserFacade.getName());
		tableModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				tablePersistenceManager.insert(connection, tableModel);
				return tableModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the table.
	 *
	 * @param location the location
	 * @return the table
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTable(java.lang.String)
	 */
	@Override
	public DataStructureTableModel getTable(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return tablePersistenceManager.find(connection, DataStructureTableModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the table by name.
	 *
	 * @param name the name
	 * @return the table by name
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTableByName(java.lang.String)
	 */
	@Override
	public DataStructureTableModel getTableByName(String name) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES")
						.where("DS_NAME = ? AND DS_TYPE = ?").toString();
				List<DataStructureTableModel> tableModels = tablePersistenceManager.query(connection, DataStructureTableModel.class, sql,
						Arrays.asList(name, IDataStructureModel.TYPE_TABLE));
				if (tableModels.isEmpty()) {
					return null;
				}
				if (tableModels.size() > 1) {
					throw new DataStructuresException(format("There are more that one Table with the same name [{0}] at locations: [{1}] and [{2}].",
							name, tableModels.get(0).getLocation(), tableModels.get(1).getLocation()));
				}
				return tableModels.get(0);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the table.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeTable(java.lang.String)
	 */
	@Override
	public void removeTable(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				tablePersistenceManager.delete(connection, DataStructureTableModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateTable(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateTable(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureTableModel tableModel = getTable(location);
				tableModel.setName(name);
				tableModel.setHash(hash);
				tablePersistenceManager.update(connection, tableModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the tables.
	 *
	 * @return the tables
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTables()
	 */
	@Override
	public List<DataStructureTableModel> getTables() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureTableModel> tableModels = tablePersistenceManager.query(connection, DataStructureTableModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_TABLE));
				return tableModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists table.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsTable(java.lang.String)
	 */
	@Override
	public boolean existsTable(String location) throws DataStructuresException {
		return getTable(location) != null;
	}

	/**
	 * Parses the table.
	 *
	 * @param json the json
	 * @return the data structure table model
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseTable(java.lang.String)
	 */
	@Override
	public DataStructureTableModel parseTable(String json) {
		return DataStructureModelFactory.parseTable(json);
	}

	/**
	 * Parses the table.
	 *
	 * @param json the json
	 * @return the data structure table model
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseTable(byte[])
	 */
	@Override
	public DataStructureTableModel parseTable(byte[] json) {
		return DataStructureModelFactory.parseTable(json);
	}

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
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createView(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureViewModel createView(String location, String name, String hash) throws DataStructuresException {
		DataStructureViewModel viewModel = new DataStructureViewModel();
		viewModel.setLocation(location);
		viewModel.setName(name);
		viewModel.setType(IDataStructureModel.TYPE_VIEW);
		viewModel.setHash(hash);
		viewModel.setCreatedBy(UserFacade.getName());
		viewModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				viewPersistenceManager.insert(connection, viewModel);
				return viewModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the view.
	 *
	 * @param location the location
	 * @return the view
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getView(java.lang.String)
	 */
	@Override
	public DataStructureViewModel getView(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return viewPersistenceManager.find(connection, DataStructureViewModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the view by name.
	 *
	 * @param name the name
	 * @return the view by name
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getViewByName(java.lang.String)
	 */
	@Override
	public DataStructureViewModel getViewByName(String name) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES")
						.where("DS_NAME = ? AND DS_TYPE = ?").toString();
				List<DataStructureViewModel> viewModels = viewPersistenceManager.query(connection, DataStructureViewModel.class, sql,
						Arrays.asList(name, IDataStructureModel.TYPE_VIEW));
				if (viewModels.isEmpty()) {
					return null;
				}
				if (viewModels.size() > 1) {
					throw new DataStructuresException(format("There are more that one View with the same name [{0}] at locations: [{1}] and [{2}].",
							name, viewModels.get(0).getLocation(), viewModels.get(1).getLocation()));
				}
				return viewModels.get(0);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the view.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeView(java.lang.String)
	 */
	@Override
	public void removeView(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				viewPersistenceManager.delete(connection, DataStructureViewModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update view.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateView(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateView(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureViewModel viewModel = getView(location);
				viewModel.setName(name);
				viewModel.setHash(hash);
				viewPersistenceManager.update(connection, viewModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the views.
	 *
	 * @return the views
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getViews()
	 */
	@Override
	public List<DataStructureViewModel> getViews() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureViewModel> viewModels = viewPersistenceManager.query(connection, DataStructureViewModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_VIEW));
				return viewModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists view.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsView(java.lang.String)
	 */
	@Override
	public boolean existsView(String location) throws DataStructuresException {
		return getView(location) != null;
	}

	/**
	 * Parses the view.
	 *
	 * @param json the json
	 * @return the data structure view model
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseView(java.lang.String)
	 */
	@Override
	public DataStructureViewModel parseView(String json) {
		return DataStructureModelFactory.parseView(json);
	}

	/**
	 * Parses the view.
	 *
	 * @param json the json
	 * @return the data structure view model
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseView(byte[])
	 */
	@Override
	public DataStructureViewModel parseView(byte[] json) {
		return DataStructureModelFactory.parseView(json);
	}

	/**
	 * Serialize table.
	 *
	 * @param tableModel the table model
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#serializeTable(org.eclipse.dirigible.database.ds
	 * .model.DataStructureTableModel)
	 */
	@Override
	public String serializeTable(DataStructureTableModel tableModel) {
		return GsonHelper.toJson(tableModel);
	}

	/**
	 * Serialize view.
	 *
	 * @param viewModel the view model
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#serializeView(org.eclipse.dirigible.database.ds.
	 * model.DataStructureViewModel)
	 */
	@Override
	public String serializeView(DataStructureViewModel viewModel) {
		return GsonHelper.toJson(viewModel);
	}

	// Replaces

	/**
	 * Creates the replace.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure data replace model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createReplace(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureDataReplaceModel createReplace(String location, String name, String hash) throws DataStructuresException {
		DataStructureDataReplaceModel dataModel = new DataStructureDataReplaceModel();
		dataModel.setLocation(location);
		dataModel.setName(name);
		dataModel.setType(IDataStructureModel.TYPE_REPLACE);
		dataModel.setHash(hash);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				replacePersistenceManager.insert(connection, dataModel);
				return dataModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the replace.
	 *
	 * @param location the location
	 * @return the replace
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getReplace(java.lang.String)
	 */
	@Override
	public DataStructureDataReplaceModel getReplace(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return replacePersistenceManager.find(connection, DataStructureDataReplaceModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the replace.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeReplace(java.lang.String)
	 */
	@Override
	public void removeReplace(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				replacePersistenceManager.delete(connection, DataStructureDataReplaceModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update replace.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateReplace(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateReplace(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureDataReplaceModel dataModel = getReplace(location);
				dataModel.setName(name);
				dataModel.setHash(hash);
				replacePersistenceManager.update(connection, dataModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the replaces.
	 *
	 * @return the replaces
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getReplaces()
	 */
	@Override
	public List<DataStructureDataReplaceModel> getReplaces() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureDataReplaceModel> dataModels = replacePersistenceManager.query(connection, DataStructureDataReplaceModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_REPLACE));
				return dataModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists replace.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsReplace(java.lang.String)
	 */
	@Override
	public boolean existsReplace(String location) throws DataStructuresException {
		return getReplace(location) != null;
	}

	/**
	 * Parses the replace.
	 *
	 * @param location the location
	 * @param data the data
	 * @return the data structure data replace model
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseReplace(org.eclipse.dirigible.database.ds.
	 * model.DataStructureReplaceModel)
	 */
	@Override
	public DataStructureDataReplaceModel parseReplace(String location, String data) {
		return DataStructureModelFactory.parseReplace(location, data);
	}

	// Appends

	/**
	 * Creates the append.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure data append model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createAppend(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureDataAppendModel createAppend(String location, String name, String hash) throws DataStructuresException {
		DataStructureDataAppendModel dataModel = new DataStructureDataAppendModel();
		dataModel.setLocation(location);
		dataModel.setName(name);
		dataModel.setType(IDataStructureModel.TYPE_APPEND);
		dataModel.setHash(hash);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				appendPersistenceManager.insert(connection, dataModel);
				return dataModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the append.
	 *
	 * @param location the location
	 * @return the append
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getAppend(java.lang.String)
	 */
	@Override
	public DataStructureDataAppendModel getAppend(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return appendPersistenceManager.find(connection, DataStructureDataAppendModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the append.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeAppend(java.lang.String)
	 */
	@Override
	public void removeAppend(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				appendPersistenceManager.delete(connection, DataStructureDataAppendModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update append.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateAppend(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateAppend(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureDataAppendModel dataModel = getAppend(location);
				dataModel.setName(name);
				dataModel.setHash(hash);
				appendPersistenceManager.update(connection, dataModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the appends.
	 *
	 * @return the appends
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getAppends()
	 */
	@Override
	public List<DataStructureDataAppendModel> getAppends() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureDataAppendModel> dataModels = appendPersistenceManager.query(connection, DataStructureDataAppendModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_APPEND));
				return dataModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Parses the append.
	 *
	 * @param location the location
	 * @param data the data
	 * @return the data structure data append model
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseAppend(org.eclipse.dirigible.database.ds.
	 * model.DataStructureAppendModel)
	 */
	@Override
	public DataStructureDataAppendModel parseAppend(String location, String data) {
		return DataStructureModelFactory.parseAppend(location, data);
	}

	/**
	 * Exists append.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsAppend(java.lang.String)
	 */
	@Override
	public boolean existsAppend(String location) throws DataStructuresException {
		return getAppend(location) != null;
	}

	// Deletes

	/**
	 * Creates the delete.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure data delete model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createDelete(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureDataDeleteModel createDelete(String location, String name, String hash) throws DataStructuresException {
		DataStructureDataDeleteModel dataModel = new DataStructureDataDeleteModel();
		dataModel.setLocation(location);
		dataModel.setName(name);
		dataModel.setType(IDataStructureModel.TYPE_DELETE);
		dataModel.setHash(hash);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				deletePersistenceManager.insert(connection, dataModel);
				return dataModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the delete.
	 *
	 * @param location the location
	 * @return the delete
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getDelete(java.lang.String)
	 */
	@Override
	public DataStructureDataDeleteModel getDelete(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return deletePersistenceManager.find(connection, DataStructureDataDeleteModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the delete.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeDelete(java.lang.String)
	 */
	@Override
	public void removeDelete(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				deletePersistenceManager.delete(connection, DataStructureDataDeleteModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update delete.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateDelete(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateDelete(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureDataDeleteModel dataModel = getDelete(location);
				dataModel.setName(name);
				dataModel.setHash(hash);
				deletePersistenceManager.update(connection, dataModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the deletes.
	 *
	 * @return the deletes
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getDeletes()
	 */
	@Override
	public List<DataStructureDataDeleteModel> getDeletes() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureDataDeleteModel> dataModels = deletePersistenceManager.query(connection, DataStructureDataDeleteModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_DELETE));
				return dataModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists delete.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsDelete(java.lang.String)
	 */
	@Override
	public boolean existsDelete(String location) throws DataStructuresException {
		return getDelete(location) != null;
	}

	/**
	 * Parses the delete.
	 *
	 * @param location the location
	 * @param data the data
	 * @return the data structure data delete model
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseDelete(org.eclipse.dirigible.database.ds.
	 * model.DataStructureDeleteModel)
	 */
	@Override
	public DataStructureDataDeleteModel parseDelete(String location, String data) {
		return DataStructureModelFactory.parseDelete(location, data);
	}

	// Updates

	/**
	 * Creates the update.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure data update model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createUpdate(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureDataUpdateModel createUpdate(String location, String name, String hash) throws DataStructuresException {
		DataStructureDataUpdateModel dataModel = new DataStructureDataUpdateModel();
		dataModel.setLocation(location);
		dataModel.setName(name);
		dataModel.setType(IDataStructureModel.TYPE_UPDATE);
		dataModel.setHash(hash);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				updatePersistenceManager.insert(connection, dataModel);
				return dataModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the update.
	 *
	 * @param location the location
	 * @return the update
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getUpdate(java.lang.String)
	 */
	@Override
	public DataStructureDataUpdateModel getUpdate(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return updatePersistenceManager.find(connection, DataStructureDataUpdateModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the update.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeUpdate(java.lang.String)
	 */
	@Override
	public void removeUpdate(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				updatePersistenceManager.delete(connection, DataStructureDataUpdateModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update update.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateUpdate(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateUpdate(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureDataUpdateModel dataModel = getUpdate(location);
				dataModel.setName(name);
				dataModel.setHash(hash);
				updatePersistenceManager.update(connection, dataModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the updates.
	 *
	 * @return the updates
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getUpdates()
	 */
	@Override
	public List<DataStructureDataUpdateModel> getUpdates() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureDataUpdateModel> dataModels = updatePersistenceManager.query(connection, DataStructureDataUpdateModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_UPDATE));
				return dataModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists update.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsUpdate(java.lang.String)
	 */
	@Override
	public boolean existsUpdate(String location) throws DataStructuresException {
		return getUpdate(location) != null;
	}

	/**
	 * Parses the update.
	 *
	 * @param location the location
	 * @param data the data
	 * @return the data structure data update model
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseUpdate(java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureDataUpdateModel parseUpdate(String location, String data) {
		return DataStructureModelFactory.parseUpdate(location, data);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// Schemas

	/**
	 * Creates the schema.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure schema model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataStructureSchemaModel createSchema(String location, String name, String hash) throws DataStructuresException {
		DataStructureSchemaModel schemaModel = new DataStructureSchemaModel();
		schemaModel.setLocation(location);
		schemaModel.setName(name);
		schemaModel.setType(IDataStructureModel.TYPE_SCHEMA);
		schemaModel.setHash(hash);
		schemaModel.setCreatedBy(UserFacade.getName());
		schemaModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				schemaPersistenceManager.insert(connection, schemaModel);
				return schemaModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the schema.
	 *
	 * @param location the location
	 * @return the schema
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getSchema(java.lang.String)
	 */
	@Override
	public DataStructureSchemaModel getSchema(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return schemaPersistenceManager.find(connection, DataStructureSchemaModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Removes the schema.
	 *
	 * @param location the location
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeSchema(java.lang.String)
	 */
	@Override
	public void removeSchema(String location) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				schemaPersistenceManager.delete(connection, DataStructureSchemaModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Update schema.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateSchema(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				DataStructureSchemaModel schemaModel = getSchema(location);
				schemaModel.setName(name);
				schemaModel.setHash(hash);
				schemaPersistenceManager.update(connection, schemaModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Gets the schemas.
	 *
	 * @return the schemas
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getSchemas()
	 */
	@Override
	public List<DataStructureSchemaModel> getSchemas() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureSchemaModel> dataModels = schemaPersistenceManager.query(connection, DataStructureSchemaModel.class, sql,
						Arrays.asList(IDataStructureModel.TYPE_SCHEMA));
				return dataModels;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/**
	 * Exists schema.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsSchema(java.lang.String)
	 */
	@Override
	public boolean existsSchema(String location) throws DataStructuresException {
		return getSchema(location) != null;
	}

	/**
	 * Parses the schema.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the data structure schema model
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseSchema(java.lang.String, java.lang.String)
	 */
	public DataStructureSchemaModel parseSchema(String location, String content) {
		return DataStructureModelFactory.parseSchema(location, content);
	}
	
	
	/**
	 * Gets the data structures.
	 *
	 * @return the data structures
	 * @throws DataStructuresException the data structures exception
	 */
	/*
	 * (non-Javadoc)
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getDataStructures(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DataStructureModel> getDataStructures() throws DataStructuresException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return dataStructurePersistenceManager.findAll(connection, DataStructureModel.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}
	
	
	
	// Changelogs
	
		/**
	 * Creates the changelog.
	 *
	 * @param location the location
	 * @param name the name
	 * @param hash the hash
	 * @return the data structure changelog model
	 * @throws DataStructuresException the data structures exception
	 */
	/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#createChangelog(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public DataStructureChangelogModel createChangelog(String location, String name, String hash) throws DataStructuresException {
			DataStructureChangelogModel dataModel = new DataStructureChangelogModel();
			dataModel.setLocation(location);
			dataModel.setName(name);
			dataModel.setType(IDataStructureModel.TYPE_CHANGELOG);
			dataModel.setHash(hash);
			dataModel.setCreatedBy(UserFacade.getName());
			dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

			try {
				Connection connection = null;
				try {
					connection = getDataSource().getConnection();
					changelogPersistenceManager.insert(connection, dataModel);
					return dataModel;
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException e) {
				throw new DataStructuresException(e);
			}
		}

		/**
		 * Gets the changelog.
		 *
		 * @param location the location
		 * @return the changelog
		 * @throws DataStructuresException the data structures exception
		 */
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getChangelog(java.lang.String)
		 */
		@Override
		public DataStructureChangelogModel getChangelog(String location) throws DataStructuresException {
			try {
				Connection connection = null;
				try {
					connection = getDataSource().getConnection();
					return changelogPersistenceManager.find(connection, DataStructureChangelogModel.class, location);
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException e) {
				throw new DataStructuresException(e);
			}
		}

		/**
		 * Removes the changelog.
		 *
		 * @param location the location
		 * @throws DataStructuresException the data structures exception
		 */
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeChangelog(java.lang.String)
		 */
		@Override
		public void removeChangelog(String location) throws DataStructuresException {
			try {
				Connection connection = null;
				try {
					connection = getDataSource().getConnection();
					changelogPersistenceManager.delete(connection, DataStructureChangelogModel.class, location);
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException e) {
				throw new DataStructuresException(e);
			}
		}

		/**
		 * Update changelog.
		 *
		 * @param location the location
		 * @param name the name
		 * @param hash the hash
		 * @throws DataStructuresException the data structures exception
		 */
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#ChangelogChangelog(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void updateChangelog(String location, String name, String hash) throws DataStructuresException {
			try {
				Connection connection = null;
				try {
					connection = getDataSource().getConnection();
					DataStructureChangelogModel dataModel = getChangelog(location);
					dataModel.setName(name);
					dataModel.setHash(hash);
					changelogPersistenceManager.update(connection, dataModel);
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException e) {
				throw new DataStructuresException(e);
			}
		}

		/**
		 * Gets the changelogs.
		 *
		 * @return the changelogs
		 * @throws DataStructuresException the data structures exception
		 */
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getChangelogs()
		 */
		@Override
		public List<DataStructureChangelogModel> getChangelogs() throws DataStructuresException {
			try {
				Connection connection = null;
				try {
					connection = getDataSource().getConnection();
					String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
					List<DataStructureChangelogModel> dataModels = changelogPersistenceManager.query(connection, DataStructureChangelogModel.class, sql,
							Arrays.asList(IDataStructureModel.TYPE_CHANGELOG));
					return dataModels;
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException e) {
				throw new DataStructuresException(e);
			}
		}

		/**
		 * Exists changelog.
		 *
		 * @param location the location
		 * @return true, if successful
		 * @throws DataStructuresException the data structures exception
		 */
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsChangelog(java.lang.String)
		 */
		@Override
		public boolean existsChangelog(String location) throws DataStructuresException {
			return getChangelog(location) != null;
		}

		/**
		 * Parses the changelog.
		 *
		 * @param location the location
		 * @param data the data
		 * @return the data structure changelog model
		 */
		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseChangelog(java.lang.String, java.lang.String)
		 */
		@Override
		public DataStructureChangelogModel parseChangelog(String location, String data) {
			return DataStructureModelFactory.parseChangelog(location, data);
		}

}
