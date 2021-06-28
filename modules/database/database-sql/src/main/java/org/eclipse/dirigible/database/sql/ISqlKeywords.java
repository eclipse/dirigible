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
package org.eclipse.dirigible.database.sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Interface ISqlKeywords.
 */
public interface ISqlKeywords {

    /**
     * The Constant KEYWORD_SELECT.
     */
    public static final String KEYWORD_SELECT = "SELECT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_DISTINCT.
     */
    public static final String KEYWORD_DISTINCT = "DISTINCT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FROM.
     */
    public static final String KEYWORD_FROM = "FROM"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_JOIN.
     */
    public static final String KEYWORD_JOIN = "JOIN"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_INNER.
     */
    public static final String KEYWORD_INNER = "INNER"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_OUTER.
     */
    public static final String KEYWORD_OUTER = "OUTER"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_LEFT.
     */
    public static final String KEYWORD_LEFT = "LEFT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_RIGHT.
     */
    public static final String KEYWORD_RIGHT = "RIGHT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FULL.
     */
    public static final String KEYWORD_FULL = "FULL"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_WHERE.
     */
    public static final String KEYWORD_WHERE = "WHERE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_GROUP_BY.
     */
    public static final String KEYWORD_GROUP_BY = "GROUP BY"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_HAVING.
     */
    public static final String KEYWORD_HAVING = "HAVING"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ORDER_BY.
     */
    public static final String KEYWORD_ORDER_BY = "ORDER BY"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_UNION.
     */
    public static final String KEYWORD_UNION = "UNION"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ASC.
     */
    public static final String KEYWORD_ASC = "ASC"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_DESC.
     */
    public static final String KEYWORD_DESC = "DESC"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_AND.
     */
    public static final String KEYWORD_AND = "AND"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_OR.
     */
    public static final String KEYWORD_OR = "OR"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_AS.
     */
    public static final String KEYWORD_AS = "AS"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ON.
     */
    public static final String KEYWORD_ON = "ON"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_LIMIT.
     */
    public static final String KEYWORD_LIMIT = "LIMIT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_OFFSET.
     */
    public static final String KEYWORD_OFFSET = "OFFSET"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_INSERT.
     */
    public static final String KEYWORD_INSERT = "INSERT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_INTO.
     */
    public static final String KEYWORD_INTO = "INTO"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_VALUES.
     */
    public static final String KEYWORD_VALUES = "VALUES"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_UPDATE.
     */
    public static final String KEYWORD_UPDATE = "UPDATE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_SET.
     */
    public static final String KEYWORD_SET = "SET"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_CREATE.
     */
    public static final String KEYWORD_CREATE = "CREATE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ALTER.
     */
    public static final String KEYWORD_ALTER = "ALTER"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_TABLE.
     */
    public static final String KEYWORD_TABLE = "TABLE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_PRIMARY.
     */
    public static final String KEYWORD_PRIMARY = "PRIMARY"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FOREIGN.
     */
    public static final String KEYWORD_FOREIGN = "FOREIGN"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_REFERENCES.
     */
    public static final String KEYWORD_REFERENCES = "REFERENCES"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_KEY.
     */
    public static final String KEYWORD_KEY = "KEY"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ADD.
     */
    public static final String KEYWORD_ADD = "ADD"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_DROP.
     */
    public static final String KEYWORD_DROP = "DROP"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_DELETE.
     */
    public static final String KEYWORD_DELETE = "DELETE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_NOT.
     */
    public static final String KEYWORD_NOT = "NOT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_NULL.
     */
    public static final String KEYWORD_NULL = "NULL"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_SEQUENCE.
     */
    public static final String KEYWORD_SEQUENCE = "SEQUENCE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_SEQUENCE_START_WITH.
     */
    public static final String KEYWORD_SEQUENCE_START_WITH = "START WITH";

    /**
     * The Constant KEYWORD_SEQUENCE_RESTART_WITH.
     */
    public static final String KEYWORD_SEQUENCE_RESTART_WITH = "RESTART WITH";

    /**
     * The Constant KEYWORD_SEQUENCE_INCREMENT_BY.
     */
    public static final String KEYWORD_SEQUENCE_INCREMENT_BY = "INCREMENT BY";

    /**
     * The Constant KEYWORD_SEQUENCE_MAXVALUE.
     */
    public static final String KEYWORD_SEQUENCE_MAXVALUE = "MAXVALUE";

    /**
     * The Constant KEYWORD_SEQUENCE_NO_MAXVALUE.
     */
    public static final String KEYWORD_SEQUENCE_NO_MAXVALUE = "NO MAXVALUE";

    /**
     * The Constant KEYWORD_SEQUENCE_MINVALUE.
     */
    public static final String KEYWORD_SEQUENCE_MINVALUE = "MINVALUE";

    /**
     * The Constant KEYWORD_SEQUENCE_NO_MINVALUE.
     */
    public static final String KEYWORD_SEQUENCE_NO_MINVALUE = "NO MINVALUE";

    /**
     * The Constant KEYWORD_SEQUENCE_CYCLE.
     */
    public static final String KEYWORD_SEQUENCE_CYCLE = "CYCLE";

    /**
     * The Constant KEYWORD_SEQUENCE_RESET_BY.
     */
    public static final String KEYWORD_SEQUENCE_RESET_BY = "RESET BY";

