/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ISqlKeywords {

	public static final String KEYWORD_SELECT = "SELECT"; //$NON-NLS-1$
	public static final String KEYWORD_DISTINCT = "DISTINCT"; //$NON-NLS-1$
	public static final String KEYWORD_FROM = "FROM"; //$NON-NLS-1$
	public static final String KEYWORD_JOIN = "JOIN"; //$NON-NLS-1$
	public static final String KEYWORD_INNER = "INNER"; //$NON-NLS-1$
	public static final String KEYWORD_OUTER = "OUTER"; //$NON-NLS-1$
	public static final String KEYWORD_LEFT = "LEFT"; //$NON-NLS-1$
	public static final String KEYWORD_RIGHT = "RIGHT"; //$NON-NLS-1$
	public static final String KEYWORD_FULL = "FULL"; //$NON-NLS-1$
	public static final String KEYWORD_WHERE = "WHERE"; //$NON-NLS-1$
	public static final String KEYWORD_GROUP_BY = "GROUP BY"; //$NON-NLS-1$
	public static final String KEYWORD_HAVING = "HAVING"; //$NON-NLS-1$
	public static final String KEYWORD_ORDER_BY = "ORDER BY"; //$NON-NLS-1$
	public static final String KEYWORD_UNION = "UNION"; //$NON-NLS-1$
	public static final String KEYWORD_ASC = "ASC"; //$NON-NLS-1$
	public static final String KEYWORD_DESC = "DESC"; //$NON-NLS-1$
	public static final String KEYWORD_AND = "AND"; //$NON-NLS-1$
	public static final String KEYWORD_OR = "OR"; //$NON-NLS-1$
	public static final String KEYWORD_AS = "AS"; //$NON-NLS-1$
	public static final String KEYWORD_ON = "ON"; //$NON-NLS-1$
	public static final String KEYWORD_LIMIT = "LIMIT"; //$NON-NLS-1$
	public static final String KEYWORD_OFFSET = "OFFSET"; //$NON-NLS-1$
	public static final String KEYWORD_INSERT = "INSERT"; //$NON-NLS-1$
	public static final String KEYWORD_INTO = "INTO"; //$NON-NLS-1$
	public static final String KEYWORD_VALUES = "VALUES"; //$NON-NLS-1$
	public static final String KEYWORD_UPDATE = "UPDATE"; //$NON-NLS-1$
	public static final String KEYWORD_SET = "SET"; //$NON-NLS-1$
	public static final String KEYWORD_CREATE = "CREATE"; //$NON-NLS-1$
	public static final String KEYWORD_TABLE = "TABLE"; //$NON-NLS-1$
	public static final String KEYWORD_PRIMARY = "PRIMARY"; //$NON-NLS-1$
	public static final String KEYWORD_KEY = "KEY"; //$NON-NLS-1$
	public static final String KEYWORD_DROP = "DROP"; //$NON-NLS-1$
	public static final String KEYWORD_DELETE = "DELETE"; //$NON-NLS-1$
	public static final String KEYWORD_NOT = "NOT"; //$NON-NLS-1$
	public static final String KEYWORD_NULL = "NULL"; //$NON-NLS-1$
	public static final String KEYWORD_SEQUENCE = "SEQUENCE"; //$NON-NLS-1$
	public static final String KEYWORD_NEXT_VALUE_FOR = "NEXT VALUE FOR"; //$NON-NLS-1$
	public static final String KEYWORD_UNIQUE = "UNIQUE"; //$NON-NLS-1$
	public static final String KEYWORD_COLUMN = "COLUMN"; //$NON-NLS-1$
	public static final String KEYWORD_ROWS = "ROWS"; //$NON-NLS-1$
	public static final String KEYWORD_VIEW = "VIEW"; //$NON-NLS-1$
	public static final String KEYWORD_FOR_UPDATE = "FOR UPDATE"; //$NON-NLS-1$

	public static final String FUNCTION_CURRENT_DATE = "CURRENT_DATE"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIME = "CURRENT_TIME"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$

	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String SPACE = " "; //$NON-NLS-1$
	public static final String OPEN = "("; //$NON-NLS-1$
	public static final String CLOSE = ")"; //$NON-NLS-1$
	public static final String QUESTION = "?"; //$NON-NLS-1$
	public static final String EQUALS = "="; //$NON-NLS-1$
	public static final String UNDERSCROE = "_"; //$NON-NLS-1$
	public static final String STAR = "*"; //$NON-NLS-1$

	public static final String METADATA_SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$
	public static final String METADATA_LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$
	public static final String METADATA_GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$
	public static final String METADATA_SYNONYM = "SYNONYM"; //$NON-NLS-1$
	public static final String METADATA_ALIAS = "ALIAS"; //$NON-NLS-1$
	public static final String METADATA_VIEW = "VIEW"; //$NON-NLS-1$
	public static final String METADATA_TABLE = "TABLE"; //$NON-NLS-1$

	public static final List<String> METADATA_TABLE_TYPES = Collections.unmodifiableList(Arrays.asList(METADATA_TABLE, METADATA_VIEW, METADATA_ALIAS,
			METADATA_SYNONYM, METADATA_GLOBAL_TEMPORARY, METADATA_LOCAL_TEMPORARY, METADATA_SYSTEM_TABLE));

}
