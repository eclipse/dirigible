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

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.Table;
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
public class HanaCreateTableBuilder extends CreateTableBuilder<HanaCreateTableBuilder> {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HanaCreateTableBuilder.class);
	
	/** The Constant DELIMITER. */
	private static final String DELIMITER = "; ";
	
	/** The is column table. */
	private final boolean isColumnTable;
	
	/** The indices. */
	private final List<CreateTableIndexBuilder> indices = new ArrayList<>();
	
	/** The unique indices. */
	private final List<CreateTableUniqueIndexBuilder> uniqueIndices = new ArrayList<>();

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

	/**
	 * Generate table.
	 *
	 * @param sql the sql
	 */
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

	/**
	 * Generate.
	 *
	 * @return the string
	 */
	@Override
	public String generate() {
		Table table = buildTable();

		String generated = table.getCreateTableStatement();
		if(!table.getCreateIndicesStatements().isEmpty()) {
			String uniqueIndices = table.getCreateIndicesStatements().stream().collect(Collectors.joining(DELIMITER));
			generated = String.join(DELIMITER, generated, uniqueIndices);
		}

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Build {@link Table} object containing the SQL statements.
	 * @return Table
	 */
	public Table buildTable(){
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

		String createTableStatement = sql.toString();

		Collection<String> createIndicesStatements = new HashSet<>();
		createIndicesStatements.addAll(generateIndices());
		createIndicesStatements.addAll(generateUniqueIndices());

		return new Table(createTableStatement, createIndicesStatements);
	}

	/**
	 * Generate create statements for indices.
	 *
	 * @return Collection of create index statements
	 */
	protected Collection<String> generateUniqueIndices() {
		Collection<String> indices = new HashSet<>();
		for (CreateTableUniqueIndexBuilder uniqueIndex : this.uniqueIndices) {
			indices.add(generateUniqueIndex(uniqueIndex));
		}

		return indices;
	}


	/**
	 * Generate unique index.
	 *
	 * @param uniqueIndex the unique index
	 * @return Create index statement
	 */
	protected String generateUniqueIndex(CreateTableUniqueIndexBuilder uniqueIndex) {
		StringBuilder sql = new StringBuilder();
		if(uniqueIndex != null){
			sql.append(KEYWORD_CREATE).append(SPACE);
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

		return sql.toString();
	}

	/**
	 * Generate create statements for indices.
	 *
	 * @return Collection of create index statements
	 */
	protected Collection<String> generateIndices() {
		Collection<String> indices = new HashSet<>();
		for (CreateTableIndexBuilder index : this.indices) {
			indices.add(generateIndex(index));
		}

		return indices;
	}

	/**
	 * Generate index create statement.
	 *
	 * @param index IndexBuilder
	 * @return Generated statement
	 */
	protected String generateIndex(CreateTableIndexBuilder index) {
		StringBuilder sql = new StringBuilder();
		if (index != null && !index.isUnique()) {
			sql.append(KEYWORD_CREATE).append(SPACE);
			if(index.getIndexType() != null) {
				sql.append(index.getIndexType()).append(SPACE);
			}
			sql.append(KEYWORD_INDEX).append(SPACE).append(index.getName()).append(SPACE).append(KEYWORD_ON).append(SPACE).append(this.getTable());
			sql.append(SPACE).append(OPEN).append(traverseNames(index.getIndexColumns())).append(CLOSE);
			if (index.getOrder() != null) {
				sql.append(SPACE).append(index.getOrder());
			}
		}

		return sql.toString();
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
    public HanaCreateTableBuilder unique(String name, String[] columns, String type, String order){
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
	public HanaCreateTableBuilder index(String name, Boolean isUnique, String order, String indexType, Set<String> indexColumns) {

			logger.trace("index: " + name + ", columns" + indexColumns);
			CreateTableIndexBuilder index = new CreateTableIndexBuilder(this.getDialect(), name);
			index.setIndexType(indexType);
			index.setOrder(order);
			index.setIndexColumns(indexColumns);
			index.setUnique(isUnique);
			this.indices.add(index);

		return this;
	}
}