    /**
     * The Constant KEYWORD_DATABASE_DROP_RESTRICT.
     */
    public static final String KEYWORD_DATABASE_DROP_RESTRICT = "RESTRICT";

    /**
     * The Constant KEYWORD_SEQUENCE_EMPTY_STRING_FOR_BOOLEAN_TYPES.
     */
    public static final String KEYWORD_SEQUENCE_EMPTY_STRING_FOR_BOOLEAN_TYPES = "";

    /**
     * The Constant KEYWORD_SYNONYM.
     */
    public static final String KEYWORD_SYNONYM = "SYNONYM"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_NEXT_VALUE_FOR.
     */
    public static final String KEYWORD_NEXT_VALUE_FOR = "NEXT VALUE FOR"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_UNIQUE.
     */
    public static final String KEYWORD_UNIQUE = "UNIQUE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_INDEX.
     */
    public static final String KEYWORD_INDEX = "INDEX"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_CHECK.
     */
    public static final String KEYWORD_CHECK = "CHECK"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_COLUMN.
     */
    public static final String KEYWORD_COLUMN = "COLUMN"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ROWS.
     */
    public static final String KEYWORD_ROWS = "ROWS"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_VIEW.
     */
    public static final String KEYWORD_VIEW = "VIEW"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FOR_UPDATE.
     */
    public static final String KEYWORD_FOR_UPDATE = "FOR UPDATE"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_CONSTRAINT.
     */
    public static final String KEYWORD_CONSTRAINT = "CONSTRAINT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_START.
     */
    public static final String KEYWORD_START = "START"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_WITH.
     */
    public static final String KEYWORD_WITH = "WITH"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FETCH.
     */
    public static final String KEYWORD_FETCH = "FETCH"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_NEXT.
     */
    public static final String KEYWORD_NEXT = "NEXT"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_ONLY.
     */
    public static final String KEYWORD_ONLY = "ONLY"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_PUBLIC.
     */
    public static final String KEYWORD_PUBLIC = "PUBLIC"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_FOR.
     */
    public static final String KEYWORD_FOR = "FOR"; //$NON-NLS-1$

    /**
     * The Constant KEYWORD_IDENTITY.
     */
    public static final String KEYWORD_IDENTITY = "IDENTITY"; //$NON-NLS-1$

    /**
     * The Constant FUNCTION_CURRENT_DATE.
     */
    public static final String FUNCTION_CURRENT_DATE = "CURRENT_DATE"; //$NON-NLS-1$

    /**
     * The Constant FUNCTION_CURRENT_TIME.
     */
    public static final String FUNCTION_CURRENT_TIME = "CURRENT_TIME"; //$NON-NLS-1$

    /**
     * The Constant FUNCTION_CURRENT_TIMESTAMP.
     */
    public static final String FUNCTION_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$

    /**
     * The Constant COMMA.
     */
    public static final String COMMA = ","; //$NON-NLS-1$

    /**
     * The Constant SPACE.
     */
    public static final String SPACE = " "; //$NON-NLS-1$

    /**
     * The Constant OPEN.
     */
    public static final String OPEN = "("; //$NON-NLS-1$

    /**
     * The Constant CLOSE.
     */
    public static final String CLOSE = ")"; //$NON-NLS-1$

    /**
     * The Constant QUESTION.
     */
    public static final String QUESTION = "?"; //$NON-NLS-1$

    /**
     * The Constant EQUALS.
     */
    public static final String EQUALS = "="; //$NON-NLS-1$

    /**
     * The Constant UNDERSCROE.
     */
    public static final String UNDERSCROE = "_"; //$NON-NLS-1$

    /**
     * The Constant STAR.
     */
    public static final String STAR = "*"; //$NON-NLS-1$

    /**
     * The Constant ALTER.
     */
    public static final String ALTER = "ALTER"; //$NON-NLS-1$

    /**
     * The Constant TABLE.
     */
    public static final String TABLE = "TABLE"; //$NON-NLS-1$


    /**
     * The Constant METADATA_SYSTEM_TABLE.
     */
    public static final String METADATA_SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$

    /**
     * The Constant METADATA_LOCAL_TEMPORARY.
     */
    public static final String METADATA_LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$

    /**
     * The Constant METADATA_GLOBAL_TEMPORARY.
     */
    public static final String METADATA_GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$

    /**
     * The Constant METADATA_SYNONYM.
     */
    public static final String METADATA_SYNONYM = "SYNONYM"; //$NON-NLS-1$

    /**
     * The Constant METADATA_ALIAS.
     */
    public static final String METADATA_ALIAS = "ALIAS"; //$NON-NLS-1$

    /**
     * The Constant METADATA_VIEW.
     */
    public static final String METADATA_VIEW = "VIEW"; //$NON-NLS-1$

    /**
     * The Constant METADATA_VIEW.
     */
    public static final String METADATA_CALC_VIEW = "CALC VIEW"; //$NON-NLS-1$

    /**
     * The Constant METADATA_TABLE.
     */
    public static final String METADATA_TABLE = "TABLE"; //$NON-NLS-1$

    /**
     * The Constant METADATA_TABLE_TYPES.
     */
    public static final List<String> METADATA_TABLE_TYPES = Collections.unmodifiableList(Arrays.asList(METADATA_TABLE, METADATA_VIEW, METADATA_ALIAS,
            METADATA_SYNONYM, METADATA_GLOBAL_TEMPORARY, METADATA_LOCAL_TEMPORARY, METADATA_SYSTEM_TABLE));

}
