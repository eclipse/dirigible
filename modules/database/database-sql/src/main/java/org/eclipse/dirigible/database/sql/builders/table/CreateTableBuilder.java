/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Create Table Builder.
 */
public class CreateTableBuilder extends AbstractTableBuilder<CreateTableBuilder> {

    private static final Logger logger = LoggerFactory.getLogger(CreateTableBuilder.class);

    private CreateTablePrimaryKeyBuilder primaryKey;

    private List<CreateTableForeignKeyBuilder> foreignKeys = new ArrayList<>();

    private List<CreateTableUniqueIndexBuilder> uniqueIndices = new ArrayList<>();

    private List<CreateTableCheckBuilder> checks = new ArrayList<>();

    /**
     * Instantiates a new creates the table builder.
     *
     * @param dialect the dialect
     * @param table   the table
     */
    public CreateTableBuilder(ISqlDialect dialect, String table) {
        super(dialect, table);
    }

    /**
     * Primary key.
     *
     * @param name    the name
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder primaryKey(String name, String[] columns) {
        logger.trace("primaryKey: " + name + ", columns" + Arrays.toString(columns));
        if (this.primaryKey != null) {
            throw new SqlException("Setting of primary key must be called only once");
        }
        this.primaryKey = new CreateTablePrimaryKeyBuilder(this.getDialect(), name);
        for (String column : columns) {
            this.primaryKey.column(column);
        }
        return this;
    }

    /**
     * Primary key.
     *
     * @param name    the name
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder primaryKey(String name, String columns) {
        logger.trace("primaryKey: " + name + ", columns" + columns);
        String[] array = splitValues(columns);
        return primaryKey(name, array);
    }

    /**
     * Primary key.
     *
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder primaryKey(String[] columns) {
        logger.trace("primaryKey: <unnamed>, columns" + Arrays.toString(columns));
        return primaryKey(null, columns);
    }

    /**
     * Primary key.
     *
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder primaryKey(String columns) {
        logger.trace("primaryKey: <unnamed>, columns" + columns);
        return primaryKey(null, splitValues(columns));
    }

    /**
     * Foreign key.
     *
     * @param name              the name
     * @param columns           the columns
     * @param referencedTable   the referenced table
     * @param referencedColumns the referenced columns
     * @return the creates the table builder
     */
    public CreateTableBuilder foreignKey(String name, String[] columns, String referencedTable, String[] referencedColumns) {
        logger.trace("foreignKey: " + name + ", columns" + Arrays.toString(columns) + ", referencedTable: " + referencedTable
                + ", referencedColumns: " + Arrays.toString(referencedColumns));
        CreateTableForeignKeyBuilder foreignKey = new CreateTableForeignKeyBuilder(this.getDialect(), name);
        for (String column : columns) {
            foreignKey.column(column);
        }
        foreignKey.referencedTable(referencedTable);
        for (String column : referencedColumns) {
            foreignKey.referencedColumn(column);
        }
        this.foreignKeys.add(foreignKey);
        return this;
    }

    /**
     * Foreign key.
     *
     * @param name              the name
     * @param columns           the columns
     * @param referencedTable   the referenced table
     * @param referencedColumns the referenced columns
     * @return the creates the table builder
     */
    public CreateTableBuilder foreignKey(String name, String columns, String referencedTable, String referencedColumns) {
        logger.trace("foreignKey: " + name + ", columns" + columns + ", referencedTable: " + referencedTable + ", referencedColumns: "
                + referencedColumns);
        return foreignKey(name, splitValues(columns), referencedTable, splitValues(referencedColumns));
    }

    /**
     * Unique.
     *
     * @param name    the name
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder unique(String name, String[] columns) {
        logger.trace("unique: " + name + ", columns" + Arrays.toString(columns));
        CreateTableUniqueIndexBuilder uniqueIndex = new CreateTableUniqueIndexBuilder(this.getDialect(), name);
        for (String column : columns) {
            uniqueIndex.column(column);
        }
        this.uniqueIndices.add(uniqueIndex);
        return this;
    }

    /**
     * Unique.
     *
     * @param name    the name
     * @param columns the columns
     * @return the creates the table builder
     */
    public CreateTableBuilder unique(String name, String columns) {
        logger.trace("unique: " + name + ", columns" + columns);
        return unique(name, splitValues(columns));
    }

    /**
     * Check.
     *
     * @param name       the name
     * @param expression the expression
     * @return the creates the table builder
     */
    public CreateTableBuilder check(String name, String expression) {
        logger.trace("check: " + name + ", expression" + expression);
        CreateTableCheckBuilder check = new CreateTableCheckBuilder(this.getDialect(), name);
        check.expression(expression);
        this.checks.add(check);
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
     */
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

        // UNIQUE INDICES
        generateUniqueIndices(sql);

        // INDICES
        generateChecks(sql);

        sql.append(CLOSE);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }

