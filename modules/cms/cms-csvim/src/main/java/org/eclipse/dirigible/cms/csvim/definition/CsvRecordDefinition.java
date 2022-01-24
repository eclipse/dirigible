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

public class CsvRecordDefinition {

	private CSVRecord csvRecord;
	private PersistenceTableModel tableMetadataModel;
	private List<String> headerNames;
	private boolean distinguishEmptyFromNull;
	private String pkColumnName;
	private String csvRecordPkValue;

	public CsvRecordDefinition(CSVRecord csvRecord, PersistenceTableModel tableMetadataModel, List<String> headerNames,
			boolean distinguishEmptyFromNull) {
		this.csvRecord = csvRecord;
		this.tableMetadataModel = tableMetadataModel;
		this.headerNames = headerNames;
		this.distinguishEmptyFromNull = distinguishEmptyFromNull;
	}

	public String getCsvValueForColumn(String columnName) {
		if (headerNames.size() > 0) {
			int csvValueIndex = headerNames.indexOf(columnName);
			return csvRecord.get(csvValueIndex);
		}

		return null;
	}

	public String getPkColumnName() {
		if (pkColumnName == null) {
			pkColumnName = tableMetadataModel.getColumns().stream().filter(c -> c.isPrimaryKey()).findFirst().get()
					.getName();
		}

		return pkColumnName;
	}

	public String getCsvRecordPkValue() {
		if (csvRecordPkValue == null && headerNames.size() > 0) {
			csvRecordPkValue = getCsvValueForColumn(getPkColumnName());
		} else if (csvRecordPkValue == null) {
			csvRecordPkValue = csvRecord.get(0);
		}

		return csvRecordPkValue;
	}

	public CSVRecord getCsvRecord() {
		return csvRecord;
	}

	public PersistenceTableModel getTableMetadataModel() {
		return tableMetadataModel;
	}

	public List<String> getHeaderNames() {
		return headerNames;
	}

	public boolean isDistinguishEmptyFromNull() {
		return distinguishEmptyFromNull;
	}

}
