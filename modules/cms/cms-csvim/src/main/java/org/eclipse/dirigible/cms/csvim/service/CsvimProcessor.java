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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.cms.csvim.api.CsvimException;
import org.eclipse.dirigible.cms.csvim.artefacts.CsvSynchronizationArtefactType;
import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvRecordDefinition;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * The Class CsvimProcessor.
 */
public class CsvimProcessor {

	/** The Constant DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE. */
	private static final String DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE = "DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE";
	
	/** The Constant DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE_DEFAULT. */
	private static final int DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE_DEFAULT = 1000;

	/** The Constant DIRIGIBLE_CSV_DATA_BATCH_SIZE. */
	private static final String DIRIGIBLE_CSV_DATA_BATCH_SIZE = "DIRIGIBLE_CSV_DATA_BATCH_SIZE";
	
	/** The Constant DIRIGIBLE_CSV_DATA_BATCH_SIZE_DEFAULT. */
	private static final int DIRIGIBLE_CSV_DATA_BATCH_SIZE_DEFAULT = 100;

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(CsvimProcessor.class);
	
	/** The Constant ARTEFACT_TYPE_CSV. */
	private static final String ARTEFACT_TYPE_CSV = new CsvSynchronizationArtefactType().getId();

	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-cms-csvim";
	
	/** The Constant ERROR_TYPE_PROCESSOR. */
	private static final String ERROR_TYPE_PROCESSOR = "PROCESSOR";

	/** The Constant ERROR_MESSAGE_NO_PRIMARY_KEY. */
	private static final String ERROR_MESSAGE_NO_PRIMARY_KEY = "Error while trying to process CSV from location [%s] - no primary key.";
	
	/** The Constant ERROR_MESSAGE_DIFFERENT_COLUMNS_SIZE. */
	private static final String ERROR_MESSAGE_DIFFERENT_COLUMNS_SIZE = "Error while trying to process CSV record with Id [%s] from location [%s]. The number of CSV items should be equal to the number of columns of the database entity.";
	
	/** The Constant ERROR_MESSAGE_INSERT_RECORD. */
	private static final String ERROR_MESSAGE_INSERT_RECORD = "Error occurred while trying to insert in table [%s] a CSV record [%s] from location [%s].";

	/** The Constant PROBLEM_MESSAGE_NO_PRIMARY_KEY. */
	private static final String PROBLEM_MESSAGE_NO_PRIMARY_KEY = "No primary key. Check the configured CSVIM delimiter, whether matches the delimiter used in the CSV data file.";
	
	/** The Constant PROBLEM_MESSAGE_DIFFERENT_COLUMNS_SIZE. */
	private static final String PROBLEM_MESSAGE_DIFFERENT_COLUMNS_SIZE = "Error while trying to process CSV record with Id [%s]. The number of CSV items should be equal to the number of columns of the database entity.";
	
	/** The Constant PROBLEM_MESSAGE_INSERT_RECORD. */
	private static final String PROBLEM_MESSAGE_INSERT_RECORD = "Error occurred while trying to insert in table [%s] a CSV record [%s].";

	/** The database metadata util. */
	private final DatabaseMetadataUtil databaseMetadataUtil = new DatabaseMetadataUtil();

	/** The data source. */
	private DataSource dataSource = null;

	/** The repository. */
	private IRepository repository = null;
	
