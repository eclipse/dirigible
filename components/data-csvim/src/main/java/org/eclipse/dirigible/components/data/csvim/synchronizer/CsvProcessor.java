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
package org.eclipse.dirigible.components.data.csvim.synchronizer;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.DateTimeUtils;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.CsvRecord;
import org.eclipse.dirigible.components.data.csvim.utils.CsvimUtils;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvProcessor {

    /** The Constant MODULE. */
    private static final String MODULE = "dirigible-cms-csv";

    /** The Constant ERROR_TYPE_PROCESSOR. */
    private static final String ERROR_TYPE_PROCESSOR = "PROCESSOR";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

    /**
     * The datasources manager.
     */
    private DataSourcesManager datasourcesManager;

    /** The database metadata util. */
    private DatabaseMetadataUtil databaseMetadataUtil = new DatabaseMetadataUtil();

    @Autowired
    public CsvProcessor(DataSourcesManager datasourcesManager) {
        this.datasourcesManager = datasourcesManager;
    }

    /**
     * Insert.
     *
     * @param csvRecords the csv record definitions
     * @param csvFile   the csv file definition
     * @throws SQLException the SQL exception
     */
    public void insert(List<CsvRecord> csvRecords, CsvFile csvFile) throws SQLException {
        String tableName = csvRecords.get(0).getTableMetadataModel().getTableName();
        String schemaName = csvRecords.get(0).getTableMetadataModel().getSchemaName();
        try (Connection connection = datasourcesManager.getDefaultDataSource().getConnection()) {
            List<PersistenceTableColumnModel> availableTableColumns = databaseMetadataUtil.getTableMetadata(tableName, schemaName).getColumns();
            InsertBuilder insertBuilder = new InsertBuilder(SqlFactory.deriveDialect(connection));
            insertBuilder.into(tableName);

            for (int i = 0; i < csvRecords.get(0).getCsvRecord().size(); i++) {
                String columnName = availableTableColumns.get(i).getName();
                insertBuilder.column("\"" + columnName + "\"").value("?");
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertBuilder.generate())) {
                for (CsvRecord next : csvRecords) {
                    populateInsertPreparedStatementValues(next, availableTableColumns, preparedStatement);
                    preparedStatement.addBatch();
                }
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("CSV records with Ids [%s] were successfully added in BATCH INSERT for table [%s].", csvRecords.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName));
                }
                preparedStatement.executeBatch();
            } catch (Throwable t) {
                String errorMessage = String.format("Error occurred while trying to BATCH INSERT CSV records [%s] into table [%s].", csvRecords.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName);
                CsvimUtils.logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFile.getFile(), Csv.ARTEFACT_TYPE, MODULE);
                if (logger.isErrorEnabled()) {
                    logger.error(errorMessage, t);
                }
            }
        }
    }

    /**
     * Update.
     *
     * @param csvRecords the csv record definitions
     * @param csvFile    the csv file definition
     * @throws SQLException the SQL exception
     */
    public void update(List<CsvRecord> csvRecords, CsvFile csvFile) throws SQLException {
        String tableName = csvRecords.get(0).getTableMetadataModel().getTableName();
        String schemaName = csvRecords.get(0).getTableMetadataModel().getSchemaName();
        try (Connection connection = datasourcesManager.getDefaultDataSource().getConnection()) {
            List<PersistenceTableColumnModel> availableTableColumns = databaseMetadataUtil.getTableMetadata(tableName, schemaName).getColumns();
            //List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, tableName, schemaName);
            UpdateBuilder updateBuilder = new UpdateBuilder(SqlFactory.deriveDialect(connection));
            updateBuilder.table(tableName);

            CSVRecord csvRecord = csvRecords.get(0).getCsvRecord();
            for (int i = 0; i < csvRecord.size(); i++) {
                String columnName = availableTableColumns.get(i).getName();
                if (columnName.equals(csvRecords.get(0).getPkColumnName())) {
                    continue;
                }

                updateBuilder.set("\"" + columnName + "\"", "?");
            }

            if (csvRecords.get(0).getHeaderNames().size() > 0) {
                updateBuilder.where(String.format("%s = ?", csvRecords.get(0).getPkColumnName()));
            } else {
                updateBuilder.where(String.format("%s = ?", availableTableColumns.get(0).getName()));
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBuilder.generate())) {
                for (CsvRecord next : csvRecords) {
                    executeUpdatePreparedStatement(next, availableTableColumns, preparedStatement);
                    preparedStatement.addBatch();
                }
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("CSV records with Ids [%s] were successfully added in BATCH UPDATED for table [%s].", csvRecords.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName));
                }
                preparedStatement.executeBatch();
            } catch (Throwable t) {
                String errorMessage = String.format("Error occurred while trying to BATCH UPDATE CSV records [%s] into table [%s].", csvRecords.stream().map(e -> e.getCsvRecord().get(0)).collect(Collectors.toList()), tableName);
                CsvimUtils.logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFile.getFile(), Csv.ARTEFACT_TYPE, MODULE);
                if (logger.isErrorEnabled()) {
                    logger.error(errorMessage, t);
                }
            }
        }
    }

    /**
     * Populate insert prepared statement values.
     *
     * @param csvRecord the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void populateInsertPreparedStatementValues(CsvRecord csvRecord, List<PersistenceTableColumnModel> tableColumns, PreparedStatement statement) throws SQLException {
        if (csvRecord.getHeaderNames().size() > 0) {
            insertCsvWithHeader(csvRecord, tableColumns, statement);
        } else {
            insertCsvWithoutHeader(csvRecord, tableColumns, statement);
        }
    }

    /**
     * Execute update prepared statement.
     *
     * @param csvRecord the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void executeUpdatePreparedStatement(CsvRecord csvRecord, List<PersistenceTableColumnModel> tableColumns,
                                                PreparedStatement statement) throws SQLException {
        if (csvRecord.getHeaderNames().size() > 0) {
            updateCsvWithHeader(csvRecord, tableColumns, statement);
        } else {
            updateCsvWithoutHeader(csvRecord, tableColumns, statement);
        }

        statement.execute();
    }

    /**
     * Insert csv with header.
     *
     * @param csvRecord the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void insertCsvWithHeader(CsvRecord csvRecord, List<PersistenceTableColumnModel> tableColumns, PreparedStatement statement) throws SQLException {
        for (int i = 0; i < tableColumns.size(); i++) {
            String columnName = tableColumns.get(i).getName();
            int columnType = Integer.parseInt(tableColumns.get(i).getType());
            String value = csvRecord.getCsvValueForColumn(columnName);
            setPreparedStatementValue(csvRecord.isDistinguishEmptyFromNull(), statement, i + 1, value, columnType);
        }
    }

    /**
     * Insert csv without header.
     *
     * @param csvRecordDefinition the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void insertCsvWithoutHeader(CsvRecord csvRecordDefinition, List<PersistenceTableColumnModel> tableColumns, PreparedStatement statement) throws SQLException {
        for (int i = 0; i < csvRecordDefinition.getCsvRecord().size(); i++) {
            String value = csvRecordDefinition.getCsvRecord().get(i);
            int columnType = Integer.parseInt(tableColumns.get(i).getType());

            setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i + 1, value, columnType);
        }
    }

    /**
     * Update csv with header.
     *
     * @param csvRecordDefinition the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void updateCsvWithHeader(CsvRecord csvRecordDefinition, List<PersistenceTableColumnModel> tableColumns, PreparedStatement statement) throws SQLException {
        CSVRecord csvRecord = csvRecordDefinition.getCsvRecord();

        for (int i = 1; i < tableColumns.size(); i++) {
            String columnName = tableColumns.get(i).getName();
            String value = csvRecordDefinition.getCsvValueForColumn(columnName);
            int columnType = Integer.parseInt(tableColumns.get(i).getType());

            setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i, value, columnType);
        }

        int pkColumnType = Integer.parseInt(tableColumns.get(0).getType());
        int lastStatementPlaceholderIndex = csvRecord.size();

        setValue(statement, lastStatementPlaceholderIndex, pkColumnType, csvRecordDefinition.getCsvRecordPkValue());
    }

    /**
     * Update csv without header.
     *
     * @param csvRecordDefinition the csv record definition
     * @param tableColumns the table columns
     * @param statement the statement
     * @throws SQLException the SQL exception
     */
    private void updateCsvWithoutHeader(CsvRecord csvRecordDefinition, List<PersistenceTableColumnModel> tableColumns, PreparedStatement statement) throws SQLException {
        CSVRecord csvRecord = csvRecordDefinition.getCsvRecord();
        for (int i = 1; i < csvRecord.size(); i++) {
            String value = csvRecord.get(i);
            int columnType = Integer.parseInt(tableColumns.get(i).getType());
            setPreparedStatementValue(csvRecordDefinition.isDistinguishEmptyFromNull(), statement, i, value, columnType);
        }

        int pkColumnType = Integer.parseInt(tableColumns.get(0).getType());
        int lastStatementPlaceholderIndex = csvRecord.size();
        setValue(statement, lastStatementPlaceholderIndex, pkColumnType, csvRecord.get(0));
    }

    /**
     * Sets the prepared statement value.
     *
     * @param distinguishEmptyFromNull the distinguish empty from null
     * @param statement the statement
     * @param i the i
     * @param value the value
     * @param columnType the column type
     * @throws SQLException the SQL exception
     */
    private void setPreparedStatementValue(Boolean distinguishEmptyFromNull, PreparedStatement statement, int i,
                                           String value, int columnType) throws SQLException {
        if (StringUtils.isEmpty(value)) {
            value = distinguishEmptyFromNull ? "" : null;
        }
        setValue(statement, i, columnType, value);
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
        if (logger.isTraceEnabled()) {logger.trace("setValue -> i: " + i + ", dataType: " + dataType + ", value: " + value);}

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
        } else if (Types.BOOLEAN == dataType || Types.BIT == dataType) {
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

    /**
     * Sanitize.
     *
     * @param value the value
     * @return the string
     */
    private String sanitize(String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        if (value != null && value.startsWith("'") && value.endsWith("'")) {
            value = value.substring(1, value.length() - 1);
        }
        return value != null ? value.trim() : null;
    }

    /**
     * Numberize.
     *
     * @param value the value
     * @return the string
     */
    private String numberize(String value) {
        if (StringUtils.isEmpty(value)) {
            value = "0";
        }
        return value;
    }
}
