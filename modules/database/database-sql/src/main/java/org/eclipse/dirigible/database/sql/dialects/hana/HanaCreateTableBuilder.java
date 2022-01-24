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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableIndexBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableUniqueIndexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The HANA Create Table Builder.
 */
public class HanaCreateTableBuilder extends CreateTableBuilder {

	private static final Logger logger = LoggerFactory.getLogger(HanaCreateTableBuilder.class);
	private boolean isColumnTable = true;
	private List<CreateTableIndexBuilder> indexes = new ArrayList<>();
	private List<CreateTableUniqueIndexBuilder> uniqueIndices = new ArrayList<>();

	/**
	 * Instantiates a new hana create table builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param table
	 *            the table
	 * @param isColumnTable
	 *            the is column table
	 */
	public HanaCreateTableBuilder(ISqlDialect dialect, String table, boolean isColumnTable) {
		super(dialect, table);
		this.isColumnTable = isColumnTable;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder#generateTable(java.lang.StringBuilder)
	 */
	@Override
	protected void generateTable(StringBuilder sql) {
		String tableName = (isCaseSensitive()) ? encapsulate(this.getTable()) : this.getTable();
		sql.append(SPACE).append(isColumnTable ? KEYWORD_COLUMN : KEYWORD_ROW).append( SPACE ).append(KEYWORD_TABLE).append(SPACE)
				.append(tableName);
	}

	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// CREATE
		generateCreate(sql);

		// TABLE
		generateTable(sql);

		sql.append(SPACE).append(OPEN);

		// COLUMNS
		generateColumns(sql);

		// PRIMARY KEY
		generatePrimaryKey(sql);

		// FOREIGN KEYS
		generateForeignKeys(sql);

		// INDICES
		generateChecks(sql);

		sql.append(CLOSE);

		// UNIQUE INDICES
		generateUniqueIndices(sql);

		//INDEXES
		generateIndexes(sql);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate unique indices.
	 *
	 * @param sql the sql
	 */
	protected void generateUniqueIndices(StringBuilder sql) {
		for (CreateTableUniqueIndexBuilder uniqueIndex : this.uniqueIndices) {
			generateUniqueIndex(sql, uniqueIndex);
		}
	}


	/**
	 * Generate unique index.
	 *
	 * @param sql         the sql
	 * @param uniqueIndex the unique index
	 */
	@Override
	protected void generateUniqueIndex(StringBuilder sql, CreateTableUniqueIndexBuilder uniqueIndex) {
		if(uniqueIndex != null){
			sql.append(SEMICOLON).append(SPACE).append(KEYWORD_CREATE).append(SPACE);
			sql.append(KEYWORD_UNIQUE).append(SPACE);
			if(uniqueIndex.getIndexType() != null) {
				sql.append(uniqueIndex.getIndexType()).append(SPACE);
			}
			sql.append(KEYWORD_INDEX).append(SPACE);
			if(uniqueIndex.getName() != null) {
				sql.append(uniqueIndex.getName()).append(SPACE);
			}
			sql.append(KEYWORD_ON).append(SPACE).append(this.getTable());
			sql.append(SPACE).append(OPEN).append(traverseNames(uniqueIndex.getColumns())).append(CLOSE);
			if (uniqueIndex.getOrder() != null) {
				sql.append(SPACE).append(uniqueIndex.getOrder());
			}
		}
	}

	/**
	 * Generate indexes.
	 *
	 * @param sql the sql
	 */
	protected void generateIndexes(StringBuilder sql) {
		for (CreateTableIndexBuilder index : this.indexes) {
			generateIndex(sql, index);
		}
	}

	/**
	 * Generate index.
	 *
	 * @param sql the sql
	 * @param index the index
	 */
	protected void generateIndex(StringBuilder sql, CreateTableIndexBuilder index) {
		if (index != null && !index.isUnique()) {
			sql.append(SEMICOLON).append(SPACE).append(KEYWORD_CREATE).append(SPACE);
			if(index.getIndexType() != null) {
				sql.append(index.getIndexType()).append(SPACE);
			}
			sql.append(KEYWORD_INDEX).append(SPACE).append(index.getName()).append(SPACE).append(KEYWORD_ON).append(SPACE).append(this.getTable());
			sql.append(SPACE).append(OPEN).append(traverseNames(index.getIndexColumns())).append(CLOSE);
			if (index.getOrder() != null) {
				sql.append(SPACE).append(index.getOrder());
			}
		}
	}

	/**
     * Unique.
     *
     * @param name    the name
     * @param columns the columns
     * @param type the type
     * @param order the order
     * @return the creates the table builder
     */
    @Override
    public CreateTableBuilder unique(String name, String[] columns, String type, String order){
        logger.trace("unique: " + name + ", columns" + Arrays.toString(columns) + ", indexType " + type + ", order " + order);
        CreateTableUniqueIndexBuilder uniqueIndex = new CreateTableUniqueIndexBuilder(this.getDialect(), name);
        for (String column : columns) {
            uniqueIndex.column(column);
        }
        uniqueIndex.setIndexType(type);
        uniqueIndex.setOrder(order);
        this.uniqueIndices.add(uniqueIndex);
        return this;

    }

	/**
	 * Index.
	 *
	 * @param name    the name
	 * @param isUnique    the isUnique
	 * @param order the order
	 * @param indexType the indexType
	 * @param indexColumns the indexColumns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder index(String name, Boolean isUnique, String order, String indexType, Set<String> indexColumns) {

			logger.trace("index: " + name + ", columns" + indexColumns);
			CreateTableIndexBuilder index = new CreateTableIndexBuilder(this.getDialect(), name);
			index.setIndexType(indexType);
			index.setOrder(order);
			index.setIndexColumns(indexColumns);
			index.setUnique(isUnique);
			this.indexes.add(index);

		return this;
	}
}
