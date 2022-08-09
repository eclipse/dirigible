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
package org.eclipse.dirigible.cms.csvim.definition;

import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;

/**
 * The Class CsvRecordDefinition.
 */
public class CsvRecordDefinition {

	/** The csv record. */
	private CSVRecord csvRecord;
	
	/** The table metadata model. */
	private PersistenceTableModel tableMetadataModel;
	
	/** The header names. */
	private List<String> headerNames;
	
	/** The distinguish empty from null. */
	private boolean distinguishEmptyFromNull;
	
	/** The pk column name. */
	private String pkColumnName;
	
	/** The csv record pk value. */
	private String csvRecordPkValue;

	/**
	 * Instantiates a new csv record definition.
	 *
	 * @param csvRecord the csv record
	 * @param tableMetadataModel the table metadata model
	 * @param headerNames the header names
	 * @param distinguishEmptyFromNull the distinguish empty from null
	 */
	public CsvRecordDefinition(CSVRecord csvRecord, PersistenceTableModel tableMetadataModel, List<String> headerNames,
			boolean distinguishEmptyFromNull) {
		this.csvRecord = csvRecord;
		this.tableMetadataModel = tableMetadataModel;
		this.headerNames = headerNames;
		this.distinguishEmptyFromNull = distinguishEmptyFromNull;
	}

	/**
	 * Gets the csv value for column.
	 *
	 * @param columnName the column name
	 * @return the csv value for column
	 */
	public String getCsvValueForColumn(String columnName) {
		if (headerNames.size() > 0) {
			int csvValueIndex = headerNames.indexOf(columnName);
			return csvRecord.get(csvValueIndex);
		}

		return null;
	}

	/**
	 * Gets the pk column name.
	 *
	 * @return the pk column name
	 */
	public String getPkColumnName() {
		if (pkColumnName == null) {
			pkColumnName = tableMetadataModel.getColumns().stream().filter(c -> c.isPrimaryKey()).findFirst().get()
					.getName();
		}

		return pkColumnName;
	}

	/**
	 * Gets the csv record pk value.
	 *
	 * @return the csv record pk value
	 */
	public String getCsvRecordPkValue() {
		if (csvRecordPkValue == null && headerNames.size() > 0) {
			csvRecordPkValue = getCsvValueForColumn(getPkColumnName());
		} else if (csvRecordPkValue == null) {
			csvRecordPkValue = csvRecord.get(0);
		}

		return csvRecordPkValue;
	}

	/**
	 * Gets the csv record.
	 *
	 * @return the csv record
	 */
	public CSVRecord getCsvRecord() {
		return csvRecord;
	}

	/**
	 * Gets the table metadata model.
	 *
	 * @return the table metadata model
	 */
	public PersistenceTableModel getTableMetadataModel() {
		return tableMetadataModel;
	}

	/**
	 * Gets the header names.
	 *
	 * @return the header names
	 */
	public List<String> getHeaderNames() {
		return headerNames;
	}

	/**
	 * Checks if is distinguish empty from null.
	 *
	 * @return true, if is distinguish empty from null
	 */
	public boolean isDistinguishEmptyFromNull() {
		return distinguishEmptyFromNull;
	}

}
