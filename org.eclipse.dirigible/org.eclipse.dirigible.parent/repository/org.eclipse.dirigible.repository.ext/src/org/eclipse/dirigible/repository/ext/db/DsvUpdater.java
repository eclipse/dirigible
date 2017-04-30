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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableDataReader;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableImporter;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class DsvUpdater extends AbstractDataUpdater {

	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String DELETE_FROM_WHERE = "DELETE FROM %s WHERE %s = %s";
	private static final String SELECT_FROM_WHERE = "SELECT * FROM %s WHERE %s = %s";
	private static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
	// private static final String TABLE_NAME = "tableName";

	private static final String EXTENSION_TABLE = ".table";
	private static final String EXTENSION_DSV = ".dsv";
	private static final String EXTENSION_REPLACE = ".replace";
	private static final String EXTENSION_APPEND = ".append";
	private static final String EXTENSION_DELETE = ".delete";
	private static final String EXTENSION_UPDATE = ".update";

	private static final String COLUMN_NAME = "COLUMN_NAME";

	private static final Logger logger = Logger.getLogger(DsvUpdater.class);

	private IRepository repository;
	private DataSource dataSource;
	private String location;

	public DsvUpdater(IRepository repository, DataSource dataSource, String location) {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;
	}

	@Override
	public void enumerateKnownFiles(ICollection collection, List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (IResource resource : resources) {
				if ((resource != null) && (resource.getName() != null)) {
					if (resource.getName().endsWith(EXTENSION_DSV) || resource.getName().endsWith(EXTENSION_REPLACE)
							|| resource.getName().endsWith(EXTENSION_APPEND) || resource.getName().endsWith(EXTENSION_DELETE)
							|| resource.getName().endsWith(EXTENSION_UPDATE)) {
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
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		if (knownFiles.size() == 0) {
			return;
		}

		for (String dsDefinition : knownFiles) {
			try {
				if (dsDefinition.endsWith(EXTENSION_DSV)) {
					executeReplaceRows(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_REPLACE)) {
					executeReplaceRows(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_APPEND)) {
					executeAppendRows(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_DELETE)) {
					executeDeleteRows(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_UPDATE)) {
					executeUpdateRows(dsDefinition);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				if (errors != null) {
					errors.add(e.getMessage());
				}
			}
		}
	}

	@Override
	public void executeUpdate(List<String> knownFiles, HttpServletRequest request, List<String> errors) throws Exception {
		executeUpdate(knownFiles, errors);
	}

	/**
	 * First delete all the record in the target table,
	 * then insert all the records in the data file line by line
	 *
	 * @param dsDefinition
	 * @throws Exception
	 */
	private void executeReplaceRows(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'replace': " + dsDefinition);
		String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
		deleteAllDataFromTable(tableName);

		IResource resource = repository.getResource(dsDefinition);
		byte[] content = resource.getContent();

		if (content.length != 0) {
			DBTableImporter tableDataInserter = new DBTableImporter(dataSource, content, tableName + EXTENSION_TABLE);
			tableDataInserter.insert();
			moveSequence(tableName); // move the sequence just in case
		}
	}

	/**
	 * First check whether the table is empty - one-time import can be done.
	 * If this is the case - insert all the records from the file.
	 * After that create a sequence to point to the latest record,
	 * so that the users can add more records via the standard CRUD service.
	 * In case the table is not empty, skip the import assuming the users already
	 * working with this table (new/edit/delete operations)
	 *
	 * @param dsDefinition
	 * @throws Exception
	 */
	private void executeAppendRows(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'append': " + dsDefinition);
		String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
		int tableRowsCount = getTableRowsCount(tableName);
		if (tableRowsCount == 0) {
			IResource resource = repository.getResource(dsDefinition);
			byte[] content = resource.getContent();
			if (content.length != 0) {
				DBTableImporter tableDataInserter = new DBTableImporter(dataSource, content, tableName + EXTENSION_TABLE);
				tableDataInserter.insert();
				moveSequence(tableName); // move the sequence, to be able to add more records after the initial import
			}
		}
	}

	protected void moveSequence(String tableName) throws Exception, SQLException {
		int tableRowsCount;
		tableRowsCount = getTableRowsCount(tableName);
		String primaryKey = getPrimaryKey(tableName);
		String sequenceName = tableName.toUpperCase() + "_" + primaryKey.toUpperCase();
		DBSequenceUtils dbSequenceUtils = new DBSequenceUtils(dataSource);
		dbSequenceUtils.createSequence(sequenceName, ++tableRowsCount);
	}

	/**
	 * If the file contains '*', the it deletes all the records
	 *
	 * @param dsDefinition
	 * @throws Exception
	 */
	private void executeDeleteRows(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'delete': " + dsDefinition);
		IResource resource = repository.getResource(dsDefinition);
		if (resource != null) {
			String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
			String primaryKey = getPrimaryKey(tableName);
			byte[] content = resource.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), ICommonConstants.UTF8));
			String firstLine = reader.readLine();
			if ((firstLine != null) && firstLine.trim().equals("*")) {
				deleteAllDataFromTable(tableName);
			} else {
				deleteRowsDataFromTable(tableName, primaryKey, resource.getContent());
			}
		}
	}

	private void executeUpdateRows(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'delete': " + dsDefinition);
		IResource resource = repository.getResource(dsDefinition);
		if (resource != null) {
			String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
			String primaryKey = getPrimaryKey(tableName);
			byte[] content = resource.getContent();
			updateRowsDataInTable(tableName, primaryKey, resource.getContent());
		}

	}

	private String getPrimaryKey(String tableName) throws Exception {
		String result = null;
		Connection connection = null;
		try {
			connection = this.dataSource.getConnection();
			ResultSet primaryKeys = DBUtils.getPrimaryKeys(connection, tableName);
			List<String> primaryKeysList = new ArrayList<String>();
			while (primaryKeys.next()) {
				String columnName = primaryKeys.getString(COLUMN_NAME);
				primaryKeysList.add(columnName);
			}
			if (primaryKeysList.size() == 0) {
				throw new Exception(String.format("Trying to manipulate data records for a table without a primary key: %s", tableName));
			}
			if (primaryKeysList.size() > 1) {
				throw new Exception(
						String.format("Trying to manipulate data records for a table with more than one columns in the primary key: %s", tableName));
			}
			result = primaryKeysList.get(0);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	private void deleteAllDataFromTable(String tableName) throws Exception {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement deleteStatement = con.prepareStatement(DELETE_FROM + tableName);
			deleteStatement.execute();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	private void deleteRowsDataFromTable(String tableName, String primaryKey, byte[] fileContent) throws Exception {
		Connection con = null;
		try {
			con = getConnection();
			List<String[]> records = DBTableDataReader.readRecords(new ByteArrayInputStream(fileContent));
			for (String[] record : records) {
				if (record.length > 0) {
					PreparedStatement deleteStatement = con.prepareStatement(String.format(DELETE_FROM_WHERE, tableName, primaryKey, record[0]));
					deleteStatement.execute();
				} else {
					logger.error(String.format("Skipping deletion of an empty data row for table: %s", tableName));
				}
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	private void updateRowsDataInTable(String tableName, String primaryKey, byte[] fileContent) throws Exception {
		Connection con = null;
		try {
			con = getConnection();
			List<String[]> records = DBTableDataReader.readRecords(new ByteArrayInputStream(fileContent));
			for (String[] record : records) {
				if (record.length > 0) {
					PreparedStatement stmt = con.prepareStatement(String.format(SELECT_FROM_WHERE, tableName, primaryKey, record[0]));
					ResultSet rs = stmt.executeQuery();
					if (!rs.next()) {
						StringBuffer buff = new StringBuffer();
						for (String value : record) {
							buff.append(value).append(ICommonConstants.DATA_DELIMETER);
						}
						buff.deleteCharAt(buff.length() - 1);
						buff.append("\n");
						DBTableImporter tableDataInserter = new DBTableImporter(dataSource, buff.toString().getBytes(), tableName + EXTENSION_TABLE);
						tableDataInserter.insert();
					}
				} else {
					logger.error(String.format("Skipping update of an empty data row for table: %s", tableName));
				}
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	private int getTableRowsCount(String tableName) throws Exception {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement countStatement = con.prepareStatement(SELECT_COUNT_FROM + tableName);
			ResultSet rs = countStatement.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return count;
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return -1;
	}

	private Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}

	@Override
	public IRepository getRepository() {
		return this.repository;
	}

	@Override
	public String getLocation() {
		return location;
	}

}