	/** The csv processor. */
	private final CsvProcessor csvProcessor = new CsvProcessor();
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		}
		return dataSource;
	}
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}
	
	/**
	 * Gets the csv resource.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @return the csv resource
	 */
	public IResource getCsvResource(CsvFileDefinition csvFileDefinition) {
		IResource resource = getRepository().getResource(convertToActualFileName(csvFileDefinition.getFile()));
		return resource;
	}
	
	/**
	 * Gets the csv content.
	 *
	 * @param resource the resource
	 * @return the csv content
	 * @throws RepositoryReadException the repository read exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String getCsvContent(IResource resource) throws RepositoryReadException, IOException {
		return IOUtils.toString(new InputStreamReader(new ByteArrayInputStream(resource.getContent()), StandardCharsets.UTF_8));
	}

	/**
	 * Process.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @param content the content
	 * @param connection the connection
	 * @throws CsvimException the csvim exception
	 * @throws SQLException the SQL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void process(CsvFileDefinition csvFileDefinition, String content, Connection connection) throws CsvimException, SQLException, IOException {
		String tableName = convertToActualTableName(csvFileDefinition.getTable());
		CSVParser csvParser = getCsvParser(csvFileDefinition, content);
		PersistenceTableModel tableMetadata = getTableMetadata(csvFileDefinition);
		if (tableMetadata == null || csvParser == null) {
			return;
		}

		List<CSVRecord> recordsToInsert = new ArrayList<>();
	    List<CSVRecord> recordsToUpdate = new ArrayList<>();

	    String pkNameForCSVRecord = getPkNameForCSVRecord(tableName, csvParser.getHeaderNames());
	    
		Map<String, List<String>> keysMap = csvFileDefinition.getKeysAsMap();
		List<CSVRecord> csvRecords = csvParser.getRecords();
		int maxCompareSize = DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE_DEFAULT;
		try {
			maxCompareSize = Integer.parseInt(Configuration.get(DIRIGIBLE_CSV_DATA_MAX_COMPARE_SIZE));
		} catch (NumberFormatException e) {
			// Do nothing
		}
		List<PersistenceTableColumnModel> tableColumns = tableMetadata.getColumns();
		if (csvRecords.size() > maxCompareSize) {
			if (isEmptyTable(tableName, connection)) {
				recordsToInsert = csvRecords.stream().filter(e -> recordShouldBeIncluded(e, tableColumns, keysMap)).collect(Collectors.toList());
			}
		} else {
			for (CSVRecord csvRecord : csvRecords) {
				if (recordShouldBeIncluded(csvRecord, tableColumns, keysMap)) {

					String pkValueForCSVRecord = getPkValueForCSVRecord(csvRecord, tableName, csvParser.getHeaderNames());

					if (pkValueForCSVRecord == null) {
						logProcessorErrors(PROBLEM_MESSAGE_NO_PRIMARY_KEY, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
						throw new CsvimException(String.format(ERROR_MESSAGE_NO_PRIMARY_KEY, csvFileDefinition.getFile()));
					}

					if (csvRecord.size() != tableColumns.size()) {
						logProcessorErrors(String.format(PROBLEM_MESSAGE_DIFFERENT_COLUMNS_SIZE, pkValueForCSVRecord), ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
						throw new CsvimException(String.format(ERROR_MESSAGE_DIFFERENT_COLUMNS_SIZE, pkValueForCSVRecord, csvFileDefinition.getFile()));
					}

					if (!recordExists(tableName, pkNameForCSVRecord, pkValueForCSVRecord, connection)) {
						recordsToInsert.add(csvRecord);
					} else {
						recordsToUpdate.add(csvRecord);
					}
				}
			}
		}

		insertCsvRecords(recordsToInsert, csvParser.getHeaderNames(), csvFileDefinition);
		updateCsvRecords(recordsToUpdate, csvParser.getHeaderNames(), csvFileDefinition);
	}

	/**
	 * Checks if is empty table.
	 *
	 * @param tableName the table name
	 * @param connection the connection
	 * @return true, if is empty table
	 * @throws SQLException the SQL exception
	 */
	private boolean isEmptyTable(String tableName, Connection connection) throws SQLException {
		boolean isEmpty = false;
		SelectBuilder selectBuilder = new SelectBuilder(SqlFactory.deriveDialect(connection));
		String sql = selectBuilder.column("COUNT(*)").from(tableName).build();
		try (PreparedStatement pstmt = connection.prepareCall(sql)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				isEmpty = rs.getInt(1) == 0;
			}
		}
		return isEmpty;
	}

	/**
	 * Record exists.
	 *
	 * @param tableName the table name
	 * @param pkNameForCSVRecord the pk name for CSV record
	 * @param pkValueForCSVRecord the pk value for CSV record
	 * @param connection the connection
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 */
	private boolean recordExists(String tableName, String pkNameForCSVRecord, String pkValueForCSVRecord, Connection connection) throws SQLException {
		boolean exists = false;
		SelectBuilder selectBuilder = new SelectBuilder(SqlFactory.deriveDialect(connection));
		String sql = selectBuilder.distinct().column("1 " + pkNameForCSVRecord).from(tableName).where(pkNameForCSVRecord + " = ?").build();
		try (PreparedStatement pstmt = connection.prepareCall(sql)) {
			ResultSet rs = null;
			try {
				pstmt.setString(1, pkValueForCSVRecord);
				rs = pstmt.executeQuery();
			} catch (Throwable e) {
				pstmt.setInt(1, Integer.valueOf(pkValueForCSVRecord));
				rs = pstmt.executeQuery();
			}
			if (rs.next()) {
				try {
					exists = "1".equals(rs.getString(1));
				} catch(Throwable e) {
					exists = 1 == rs.getInt(1);
				}
			}
		}
		return exists;
	}

	/**
	 * Insert csv records.
	 *
	 * @param recordsToProcess the records to process
	 * @param headerNames the header names
	 * @param csvFileDefinition the csv file definition
	 * @throws SQLException the SQL exception
	 */
	private void insertCsvRecords(List<CSVRecord> recordsToProcess, List<String> headerNames, CsvFileDefinition csvFileDefinition) throws SQLException {
		String tableName = convertToActualTableName(csvFileDefinition.getTable());
		PersistenceTableModel tableModel = databaseMetadataUtil.getTableMetadata(tableName, DatabaseMetadataUtil.getTableSchema(getDataSource(), tableName));

		CsvRecordDefinition csvRecordDefinition = null;

		try {
			for (List<CSVRecord> csvBatch : Lists.partition(recordsToProcess, getCsvDataBatchSize())) {
				List<CsvRecordDefinition> csvRecordDefinitions = csvBatch.stream().map(
						e -> new CsvRecordDefinition(e, tableModel, headerNames, csvFileDefinition.getDistinguishEmptyFromNull()))
						.collect(Collectors.toList()
				);
				csvProcessor.insert(csvRecordDefinitions, csvFileDefinition);
			}
		} catch (SQLException e) {
			String csvRecordValue = csvRecordDefinition != null ? csvRecordDefinition.getCsvRecord().toString() : "<empty>";
			logProcessorErrors(String.format(PROBLEM_MESSAGE_INSERT_RECORD, tableName, csvRecordValue), ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			logger.error(String.format(ERROR_MESSAGE_INSERT_RECORD, tableName, csvRecordValue, csvFileDefinition.getFile()), e);
		}
	}

	/**
	 * Update csv records.
	 *
	 * @param recordsToProcess the records to process
	 * @param headerNames the header names
	 * @param csvFileDefinition the csv file definition
	 * @throws SQLException the SQL exception
	 */
	private void updateCsvRecords(List<CSVRecord> recordsToProcess, List<String> headerNames, CsvFileDefinition csvFileDefinition) throws SQLException {
		String tableName = convertToActualTableName(csvFileDefinition.getTable());
		PersistenceTableModel tableModel = databaseMetadataUtil.getTableMetadata(tableName, DatabaseMetadataUtil.getTableSchema(getDataSource(), tableName));

		CsvRecordDefinition csvRecordDefinition = null;

		try {
			for (List<CSVRecord> csvBatch : Lists.partition(recordsToProcess, getCsvDataBatchSize())) {
				List<CsvRecordDefinition> csvRecordDefinitions = csvBatch.stream().map(
						e -> new CsvRecordDefinition(e, tableModel, headerNames, csvFileDefinition.getDistinguishEmptyFromNull()))
						.collect(Collectors.toList()
				);
				csvProcessor.update(csvRecordDefinitions, csvFileDefinition);
			}
		} catch (SQLException e) {
			String csvRecordValue = csvRecordDefinition != null ? csvRecordDefinition.getCsvRecord().toString() : "<empty>";
			logProcessorErrors(String.format(PROBLEM_MESSAGE_INSERT_RECORD, tableName, csvRecordValue), ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			logger.error(String.format(ERROR_MESSAGE_INSERT_RECORD, tableName, csvRecordValue, csvFileDefinition.getFile()), e);
		}
	}

	/**
	 * Gets the csv data batch size.
	 *
	 * @return the csv data batch size
	 */
	private int getCsvDataBatchSize() {
		int batchSize = DIRIGIBLE_CSV_DATA_BATCH_SIZE_DEFAULT;
		try {
			batchSize = Integer.parseInt(Configuration.get(DIRIGIBLE_CSV_DATA_BATCH_SIZE));
		} catch (NumberFormatException e) {
			// Do nothing
		}
		return batchSize;
	}

	/**
	 * Record should be included.
	 *
	 * @param record the record
	 * @param columns the columns
	 * @param keys the keys
	 * @return true, if successful
	 */
	private boolean recordShouldBeIncluded(CSVRecord record, List<PersistenceTableColumnModel> columns,
			Map<String, List<String>> keys) {
		if (keys == null || keys.isEmpty()) {
			return true;
		}

		boolean match = false;
		for (PersistenceTableColumnModel column : columns) {
			String columnName = column.getName();
			List<String> values = keys.get(columnName);
			values = keys.get(columnName) == null ? keys.get(columnName.toLowerCase()) : values;
			if (values != null) {
				match = values.contains(record.get(columns.indexOf(column)));
			}
		}

		return match;
	}

	/**
	 * Gets the table metadata.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @return the table metadata
	 */
	private PersistenceTableModel getTableMetadata(CsvFileDefinition csvFileDefinition) {
		String tableName = convertToActualTableName(csvFileDefinition.getTable());
		try {
			return databaseMetadataUtil.getTableMetadata(tableName, csvFileDefinition.getSchema());
		} catch (SQLException sqlException) {
			String errorMessage = String.format("Error occurred while trying to read metadata for table [%s].", tableName);
			logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			logger.error(errorMessage, sqlException);
		}
		return null;
	}
	
	/**
	 * Gets the csv parser.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @param contentAsString the content as string
	 * @return the csv parser
	 * @throws CsvimException the csvim exception
	 */
	private CSVParser getCsvParser(CsvFileDefinition csvFileDefinition, String contentAsString) throws CsvimException {
		try {
			CSVFormat csvFormat = createCSVFormat(csvFileDefinition);
			return CSVParser.parse(contentAsString, csvFormat);
		} catch (IOException e) {
			String errorMessage = String.format("Error occurred while trying to parse data from CSV file [%s].", csvFileDefinition.getFile());
			logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			logger.error(errorMessage, e);
		}

		return null;
	}

	/**
	 * Creates the CSV format.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @return the CSV format
	 * @throws CsvimException the csvim exception
	 */
	private CSVFormat createCSVFormat(CsvFileDefinition csvFileDefinition) throws CsvimException {
		if (csvFileDefinition.getDelimField() != null && (!csvFileDefinition.getDelimField().equals(",") && !csvFileDefinition.getDelimField().equals(";"))) {
			String errorMessage = "Only ';' or ',' characters are supported as delimiters for CSV files.";
			logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			throw new CsvimException(errorMessage);
		} else if (csvFileDefinition.getDelimEnclosing() != null && csvFileDefinition.getDelimEnclosing().length() > 1) {
			String errorMessage = "Delim enclosing should only contain one character.";
			logProcessorErrors(errorMessage, ERROR_TYPE_PROCESSOR, csvFileDefinition.getFile(), ARTEFACT_TYPE_CSV);
			throw new CsvimException(errorMessage);
		}

		char delimiter = Objects.isNull(csvFileDefinition.getDelimField()) ? ',' : csvFileDefinition.getDelimField().charAt(0);
		char quote = Objects.isNull(csvFileDefinition.getDelimEnclosing()) ? '"' : csvFileDefinition.getDelimEnclosing().charAt(0);
		CSVFormat csvFormat = CSVFormat.newFormat(delimiter).withIgnoreEmptyLines().withQuote(quote).withEscape('\\');

		boolean useHeader = !Objects.isNull(csvFileDefinition.getHeader()) && csvFileDefinition.getHeader();
		if (useHeader) {
			csvFormat = csvFormat.withFirstRecordAsHeader();
		}

		return csvFormat;
	}

	/**
	 * Use to log errors from artifact processing.
	 *
	 * @param errorMessage the error message
	 * @param errorType the error type
	 * @param location the location
	 * @param artifactType the artifact type
	 */
	private static void logProcessorErrors(String errorMessage, String errorType, String location, String artifactType) {
		try {
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, CsvimProcessor.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			logger.error("There is an issue with logging of the Errors.");
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Gets the pk name for CSV record.
	 *
	 * @param tableName the table name
	 * @param headerNames the header names
	 * @return the pk name for CSV record
	 */
	private String getPkNameForCSVRecord(String tableName, List<String> headerNames) {
		List<PersistenceTableColumnModel> columnModels = getTableMetadata(tableName).getColumns();
		if (headerNames.size() > 0) {
			String pkColumnName = columnModels.stream().filter(PersistenceTableColumnModel::isPrimaryKey).findFirst().get().getName();
			return pkColumnName;
		}

		for (int i = 0; i < columnModels.size(); i++) {
			if (columnModels.get(i).isPrimaryKey()) {
				return columnModels.get(i).getName();
			}
		}

		return null;
	}

	/**
	 * Gets the pk value for CSV record.
	 *
	 * @param csvRecord the csv record
	 * @param tableName the table name
	 * @param headerNames the header names
	 * @return the pk value for CSV record
	 */
	private String getPkValueForCSVRecord(CSVRecord csvRecord, String tableName, List<String> headerNames) {
		List<PersistenceTableColumnModel> columnModels = getTableMetadata(tableName).getColumns();
		if (headerNames.size() > 0) {
			String pkColumnName = columnModels.stream().filter(PersistenceTableColumnModel::isPrimaryKey).findFirst().get().getName();
			int csvRecordPkValueIndex = headerNames.indexOf(pkColumnName);
			return csvRecordPkValueIndex >= 0 ? csvRecord.get(csvRecordPkValueIndex) : null;
		}

		for (int i = 0; i < csvRecord.size(); i++) {
			boolean isColumnPk = columnModels.get(i).isPrimaryKey();
			if (isColumnPk && !StringUtils.isEmpty(csvRecord.get(i))) {
				return csvRecord.get(i);
			}
		}

		return null;
	}

	/**
	 * Convert to actual table name.
	 *
	 * @param tableName the table name
	 * @return the string
	 */
	private String convertToActualTableName(String tableName) {
		boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
		if (caseSensitive) {
			tableName = "\"" + tableName + "\"";
		}
		return tableName;
	}

	/**
	 * Convert to actual file name.
	 *
	 * @param fileNamePath the file name path
	 * @return the string
	 */
	private String convertToActualFileName(String fileNamePath) {
		return IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepository.SEPARATOR + fileNamePath;
	}

	/**
	 * Gets the table metadata.
	 *
	 * @param tableName the table name
	 * @return the table metadata
	 */
	private PersistenceTableModel getTableMetadata(String tableName) {
		try {
			return databaseMetadataUtil.getTableMetadata(tableName, DatabaseMetadataUtil.getTableSchema(getDataSource(), tableName));
		} catch (SQLException sqlException) {
			logger.error(String.format("Error occurred while trying to read table metadata for table with name: %s", tableName), sqlException);
		}
		return null;
	}

	// TODO delete records logic ?

}
