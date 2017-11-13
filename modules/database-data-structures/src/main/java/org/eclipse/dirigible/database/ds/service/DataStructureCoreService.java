/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.service;

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Data Structure Core Service.
 */
@Singleton
public class DataStructureCoreService implements IDataStructuresCoreService {

	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<DataStructureTableModel> tablePersistenceManager;

	@Inject
	private PersistenceManager<DataStructureViewModel> viewPersistenceManager;

	// Tables

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
		tableModel.setType(IDataStructuresCoreService.TYPE_TABLE);
		tableModel.setHash(hash);
		tableModel.setCreatedBy(UserFacade.getName());
		tableModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTable(java.lang.String)
	 */
	@Override
	public DataStructureTableModel getTable(String location) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTableByName(java.lang.String)
	 */
	@Override
	public DataStructureTableModel getTableByName(String name) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES")
						.where("DS_NAME = ? AND DS_TYPE = ?").toString();
				List<DataStructureTableModel> tableModels = tablePersistenceManager.query(connection, DataStructureTableModel.class, sql,
						Arrays.asList(name, IDataStructuresCoreService.TYPE_TABLE));
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeTable(java.lang.String)
	 */
	@Override
	public void removeTable(String location) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateTable(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateTable(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				DataStructureTableModel tableModel = getTable(location);
				tableModel.setName(name);
				tableModel.setHash(hash);
				tablePersistenceManager.update(connection, tableModel, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getTables()
	 */
	@Override
	public List<DataStructureTableModel> getTables() throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureTableModel> tableModels = tablePersistenceManager.query(connection, DataStructureTableModel.class, sql,
						Arrays.asList(IDataStructuresCoreService.TYPE_TABLE));
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

	// Views

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
		viewModel.setType(IDataStructuresCoreService.TYPE_VIEW);
		viewModel.setHash(hash);
		viewModel.setCreatedBy(UserFacade.getName());
		viewModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getView(java.lang.String)
	 */
	@Override
	public DataStructureViewModel getView(String location) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getViewByName(java.lang.String)
	 */
	@Override
	public DataStructureViewModel getViewByName(String name) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES")
						.where("DS_NAME = ? AND DS_TYPE = ?").toString();
				List<DataStructureViewModel> viewModels = viewPersistenceManager.query(connection, DataStructureViewModel.class, sql,
						Arrays.asList(name, IDataStructuresCoreService.TYPE_VIEW));
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#removeView(java.lang.String)
	 */
	@Override
	public void removeView(String location) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#updateView(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateView(String location, String name, String hash) throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				DataStructureViewModel viewModel = getView(location);
				viewModel.setName(name);
				viewModel.setHash(hash);
				viewPersistenceManager.update(connection, viewModel, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new DataStructuresException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#getViews()
	 */
	@Override
	public List<DataStructureViewModel> getViews() throws DataStructuresException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_DATA_STRUCTURES").where("DS_TYPE = ?").toString();
				List<DataStructureViewModel> viewModels = viewPersistenceManager.query(connection, DataStructureViewModel.class, sql,
						Arrays.asList(IDataStructuresCoreService.TYPE_VIEW));
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsTable(java.lang.String)
	 */
	@Override
	public boolean existsTable(String location) throws DataStructuresException {
		return getTable(location) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#existsView(java.lang.String)
	 */
	@Override
	public boolean existsView(String location) throws DataStructuresException {
		return getView(location) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseTable(java.lang.String)
	 */
	@Override
	public DataStructureTableModel parseTable(String json) {
		return DataStructureModelFactory.parseTable(json);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseView(java.lang.String)
	 */
	@Override
	public DataStructureViewModel parseView(String json) {
		return DataStructureModelFactory.parseView(json);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseTable(byte[])
	 */
	@Override
	public DataStructureTableModel parseTable(byte[] json) {
		return DataStructureModelFactory.parseTable(json);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#parseView(byte[])
	 */
	@Override
	public DataStructureViewModel parseView(byte[] json) {
		return DataStructureModelFactory.parseView(json);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#serializeTable(org.eclipse.dirigible.database.ds
	 * .model.DataStructureTableModel)
	 */
	@Override
	public String serializeTable(DataStructureTableModel tableModel) {
		return GsonHelper.GSON.toJson(tableModel);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService#serializeView(org.eclipse.dirigible.database.ds.
	 * model.DataStructureViewModel)
	 */
	@Override
	public String serializeView(DataStructureViewModel viewModel) {
		return GsonHelper.GSON.toJson(viewModel);
	}

}
