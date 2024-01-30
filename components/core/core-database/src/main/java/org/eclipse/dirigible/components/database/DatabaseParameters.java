/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.database;

public interface DatabaseParameters {

    /** Whether or not to use case sensitive syntax for table or view names and column names. */
    public static final String DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE = "DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE";

    /** The Constant DIRIGIBLE_DATABASE_DEFAULT_QUERY_LIMIT. */
    public static final String DIRIGIBLE_DATABASE_DEFAULT_QUERY_LIMIT = "DIRIGIBLE_DATABASE_DEFAULT_QUERY_LIMIT";

    /** DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT. */
    public static final String DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT = "DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT"; //$NON-NLS-1$

    /** DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT. */
    public static final String DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT = "DefaultDB"; //$NON-NLS-1$

    /** DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM. */
    public static final String DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM = "DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM"; //$NON-NLS-1$

    /** DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM. */
    public static final String DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM = "SystemDB"; //$NON-NLS-1$

    /** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER. */
    public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER = "DIRIGIBLE_DATABASE_H2_ROOT_FOLDER"; //$NON-NLS-1$

    /** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT. */
    public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT = DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + "_DEFAULT"; //$NON-NLS-1$


    /** The Constant SYSTEM_TABLE. */
    public static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$

    /** The Constant LOCAL_TEMPORARY. */
    public static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$

    /** The Constant GLOBAL_TEMPORARY. */
    public static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$

    /** The Constant SYNONYM. */
    public static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$

    /** The Constant ALIAS. */
    public static final String ALIAS = "ALIAS"; //$NON-NLS-1$

    /** The Constant VIEW. */
    public static final String VIEW = "VIEW"; //$NON-NLS-1$

    /** The Constant TABLE. */
    public static final String TABLE = "TABLE"; //$NON-NLS-1$

    /** The Constant TABLE_TYPES. */
    public static final String[] TABLE_TYPES = {TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, SYSTEM_TABLE};

    /** The Constant PRCNT. */
    public static final String PRCNT = "%"; //$NON-NLS-1$

    /** The Constant COLUMN_NAME. */
    public static final String COLUMN_NAME = "COLUMN_NAME"; //$NON-NLS-1$

    /** The Constant COLUMN_TYPE. */
    public static final String COLUMN_TYPE = "COLUMN_TYPE"; //$NON-NLS-1$

    /** The Constant TYPE_NAME. */
    public static final String TYPE_NAME = "TYPE_NAME"; //$NON-NLS-1$

    /** The Constant COLUMN_SIZE. */
    public static final String COLUMN_SIZE = "COLUMN_SIZE"; //$NON-NLS-1$

    /** The Constant EMPTY. */
    public static final String EMPTY = ""; //$NON-NLS-1$

    /** The Constant PK. */
    public static final String PK = "PK"; //$NON-NLS-1$

    /** The Constant IS_NULLABLE. */
    public static final String IS_NULLABLE = "IS_NULLABLE"; //$NON-NLS-1$

    /** The Constant INDEX_NAME. */
    public static final String INDEX_NAME = "INDEX_NAME"; //$NON-NLS-1$

    /** The Constant TYPE_INDEX. */
    public static final String TYPE_INDEX = "TYPE"; //$NON-NLS-1$

    /** The Constant NON_UNIQUE. */
    public static final String NON_UNIQUE = "NON_UNIQUE"; //$NON-NLS-1$

    /** The Constant INDEX_QUALIFIER. */
    public static final String INDEX_QUALIFIER = "INDEX_QUALIFIER"; //$NON-NLS-1$

    /** The Constant ORDINAL_POSITION. */
    public static final String ORDINAL_POSITION = "ORDINAL_POSITION"; //$NON-NLS-1$

    /** The Constant ASC_OR_DESC. */
    public static final String ASC_OR_DESC = "ASC_OR_DESC"; //$NON-NLS-1$

    /** The Constant CARDINALITY. */
    public static final String CARDINALITY = "CARDINALITY"; //$NON-NLS-1$

