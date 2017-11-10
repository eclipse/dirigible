/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.ColumnsIteratorCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.IndicesIteratorCallback;

public class TableMetadata {

	private String name;

	private String type;

	private String remarks;

	private List<ColumnMetadata> columns;

	private List<IndexMetadata> indices;

	private String kind = "table";

	public TableMetadata(String name, String type, String remarks, Connection connection, String catalogName, String schemaName) throws SQLException {
		super();
		this.name = name;
		this.type = type;
		this.remarks = remarks;

		this.columns = new ArrayList<ColumnMetadata>();
		this.indices = new ArrayList<IndexMetadata>();

		DatabaseMetadataHelper.iterateTableDefinition(connection, catalogName, schemaName, name, new ColumnsIteratorCallback() {
			@Override
			public void onColumn(String columnName, String columnType, String columnSize, String isNullable, String isKey) {
				columns.add(new ColumnMetadata(columnName, columnType, columnSize != null ? Integer.parseInt(columnSize) : 0,
						Boolean.parseBoolean(isNullable), Boolean.parseBoolean(isKey)));
			}
		}, new IndicesIteratorCallback() {
			@Override
			public void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier,
					String ordinalPosition, String sortOrder, String cardinality, String pagesIndex, String filterCondition) {
				indices.add(new IndexMetadata(indexName, indexType, columnName, Boolean.parseBoolean(isNonUnique), indexQualifier, ordinalPosition,
						sortOrder, cardinality != null ? Integer.parseInt(cardinality) : 0, pagesIndex != null ? Integer.parseInt(pagesIndex) : 0,
						filterCondition));
			}
		});
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<ColumnMetadata> getColumns() {
		return columns;
	}

	public List<IndexMetadata> getIndices() {
		return indices;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