    /**
     * Generate primary key.
     *
     * @param sql the sql
     */
    protected void generatePrimaryKey(StringBuilder sql) {
        List<String[]> allPrimaryKeys = this.getColumns().stream().filter(el -> Arrays.stream(el).anyMatch(x -> x.equals(getDialect().getPrimaryKeyArgument()))).collect(Collectors.toList());
        boolean isCompositeKey = allPrimaryKeys.size() > 1;

        if ((this.primaryKey != null) && allPrimaryKeys.size() == 0 && !this.primaryKey.getColumns().isEmpty()) {
            sql.append(COMMA).append(SPACE);
            if (this.primaryKey.getName() != null) {
                String primaryKeyName = (isCaseSensitive()) ? encapsulate(this.primaryKey.getName()) : this.primaryKey.getName();
                sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(primaryKeyName).append(SPACE);
            }
            sql.append(KEYWORD_PRIMARY).append(SPACE).append(KEYWORD_KEY).append(SPACE).append(OPEN)
                    .append(traverseNames(this.primaryKey.getColumns())).append(CLOSE);
        } else {
            if (isCompositeKey) {
                sql.append(COMMA).append(SPACE);
                ArrayList<String> keys = new ArrayList<>();
                allPrimaryKeys.forEach(el -> keys.add(el[0]));
                sql.append(KEYWORD_PRIMARY).append(SPACE).append(KEYWORD_KEY).append(OPEN).append(String.join(" , ", keys)).append(CLOSE).append(SPACE);
            }
        }
    }

    /**
     * Generate foreign keys.
     *
     * @param sql the sql
     */
    protected void generateForeignKeys(StringBuilder sql) {
        for (CreateTableForeignKeyBuilder foreignKey : this.foreignKeys) {
            generateForeignKey(sql, foreignKey);
        }
    }

    /**
     * Generate foreign key.
     *
     * @param sql        the sql
     * @param foreignKey the foreign key
     */
    protected void generateForeignKey(StringBuilder sql, CreateTableForeignKeyBuilder foreignKey) {
        if (foreignKey != null) {
            sql.append(COMMA).append(SPACE);
            if (foreignKey.getName() != null) {
                String foreignKeyName = (isCaseSensitive()) ? encapsulate(foreignKey.getName()) : foreignKey.getName();
                sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(foreignKeyName).append(SPACE);
            }
            String referencedTableName = (isCaseSensitive()) ? encapsulate(foreignKey.getReferencedTable()) : foreignKey.getReferencedTable();
            sql.append(KEYWORD_FOREIGN).append(SPACE).append(KEYWORD_KEY).append(SPACE).append(OPEN)
                    .append(traverseNames(foreignKey.getColumns())).append(CLOSE).append(SPACE).append(KEYWORD_REFERENCES).append(SPACE)
                    .append(referencedTableName).append(OPEN).append(traverseNames(foreignKey.getReferencedColumns()))
                    .append(CLOSE);
        }
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
    protected void generateUniqueIndex(StringBuilder sql, CreateTableUniqueIndexBuilder uniqueIndex) {
        if (uniqueIndex != null) {
            sql.append(COMMA).append(SPACE);
            if (uniqueIndex.getName() != null) {
                String uniqueIndexName = (isCaseSensitive()) ? encapsulate(uniqueIndex.getName()) : uniqueIndex.getName();
                sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(uniqueIndexName).append(SPACE);
            }
            sql.append(KEYWORD_UNIQUE).append(SPACE).append(OPEN).append(traverseNames(uniqueIndex.getColumns())).append(CLOSE);
        }
    }

    /**
     * Generate checks.
     *
     * @param sql the sql
     */
    protected void generateChecks(StringBuilder sql) {
        for (CreateTableCheckBuilder index : this.checks) {
            generateCheck(sql, index);
        }
    }

    /**
     * Generate check.
     *
     * @param sql   the sql
     * @param check the check
     */
    protected void generateCheck(StringBuilder sql, CreateTableCheckBuilder check) {
        if (check != null) {
            sql.append(COMMA).append(SPACE);
            if (check.getName() != null) {
                String checkName = (isCaseSensitive()) ? encapsulate(check.getName()) : check.getName();
                sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(checkName).append(SPACE);
            }
            sql.append(KEYWORD_CHECK).append(SPACE).append(OPEN).append(check.getExpression()).append(CLOSE);
        }
    }

}