    /** The Constant PAGES_INDEX. */
    public static final String PAGES_INDEX = "PAGES"; //$NON-NLS-1$

    /** The Constant FILTER_CONDITION. */
    public static final String FILTER_CONDITION = "FILTER_CONDITION"; //$NON-NLS-1$

    /** The Constant PRECISION. */
    public static final String PRECISION = "PRECISION"; //$NON-NLS-1$

    /** The Constant LENGTH. */
    public static final String LENGTH = "LENGTH"; //$NON-NLS-1$

    /** The Constant SCALE. */
    public static final String SCALE = "SCALE"; //$NON-NLS-1$

    /** The Constant RADIX. */
    public static final String RADIX = "RADIX"; //$NON-NLS-1$

    /** The Constant NULLABLE. */
    public static final String NULLABLE = "NULLABLE"; //$NON-NLS-1$

    /** The Constant REMARKS. */
    public static final String REMARKS = "REMARKS"; //$NON-NLS-1$

    /** The Constant DECIMAL_DIGITS. */
    public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS"; //$NON-NLS-1$

    /** The Constant FK_NAME. */
    public static final String FK_NAME = "FK_NAME"; //$NON-NLS-1$


    /** The Constant JDBC_TABLE_NAME_PROPERTY. */
    public static final String JDBC_TABLE_NAME_PROPERTY = "TABLE_NAME";

    /** The Constant JDBC_TABLE_SCHEME_PROPERTY. */
    public static final String JDBC_TABLE_SCHEME_PROPERTY = "TABLE_SCHEM";

    /** The Constant JDBC_TABLE_TYPE_PROPERTY. */
    public static final String JDBC_TABLE_TYPE_PROPERTY = "TABLE_TYPE";

    /** The Constant JDBC_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_COLUMN_NAME_PROPERTY = "COLUMN_NAME";

    /** The Constant JDBC_COLUMN_TYPE_PROPERTY. */
    public static final String JDBC_COLUMN_TYPE_PROPERTY = "TYPE_NAME";

    /** The Constant JDBC_COLUMN_NULLABLE_PROPERTY. */
    public static final String JDBC_COLUMN_NULLABLE_PROPERTY = "NULLABLE";

    /** The Constant JDBC_COLUMN_SIZE_PROPERTY. */
    public static final String JDBC_COLUMN_SIZE_PROPERTY = "COLUMN_SIZE";

    /** The Constant JDBC_COLUMN_DECIMAL_DIGITS_PROPERTY. */
    public static final String JDBC_COLUMN_DECIMAL_DIGITS_PROPERTY = "DECIMAL_DIGITS";

    /** The Constant JDBC_FK_TABLE_NAME_PROPERTY. */
    public static final String JDBC_FK_TABLE_NAME_PROPERTY = "FKTABLE_NAME";

    /** The Constant JDBC_FK_NAME_PROPERTY. */
    public static final String JDBC_FK_NAME_PROPERTY = "FK_NAME";

    /** The Constant JDBC_PK_NAME_PROPERTY. */
    public static final String JDBC_PK_NAME_PROPERTY = "PK_NAME";

    /** The Constant JDBC_PK_TABLE_NAME_PROPERTY. */
    public static final String JDBC_PK_TABLE_NAME_PROPERTY = "PKTABLE_NAME";

    /** The Constant JDBC_PK_TABLE_NAME_PROPERTY. */
    public static final String JDBC_PK_SCHEMA_NAME_PROPERTY = "PKTABLE_SCHEM";

    /** The Constant JDBC_FK_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_FK_COLUMN_NAME_PROPERTY = "FKCOLUMN_NAME";

    /** The Constant JDBC_PK_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_PK_COLUMN_NAME_PROPERTY = "PKCOLUMN_NAME";

    /** The Constant JDBC_FILTER_CONDITION_PROPERTY. */
    public static final String JDBC_FILTER_CONDITION_PROPERTY = "FILTER_CONDITION";

}
