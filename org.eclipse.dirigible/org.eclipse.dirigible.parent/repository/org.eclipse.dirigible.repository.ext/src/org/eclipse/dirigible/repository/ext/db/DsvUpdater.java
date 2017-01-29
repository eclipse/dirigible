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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableImporter;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class DsvUpdater extends AbstractDataUpdater {

	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
	// private static final String TABLE_NAME = "tableName";

	private static final String EXTENSION_TABLE = ".table";
	private static final String EXTENSION_DSV = ".dsv";
	private static final String EXTENSION_REPLACE = ".replace";
	private static final String EXTENSION_APPEND = ".append";
	private static final String EXTENSION_DELETE = ".delete";

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
							|| resource.getName().endsWith(EXTENSION_APPEND) || resource.getName().endsWith(EXTENSION_DELETE)) {
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
					executeReplace(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_REPLACE)) {
					executeReplace(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_APPEND)) {
					executeAppend(dsDefinition);
				} else if (dsDefinition.endsWith(EXTENSION_DELETE)) {
					executeDelete(dsDefinition);
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
	private void executeReplace(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'replace': " + dsDefinition);
		String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
		deleteAllDataFromTable(tableName);

		IResource resource = repository.getResource(dsDefinition);
		byte[] content = resource.getContent();

		if (content.length != 0) {
			DBTableImporter tableDataInserter = new DBTableImporter(dataSource, content, tableName + EXTENSION_TABLE);
			tableDataInserter.insert();
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
	private void executeAppend(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'append': " + dsDefinition);
		String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
		int tableRowsCount = getTableRowsCount(tableName);
		if (tableRowsCount == 0) {
			IResource resource = repository.getResource(dsDefinition);
			byte[] content = resource.getContent();
			if (content.length != 0) {
				DBTableImporter tableDataInserter = new DBTableImporter(dataSource, content, tableName + EXTENSION_TABLE);
				tableDataInserter.insert();
				tableRowsCount = getTableRowsCount(tableName);
				String primaryKey = getPrimaryKeyName(tableName);
				String sequenceName = tableName.toUpperCase() + "_" + primaryKey.toUpperCase();
				DBSequenceUtils dbSequenceUtils = new DBSequenceUtils(dataSource);
				dbSequenceUtils.createSequence(sequenceName, ++tableRowsCount);
			}
		}
	}

	/**
	 * If the file contains '*', the it deletes all the records
	 *
	 * @param dsDefinition
	 * @throws Exception
	 */
	private void executeDelete(String dsDefinition) throws Exception {
		logger.info("Processing rows with type 'delete': " + dsDefinition);
		IResource resource = repository.getResource(dsDefinition);
		if (resource != null) {
			byte[] content = resource.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), ICommonConstants.UTF8));
			String firstLine = reader.readLine();
			if ((firstLine != null) && firstLine.trim().equals("*")) {
				String tableName = CommonUtils.getFileNameFromRepositoryPathNoExtension(dsDefinition);
				deleteAllDataFromTable(tableName);
			} else {
				logger.error("Deletion of particular records is not supported yet");
			}
		}
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

	private String getPrimaryKeyName(String tableName) throws Exception {
		Connection con = null;
		try {
			con = getConnection();
			ResultSet primaryKeys = DBUtils.getPrimaryKeys(con, tableName);
			while (primaryKeys.next()) {
				String columnName = primaryKeys.getString("COLUMN_NAME"); //$NON-NLS-1$
				return columnName;
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return "";
	}

}
