/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap;
import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap.DataTypes;
import org.eclipse.dirigible.repository.datasource.db.dialect.IDialectSpecifier;
import org.eclipse.dirigible.repository.ext.db.model.DataStructureModel;
import org.eclipse.dirigible.repository.ext.db.model.DataStructureModelFactory;
import org.eclipse.dirigible.repository.ext.db.model.EDataStructureModelFormatException;
import org.eclipse.dirigible.repository.ext.db.model.TableColumnModel;
import org.eclipse.dirigible.repository.ext.db.model.TableModel;
import org.eclipse.dirigible.repository.ext.db.model.TopologicalSorter;
import org.eclipse.dirigible.repository.ext.db.model.ViewModel;
import org.eclipse.dirigible.repository.logging.Logger;

public class DatabaseUpdater extends AbstractDataUpdater {

	private static final String DASH = " - "; //$NON-NLS-1$
	private static final String AUTOMATIC_DROP_COLUMN_NOT_SUPPORTED = Messages.getString("DatabaseUpdater.AUTOMATIC_DROP_COLUMN_NOT_SUPPORTED"); //$NON-NLS-1$
	private static final String AS = " AS "; //$NON-NLS-1$
	private static final String CREATE_VIEW = "CREATE VIEW "; //$NON-NLS-1$
	private static final String DROP_VIEW = "DROP VIEW "; //$NON-NLS-1$

	private static final String CANNOT_BE_CHANGED_TO = Messages.getString("DatabaseUpdater.CANNOT_BE_CHANGED_TO"); //$NON-NLS-1$
	private static final String TYPE2 = Messages.getString("DatabaseUpdater.TYPE2"); //$NON-NLS-1$
	private static final String ADDING_PRIMARY_KEY_COLUMN = Messages.getString("DatabaseUpdater.ADDING_PRIMARY_KEY_COLUMN"); //$NON-NLS-1$
	private static final String ADDING_NOT_NULL_COLUMN = Messages.getString("DatabaseUpdater.ADDING_NOT_NULL_COLUMN"); //$NON-NLS-1$
	private static final String AND_COLUMN = Messages.getString("DatabaseUpdater.AND_COLUMN"); //$NON-NLS-1$
	private static final String INCOMPATIBLE_CHANGE_OF_TABLE = Messages.getString("DatabaseUpdater.INCOMPATIBLE_CHANGE_OF_TABLE"); //$NON-NLS-1$
	// private static final String ADD = "ADD "; //$NON-NLS-1$
	private static final String ALTER_TABLE = "ALTER TABLE "; //$NON-NLS-1$

	private static final String CREATE_TABLE = "CREATE TABLE "; //$NON-NLS-1$

	private static final String DEFAULT = "DEFAULT "; //$NON-NLS-1$
	private static final String PRIMARY_KEY = "PRIMARY KEY "; //$NON-NLS-1$
	private static final String NOT_NULL = "NOT NULL "; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(DatabaseUpdater.class);

	public static final String EXTENSION_TABLE = ".table"; //$NON-NLS-1$
	public static final String EXTENSION_VIEW = ".view"; //$NON-NLS-1$
	public static final String REGISTRY_DATA_STRUCTURES_DEFAULT = ICommonConstants.DATA_CONTENT_REGISTRY_PUBLISH_LOCATION;

	private IRepository repository;
	private DataSource dataSource;
	private String location;
	private DBUtils dbUtils;

	public DatabaseUpdater(IRepository repository, DataSource dataSource, String location) {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;
		this.dbUtils = new DBUtils(dataSource);
	}

	@Override
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		if (knownFiles.size() == 0) {
			return;
		}

		logger.debug("DatabaseUpdater->executeUpdate start...");

		logger.debug("unsorted ------");

		for (String fileName : knownFiles) {
			logger.debug("fileName: " + fileName);
		}

