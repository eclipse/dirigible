/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.datasource;

public class DBSupportedTypesMap {

	public static final String VARCHAR = "VARCHAR"; //$NON-NLS-1$
	public static final String TINYINT = "TINYINT"; //$NON-NLS-1$
	public static final String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	public static final String TIME = "TIME"; //$NON-NLS-1$
	public static final String SMALLINT = "SMALLINT"; //$NON-NLS-1$
	public static final String NVARCHAR = "NVARCHAR"; //$NON-NLS-1$
	public static final String NUMERIC = "NUMERIC"; //$NON-NLS-1$
	public static final String INTEGER = "INTEGER"; //$NON-NLS-1$
	public static final String REAL = "REAL"; //$NON-NLS-1$
	public static final String FLOAT = "FLOAT"; //$NON-NLS-1$
	public static final String DOUBLE = "DOUBLE"; //$NON-NLS-1$
	public static final String DECIMAL = "DECIMAL"; //$NON-NLS-1$
	public static final String DATE = "DATE"; //$NON-NLS-1$
	public static final String CLOB = "CLOB"; //$NON-NLS-1$
	public static final String CHAR = "CHAR"; //$NON-NLS-1$
	public static final String BOOLEAN = "BOOLEAN"; //$NON-NLS-1$
	public static final String BLOB = "BLOB"; //$NON-NLS-1$
	public static final String BIT = "BIT"; //$NON-NLS-1$
	public static final String BINARY = "BINARY"; //$NON-NLS-1$
	public static final String BIGINT = "BIGINT"; //$NON-NLS-1$
	public static final String UNSUPPORTED_TYPE = "Unsupported Type:"; //$NON-NLS-1$

	/**
	 * Gives the meaningful subset of all the existing types in JDBC
	 * specification which are supported in this framework
	 *
	 * @return
	 */
	public static String[] getSupportedTypes() {
		return new String[] { VARCHAR, CHAR, INTEGER, BIGINT, SMALLINT, REAL, DOUBLE, DATE, TIME, TIMESTAMP, BLOB };
	}

	/**
	 * Retrieve the type name by given JDBC type as integer
	 *
	 * @param type
	 * @return
	 */
	public static String getTypeName(int type) {
		String typeName = null;
		switch (type) {
			case java.sql.Types.BIGINT:
				typeName = BIGINT;
				break;
			case java.sql.Types.BINARY:
				typeName = BINARY;
				break;
			case java.sql.Types.BIT:
				typeName = BIT;
				break;
			case java.sql.Types.BLOB:
				typeName = BLOB;
				break;
			case java.sql.Types.BOOLEAN:
				typeName = BOOLEAN;
				break;
			case java.sql.Types.CHAR:
				typeName = CHAR;
				break;
			case java.sql.Types.CLOB:
				typeName = CLOB;
				break;
			case java.sql.Types.DATE:
				typeName = DATE;
				break;
			case java.sql.Types.DECIMAL:
				typeName = DECIMAL;
				break;
			case java.sql.Types.DOUBLE:
				typeName = DOUBLE;
				break;
			case java.sql.Types.FLOAT:
				typeName = FLOAT;
				break;
			case java.sql.Types.INTEGER:
				typeName = INTEGER;
				break;
			case java.sql.Types.NUMERIC:
				typeName = NUMERIC;
				break;
			case java.sql.Types.NVARCHAR:
				typeName = NVARCHAR;
				break;
			case java.sql.Types.SMALLINT:
				typeName = SMALLINT;
				break;
			case java.sql.Types.TIME:
				typeName = TIME;
				break;
			case java.sql.Types.TIMESTAMP:
				typeName = TIMESTAMP;
				break;
			case java.sql.Types.TINYINT:
				typeName = TINYINT;
				break;
			case java.sql.Types.REAL:
				typeName = REAL;
				break;
			case java.sql.Types.VARCHAR:
				typeName = VARCHAR;
				break;
			default:
				typeName = UNSUPPORTED_TYPE + type;
				break;
		}

		return typeName;
	}

}
