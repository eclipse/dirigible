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
package org.eclipse.dirigible.cms.csvim.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.cms.csvim.artefacts.CsvSynchronizationArtefactType;
import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvRecordDefinition;
import org.eclipse.dirigible.commons.api.helpers.DateTimeUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.database.ds.model.transfer.TableColumn;
import org.eclipse.dirigible.database.ds.model.transfer.TableMetadataHelper;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvProcessor {
	
	private static final String MODULE = "dirigible-cms-csv";

	private static final String ARTEFACT_TYPE_CSV = new CsvSynchronizationArtefactType().getId();

	private static final String ERROR_TYPE_PROCESSOR = "PROCESSOR";

	private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

	private DataSource dataSource = null;

	private DatabaseMetadataUtil databaseMetadataUtil = new DatabaseMetadataUtil();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		}
		return dataSource;
	}

	public void insert(List<CsvRecordDefinition> csvRecordDefinitions, CsvFileDefinition csvFileDefinition) throws SQLException {
		String tableName = csvRecordDefinitions.get(0).getTableMetadataModel().getTableName();
		String schemaName = csvRecordDefinitions.get(0).getTableMetadataModel().getSchemaName();
		try (Connection connection = getDataSource().getConnection()) {
			List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, tableName, schemaName);
			InsertBuilder insertBuilder = new InsertBuilder(SqlFactory.deriveDialect(connection));
			insertBuilder.into(tableName);

			for (int i = 0; i < csvRecordDefinitions.get(0).getCsvRecord().size(); i++) {
				String columnName = availableTableColumns.get(i).getName();
				insertBuilder.column("\"" + columnName + "\"").value("?");
			}
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertBuilder.generate())) {
				for (CsvRecordDefinition next : csvRecordDefinitions) {
					populateInsertPreparedStatementValues(next, availableTableColumns, preparedStatement);
					preparedStatement.addBatch();
				}
				logger.info(String.format("CSV records with Ids [%s] were successfully added in BATCH INSERT for table [%s].", csvRecordDefinitions.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName));
				preparedStatement.executeBatch();
			} catch(Throwable t) {
				String errorMessage = String.format("Error occurred while trying to BATCH INSERT CSV records [%s] into table [%s].", csvRecordDefinitions.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName);
				logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
				logger.error(errorMessage, t);
			}
		}
	}

	public void update(List<CsvRecordDefinition> csvRecordDefinitions, CsvFileDefinition csvFileDefinition) throws SQLException {
		String tableName = csvRecordDefinitions.get(0).getTableMetadataModel().getTableName();
		String schemaName = csvRecordDefinitions.get(0).getTableMetadataModel().getSchemaName();
		try (Connection connection = getDataSource().getConnection()) {
			List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, tableName, schemaName);
			UpdateBuilder updateBuilder = new UpdateBuilder(SqlFactory.deriveDialect(connection));
			updateBuilder.table(tableName);

			CSVRecord csvRecord = csvRecordDefinitions.get(0).getCsvRecord();
			for (int i = 0; i < csvRecord.size(); i++) {
				String columnName = availableTableColumns.get(i).getName();
				if (columnName.equals(csvRecordDefinitions.get(0).getPkColumnName())) {
					continue;
				}

				updateBuilder.set("\"" + columnName + "\"", "?");
			}

			if (csvRecordDefinitions.get(0).getHeaderNames().size() > 0) {
				updateBuilder.where(String.format("%s = ?", csvRecordDefinitions.get(0).getPkColumnName()));
			} else {
				updateBuilder.where(String.format("%s = ?", availableTableColumns.get(0).getName()));
			}

			try (PreparedStatement preparedStatement = connection.prepareStatement(updateBuilder.generate())) {
				for (CsvRecordDefinition next : csvRecordDefinitions) {
					executeUpdatePreparedStatement(next, availableTableColumns, preparedStatement);
					preparedStatement.addBatch();
				}
				logger.info(String.format("CSV records with Ids [%s] were successfully added in BATCH UPDATED for table [%s].", csvRecordDefinitions.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName));
				preparedStatement.executeBatch();
			} catch (Throwable t) {
				String errorMessage = String.format("Error occurred while trying to BATCH UPDATE CSV records [%s] into table [%s].", csvRecordDefinitions.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName);
				logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
				logger.error(errorMessage, t);
			}
		}
	}

	public void deleteAll(List<String> ids, String tableName) throws SQLException {
		if (ids.isEmpty()) {
			return;
		}

		try (Connection connection = getDataSource().getConnection()) {
			String pkColumnName = databaseMetadataUtil
					.getTableMetadata(tableName, DatabaseMetadataUtil.getTableSchema(getDataSource(), tableName)).getColumns()
					.get(0).getName();
			DeleteBuilder deleteBuilder = new DeleteBuilder(SqlFactory.deriveDialect(connection));
			deleteBuilder.from(tableName).where(String.format("%s IN (%s)", pkColumnName, String.join(",", ids)));
			try (PreparedStatement statement = connection.prepareStatement(deleteBuilder.build())) {
				statement.executeUpdate();
				logger.info(
						String.format("Entities with Row Ids: %s from table: %s", String.join(", ", ids), tableName));
			}
		}
	}

	public void delete(String id, String tableName) throws SQLException {
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(tableName)) {
			return;
		}

		try (Connection connection = getDataSource().getConnection()) {
			String pkColumnName = databaseMetadataUtil
					.getTableMetadata(tableName, DatabaseMetadataUtil.getTableSchema(getDataSource(), tableName)).getColumns()
					.get(0).getName();
			DeleteBuilder deleteBuilder = new DeleteBuilder(SqlFactory.deriveDialect(connection));
			deleteBuilder.from(tableName).where(String.format("%s='%s'", pkColumnName, id));
			try (PreparedStatement statement = connection.prepareStatement(deleteBuilder.build())) {
				statement.executeUpdate();
				logger.info(String.format("Entity with Row Id: %s from table: %s", id, tableName));
			}
		}
	}

	private void populateInsertPreparedStatementValues(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns, PreparedStatement statement) throws SQLException {
		if (csvRecordDefinition.getHeaderNames().size() > 0) {
			insertCsvWithHeader(csvRecordDefinition, tableColumns, statement);
		} else {
			insertCsvWithoutHeader(csvRecordDefinition, tableColumns, statement);
		}
	}

	private void insertCsvWithHeader(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns, PreparedStatement statement) throws SQLException {
		for (int i = 0; i < tableColumns.size(); i++) {
			String columnName = tableColumns.get(i).getName();
			int columnType = tableColumns.get(i).getType();
			String value = csvRecordDefinition.getCsvValueForColumn(columnName);
			setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i + 1, value, columnType);
		}
	}

	private void insertCsvWithoutHeader(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns, PreparedStatement statement) throws SQLException {
		for (int i = 0; i < csvRecordDefinition.getCsvRecord().size(); i++) {
			String value = csvRecordDefinition.getCsvRecord().get(i);
			int columnType = tableColumns.get(i).getType();

			setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i + 1, value, columnType);
		}
	}

	private void executeUpdatePreparedStatement(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns,
			PreparedStatement statement) throws SQLException {
		if (csvRecordDefinition.getHeaderNames().size() > 0) {
			updateCsvWithHeader(csvRecordDefinition, tableColumns, statement);
		} else {
			updateCsvWithoutHeader(csvRecordDefinition, tableColumns, statement);
		}

		statement.execute();
	}

	private void updateCsvWithHeader(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns, PreparedStatement statement) throws SQLException {
		CSVRecord csvRecord = csvRecordDefinition.getCsvRecord();

		for (int i = 1; i < tableColumns.size(); i++) {
			String columnName = tableColumns.get(i).getName();
			String value = csvRecordDefinition.getCsvValueForColumn(columnName);
			int columnType = tableColumns.get(i).getType();

			setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i, value, columnType);
		}

		int pkColumnType = tableColumns.get(0).getType();
		int lastStatementPlaceholderIndex = csvRecord.size();

		setValue(statement, lastStatementPlaceholderIndex, pkColumnType, csvRecordDefinition.getCsvRecordPkValue());
	}

	private void updateCsvWithoutHeader(CsvRecordDefinition csvRecordDefinition, List<TableColumn> tableColumns, PreparedStatement statement) throws SQLException {
		CSVRecord csvRecord = csvRecordDefinition.getCsvRecord();
		for (int i = 1; i < csvRecord.size(); i++) {
			String value = csvRecord.get(i);
			int columnType = tableColumns.get(i).getType();
			setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i, value, columnType);
		}

		int pkColumnType = tableColumns.get(0).getType();
		int lastStatementPlaceholderIndex = csvRecord.size();
		setValue(statement, lastStatementPlaceholderIndex, pkColumnType, csvRecord.get(0));
	}

	private void setPreparedStatementValue(Boolean distinguishEmptyFromNull, PreparedStatement statement, int i,
			String value, int columnType) throws SQLException {
		if (!StringUtils.isEmpty(value)) {
			setValue(statement, i, columnType, value);
		} else {
			if (distinguishEmptyFromNull) {
				setValue(statement, i, columnType, "");
			} else {
				setValue(statement, i, columnType, value);
			}
		}
	}

	/**
	 * Sets the value.
	 *
	 * @param preparedStatement the prepared statement
	 * @param i                 the i
	 * @param dataType          the data type
	 * @param value             the value
	 * @throws SQLException the SQL exception
	 */
	protected void setValue(PreparedStatement preparedStatement, int i, int dataType, String value)
			throws SQLException {
		logger.trace("setValue -> i: " + i + ", dataType: " + dataType + ", value: " + value);

		if (value == null) {
			preparedStatement.setNull(i, dataType);
		} else if (Types.VARCHAR == dataType) {
			preparedStatement.setString(i, sanitize(value));
		} else if (Types.NVARCHAR == dataType) {
			preparedStatement.setString(i, sanitize(value));
		} else if (Types.CHAR == dataType) {
			preparedStatement.setString(i, sanitize(value));
		} else if (Types.DATE == dataType) {
			preparedStatement.setDate(i, DateTimeUtils.parseDate(value));
		} else if (Types.TIME == dataType) {
			preparedStatement.setTime(i, DateTimeUtils.parseTime(value));
		} else if (Types.TIMESTAMP == dataType) {
			preparedStatement.setTimestamp(i, DateTimeUtils.parseDateTime(value));
		} else if (Types.INTEGER == dataType) {
			value = numberize(value);
			preparedStatement.setInt(i, Integer.parseInt(value));
		} else if (Types.TINYINT == dataType) {
			value = numberize(value);
			preparedStatement.setByte(i, Byte.parseByte(value));
		} else if (Types.SMALLINT == dataType) {
			value = numberize(value);
			preparedStatement.setShort(i, Short.parseShort(value));
		} else if (Types.BIGINT == dataType) {
			value = numberize(value);
			preparedStatement.setLong(i, new BigInteger(value).longValueExact());
		} else if (Types.REAL == dataType) {
			value = numberize(value);
			preparedStatement.setFloat(i, Float.parseFloat(value));
		} else if (Types.DOUBLE == dataType) {
			value = numberize(value);
			preparedStatement.setDouble(i, Double.parseDouble(value));
		} else if (Types.BOOLEAN == dataType) {
			preparedStatement.setBoolean(i, Boolean.parseBoolean(value));
		} else if (Types.DECIMAL == dataType) {
			value = numberize(value);
			preparedStatement.setBigDecimal(i, new BigDecimal(value));
		} else if (Types.NCLOB == dataType) {
			preparedStatement.setString(i, sanitize(value));
		} else {
			throw new PersistenceException(
					String.format("Database type [%s] not supported", JDBCType.valueOf(dataType).getName()));
		}
	}

	private String sanitize(String value) {
		if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
		if (value != null && value.startsWith("'") && value.endsWith("'")) {
			value = value.substring(1, value.length() - 1);
		}
		return value != null ? value.trim() : null;
	}

	private String numberize(String value) {
		if (StringUtils.isEmpty(value)) {
			value = "0";
		}
		return value;
	}
	
	/**
	 * Use to log errors from artifact processing
	 */
	private static void logProcessorErrors(String errorMessage, String errorType, String location, String artifactType) {
		try {
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE,
					"CsvProcessor", IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			logger.error("There is an issue with logging of the Errors.");
			logger.error(e.getMessage());
		}
	}

}