		// preliminary sorting, so that the tables to be executed first and then the views
		knownFiles.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				boolean b1 = o1.endsWith(EXTENSION_TABLE);
				boolean b2 = o2.endsWith(EXTENSION_TABLE);
				return b1 & b2 ? 0 : b1 && !b2 ? -1 : 1;
			}
		});

		logger.debug("preliminary sorting ------");

		for (String fileName : knownFiles) {
			logger.debug("fileName: " + fileName);
		}

		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String productName = connection.getMetaData().getDatabaseProductName();
				IDialectSpecifier dialectSpecifier = DBUtils.getDialectSpecifier(productName);

				// parse models
				Map<String, DataStructureModel> models = new LinkedHashMap<String, DataStructureModel>();
				for (String dsDefinition : knownFiles) {
					try {
						if (dsDefinition.endsWith(EXTENSION_TABLE)) {
							TableModel tableModel = parseTable(dsDefinition);
							models.put(tableModel.getName(), tableModel);
						} else if (dsDefinition.endsWith(EXTENSION_VIEW)) {
							ViewModel viewModel = parseView(dsDefinition);
							models.put(viewModel.getName(), viewModel);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (errors != null) {
							errors.add(e.getMessage());
						}
					}
				}

				// topology sort of dependencies
				List<String> output = new ArrayList<String>();
				List<String> external = new ArrayList<String>();
				try {
					TopologicalSorter.sort(models, output, external);

					logger.debug("topological sorting ------");

					for (String fileName : output) {
						logger.debug("fileName: " + fileName);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					if (errors != null) {
						errors.add(e.getMessage());
					}
					output.clear();
				}

				if (output.isEmpty()) {
					// something wrong happened with the sorting - probably cyclic dependencies
					// we go for the back-up list and try to apply what would succeed
					logger.debug("probably cyclic dependencies!");
					output.addAll(models.keySet());
				}

				// drop view in a reverse order
				for (int i = output.size() - 1; i >= 0; i--) {
					String dsName = output.get(i);
					DataStructureModel model = models.get(dsName);
					try {
						if (model instanceof ViewModel) {
							executeViewDrop(connection, (ViewModel) model);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (errors != null) {
							errors.add(e.getMessage());
						}
					}
				}

				// process models in the proper order
				for (String dsName : output) {
					DataStructureModel model = models.get(dsName);
					try {
						if (model instanceof TableModel) {
							executeTableUpdateMain(connection, dialectSpecifier, (TableModel) model);
						} else if (model instanceof ViewModel) {
							executeViewCreate(connection, (ViewModel) model);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (errors != null) {
							errors.add(e.getMessage());
						}
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("DatabaseUpdater->executeUpdate end.");
	}

	private TableModel parseTable(String dsDefinition) throws IOException {
		String content = getContent(dsDefinition);
		TableModel tableModel;
		try {
			tableModel = DataStructureModelFactory.createTableModel(content);
		} catch (EDataStructureModelFormatException e) {
			throw new IOException(e);
		}
		return tableModel;
	}

	private ViewModel parseView(String dsDefinition) throws IOException {
		String content = getContent(dsDefinition);
		ViewModel viewModel;
		try {
			viewModel = DataStructureModelFactory.createViewModel(content);
		} catch (EDataStructureModelFormatException e) {
			throw new IOException(e);
		}
		return viewModel;
	}

	@Override
	public void executeUpdate(List<String> knownFiles, HttpServletRequest request, List<String> errors) throws Exception {
		executeUpdate(knownFiles, errors);
	}

	private void executeTableUpdateMain(Connection connection, IDialectSpecifier dialectSpecifier, TableModel tableModel) throws SQLException {

		String tableName = tableModel.getName();
		boolean exists = DBUtils.isTableOrViewExists(connection, tableName);
		if (exists) {
			// String retrievedTableName = rs.getString(3);
			executeTableUpdate(connection, dialectSpecifier, tableModel);
		} else {
			executeTableCreate(connection, dialectSpecifier, tableModel);
		}
	}

	private void executeTableCreate(Connection connection, @SuppressWarnings("unused") IDialectSpecifier dialectSpecifier, TableModel tableModel)
			throws SQLException {
		logger.info("Processing table 'create': " + tableModel.getName());

		StringBuilder sql = new StringBuilder();
		String tableName = tableModel.getName();

		sql.append(CREATE_TABLE + tableName + " ("); //$NON-NLS-1$

		List<TableColumnModel> columns = tableModel.getColumns();
		int i = 0;
		for (TableColumnModel columnModel : columns) {

			if ((i > 0) && (i < columns.size())) {
				sql.append(", "); //$NON-NLS-1$
			}
			String name = columnModel.getName();
			String type = dbUtils.specifyDataType(connection, columnModel.getType());
			String length = columnModel.getLength();
			boolean notNull = columnModel.isNotNull();
			boolean primaryKey = columnModel.isPrimaryKey();
			String defaultValue = columnModel.getDefaultValue();

			sql.append(name + " " + type); //$NON-NLS-1$
			if (DataTypes.VARCHAR.equals(DataTypes.valueOf(type)) || DataTypes.CHAR.equals(DataTypes.valueOf(type))) {
				sql.append("(" + length + ") "); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				sql.append(" "); //$NON-NLS-1$
			}
			if (notNull) {
				sql.append(NOT_NULL);
			}
			if (primaryKey) {
				sql.append(PRIMARY_KEY);
			}
			if ((defaultValue != null) && !"".equals(defaultValue)) { //$NON-NLS-1$
				sql.append(DEFAULT + defaultValue + " "); //$NON-NLS-1$
			}
			i++;
		}

		sql.append(")"); //$NON-NLS-1$
		final String sqlExpression = sql.toString();
		try {
			logger.info(sqlExpression);
			executeUpdateSQL(connection, sqlExpression);
		} catch (SQLException e) {
			logger.error(sqlExpression);
			logger.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		}
	}

	private void executeUpdateSQL(Connection connection, String sql) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.executeUpdate();
	}

	private void executeTableUpdate(Connection connection, IDialectSpecifier dialectSpecifier, TableModel tableModel) throws SQLException {
		logger.info("Processing table 'update': " + tableModel.getName());

		StringBuilder sql = new StringBuilder();
		String tableName = tableModel.getName();

		Map<String, String> columnDefinitions = new HashMap<String, String>();
		ResultSet rsColumns = DBUtils.getColumns(connection, tableName);
		while (rsColumns.next()) {
			String typeName = dbUtils.specifyDataType(connection, DBSupportedTypesMap.getTypeName(rsColumns.getInt(5)));
			columnDefinitions.put(rsColumns.getString(4), typeName);
		}

		sql.append(ALTER_TABLE + tableName + " "); //$NON-NLS-1$

		List<TableColumnModel> columns = tableModel.getColumns();
		int i = 0;
		StringBuffer addSql = new StringBuffer();
		String alterAddOpen = dialectSpecifier.getAlterAddOpen();
		if (alterAddOpen != null) {
			addSql.append(alterAddOpen);
		}

		for (TableColumnModel columnModel : columns) {

			String name = columnModel.getName();
			String type = dbUtils.specifyDataType(connection, columnModel.getType());
			String length = columnModel.getLength();
			boolean notNull = columnModel.isNotNull();
			boolean primaryKey = columnModel.isPrimaryKey();
			String defaultValue = columnModel.getDefaultValue();

			if (!columnDefinitions.containsKey(name)) {
				if (i > 0) {
					addSql.append(", "); //$NON-NLS-1$
				}

				String alterAddOpenEach = dialectSpecifier.getAlterAddOpenEach();
				if (alterAddOpenEach != null) {
					addSql.append(alterAddOpenEach);
				}

				addSql.append(name + " " + type); //$NON-NLS-1$
				if (DataTypes.VARCHAR.equals(DataTypes.valueOf(type)) || DataTypes.CHAR.equals(DataTypes.valueOf(type))) {
					addSql.append("(" + length + ") "); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					addSql.append(" "); //$NON-NLS-1$
				}
				if (notNull) {
					// sql.append("NOT NULL ");
					throw new SQLException(INCOMPATIBLE_CHANGE_OF_TABLE + tableName + AND_COLUMN + name + ADDING_NOT_NULL_COLUMN);
				}
				if (primaryKey) {
					// sql.append("PRIMARY KEY ");
					throw new SQLException(INCOMPATIBLE_CHANGE_OF_TABLE + tableName + AND_COLUMN + name + ADDING_PRIMARY_KEY_COLUMN);
				}

				String alterAddCloseEach = dialectSpecifier.getAlterAddCloseEach();
				if (alterAddCloseEach != null) {
					addSql.append(alterAddCloseEach);
				}

				if ((defaultValue != null) && !"".equals(defaultValue)) { //$NON-NLS-1$
					addSql.append(DEFAULT + defaultValue + " "); //$NON-NLS-1$
				}

				i++;
			} else if (!columnDefinitions.get(name).equals(type)) {
				throw new SQLException(INCOMPATIBLE_CHANGE_OF_TABLE + tableName + AND_COLUMN + name + TYPE2 + columnDefinitions.get(name)
						+ CANNOT_BE_CHANGED_TO + type);
			}

		}

		// TODO Derby does not support multiple ADD in a single statement!

		if (i > 0) {
			String alterAddClose = dialectSpecifier.getAlterAddClose();
			if (alterAddClose != null) {
				addSql.append(alterAddClose);
			}
			sql.append(addSql.toString());
		}

		if (columnDefinitions.size() > columns.size()) {
			throw new SQLException(INCOMPATIBLE_CHANGE_OF_TABLE + tableName + DASH + AUTOMATIC_DROP_COLUMN_NOT_SUPPORTED);
		}

		if (i > 0) {
			final String sqlExpression = sql.toString();
			try {
				logger.info(sqlExpression);
				executeUpdateSQL(connection, sqlExpression);
			} catch (SQLException e) {
				logger.error(sqlExpression);
				logger.error(e.getMessage(), e);
				throw new SQLException(e.getMessage(), e);
			}
		}
	}

	private String getContent(String dsDefinition) throws IOException {
		// # 177
		// IResource resource = repository.getResource(this.location +
		// dsDefinition);
		IResource resource = this.repository.getResource(dsDefinition);

		String content = new String(resource.getContent(), ICommonConstants.UTF8);
		return content;
	}

	private void executeViewDrop(Connection connection, ViewModel viewModel) throws SQLException {
		logger.info("Processing view 'drop': " + viewModel.getName());

		StringBuilder sql = new StringBuilder();
		String viewName = viewModel.getName();

		String sqlExpression = null;
		boolean exists = DBUtils.isTableOrViewExists(connection, viewName);
		if (exists) {
			sql.append(DROP_VIEW + viewName);
			sqlExpression = sql.toString();
			try {
				logger.info(sqlExpression);
				executeUpdateSQL(connection, sqlExpression);
			} catch (SQLException e) {
				logger.error(sqlExpression);
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void executeViewCreate(Connection connection, ViewModel viewModel) throws SQLException {
		logger.info("Processing view 'create': " + viewModel.getName());

		StringBuilder sql = new StringBuilder();
		String viewName = viewModel.getName();
		String query = viewModel.getQuery();

		sql.append(CREATE_VIEW + viewName + AS + query);

		String sqlExpression = sql.toString();
		try {
			logger.info(sqlExpression);
			executeUpdateSQL(connection, sqlExpression);
		} catch (SQLException e) {
			logger.error(sqlExpression);
			logger.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		}
	}

	@Override
	public void enumerateKnownFiles(ICollection collection, List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (IResource resource : resources) {
				if ((resource != null) && (resource.getName() != null)) {
					if (resource.getName().endsWith(EXTENSION_TABLE) || resource.getName().endsWith(EXTENSION_VIEW)) {
						// # 177
						// String fullPath = collection.getPath().substring(
						// this.location.length())
						// + IRepository.SEPARATOR + resource.getName();
						String fullPath = resource.getPath();
						dsDefinitions.add(fullPath);
					}
				}
			}

			List<ICollection> collections = collection.getCollections();
			for (ICollection subCollection : collections) {
				enumerateKnownFiles(subCollection, dsDefinitions);
			}
		}
	}

	@Override
	public IRepository getRepository() {
		return this.repository;
	}

	@Override
	public String getLocation() {
		return this.location;
	}
}
