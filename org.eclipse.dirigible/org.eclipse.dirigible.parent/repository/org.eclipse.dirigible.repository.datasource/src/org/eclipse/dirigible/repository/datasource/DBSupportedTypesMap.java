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

	public enum DataTypes {
		VARCHAR, TINYINT, TIMESTAMP, TIME, SMALLINT, NVARCHAR, NUMERIC, INTEGER, REAL, FLOAT, DOUBLE, DECIMAL, DATE, CLOB, CHAR, BOOLEAN, BLOB, BIT, BINARY, BIGINT, UNSUPPORTED_TYPE
	}

	// public static final String VARCHAR = "VARCHAR"; //$NON-NLS-1$
	// public static final String TINYINT = "TINYINT"; //$NON-NLS-1$
	// public static final String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	// public static final String TIME = "TIME"; //$NON-NLS-1$
	// public static final String SMALLINT = "SMALLINT"; //$NON-NLS-1$
	// public static final String NVARCHAR = "NVARCHAR"; //$NON-NLS-1$
	// public static final String NUMERIC = "NUMERIC"; //$NON-NLS-1$
	// public static final String INTEGER = "INTEGER"; //$NON-NLS-1$
	// public static final String REAL = "REAL"; //$NON-NLS-1$
	// public static final String FLOAT = "FLOAT"; //$NON-NLS-1$
	// public static final String DOUBLE = "DOUBLE"; //$NON-NLS-1$
	// public static final String DECIMAL = "DECIMAL"; //$NON-NLS-1$
	// public static final String DATE = "DATE"; //$NON-NLS-1$
	// public static final String CLOB = "CLOB"; //$NON-NLS-1$
	// public static final String CHAR = "CHAR"; //$NON-NLS-1$
	// public static final String BOOLEAN = "BOOLEAN"; //$NON-NLS-1$
	// public static final String BLOB = "BLOB"; //$NON-NLS-1$
	// public static final String BIT = "BIT"; //$NON-NLS-1$
	// public static final String BINARY = "BINARY"; //$NON-NLS-1$
	// public static final String BIGINT = "BIGINT"; //$NON-NLS-1$
	// public static final String UNSUPPORTED_TYPE = "Unsupported Type:"; //$NON-NLS-1$

	/**
	 * Gives the meaningful subset of all the existing types in JDBC
	 * specification which are supported in this framework
	 *
	 * @return the flat array of the supported data types
	 */
	public static String[] getSupportedTypes() {
		return new String[] { DataTypes.VARCHAR.toString(), DataTypes.CHAR.toString(), DataTypes.INTEGER.toString(), DataTypes.BIGINT.toString(),
				DataTypes.SMALLINT.toString(), DataTypes.REAL.toString(), DataTypes.DOUBLE.toString(), DataTypes.DATE.toString(),
				DataTypes.TIME.toString(), DataTypes.TIMESTAMP.toString(), DataTypes.BLOB.toString() };
	}

	/**
	 * Retrieve the type name by given JDBC type as integer
	 *
	 * @param type
	 *            number
	 * @return type name
	 */
	public static String getTypeName(int type) {
		String typeName = null;
		switch (type) {
			case java.sql.Types.BIGINT:
				typeName = DataTypes.BIGINT.toString();
				break;
			case java.sql.Types.BINARY:
				typeName = DataTypes.BINARY.toString();
				break;
			case java.sql.Types.BIT:
				typeName = DataTypes.BIT.toString();
				break;
			case java.sql.Types.BLOB:
				typeName = DataTypes.BLOB.toString();
				break;
			case java.sql.Types.BOOLEAN:
				typeName = DataTypes.BOOLEAN.toString();
				break;
			case java.sql.Types.CHAR:
				typeName = DataTypes.CHAR.toString();
				break;
			case java.sql.Types.CLOB:
				typeName = DataTypes.CLOB.toString();
				break;
			case java.sql.Types.DATE:
				typeName = DataTypes.DATE.toString();
				break;
			case java.sql.Types.DECIMAL:
				typeName = DataTypes.DECIMAL.toString();
				break;
			case java.sql.Types.DOUBLE:
				typeName = DataTypes.DOUBLE.toString();
				break;
			case java.sql.Types.FLOAT:
				typeName = DataTypes.FLOAT.toString();
				break;
			case java.sql.Types.INTEGER:
				typeName = DataTypes.INTEGER.toString();
				break;
			case java.sql.Types.NUMERIC:
				typeName = DataTypes.NUMERIC.toString();
				break;
			case java.sql.Types.NVARCHAR:
				typeName = DataTypes.NVARCHAR.toString();
				break;
			case java.sql.Types.SMALLINT:
				typeName = DataTypes.SMALLINT.toString();
				break;
			case java.sql.Types.TIME:
				typeName = DataTypes.TIME.toString();
				break;
			case java.sql.Types.TIMESTAMP:
				typeName = DataTypes.TIMESTAMP.toString();
				break;
			case java.sql.Types.TINYINT:
				typeName = DataTypes.TINYINT.toString();
				break;
			case java.sql.Types.REAL:
				typeName = DataTypes.REAL.toString();
				break;
			case java.sql.Types.VARCHAR:
				typeName = DataTypes.VARCHAR.toString();
				break;
			default:
				typeName = DataTypes.UNSUPPORTED_TYPE.toString() + type;
				break;
		}

		return typeName;
	}

	/**
	 * Retrieve the JDBC type number by a given type name
	 *
	 * @param name
	 *            the name of the type
	 * @return the JDBC type number
	 */
	public static int getTypeNumber(String name) {
		int typeNumber = -1;
		switch (DataTypes.valueOf(name)) {
			case BIGINT:
				typeNumber = java.sql.Types.BIGINT;
				break;
			case BINARY:
				typeNumber = java.sql.Types.BINARY;
				break;
			case BIT:
				typeNumber = java.sql.Types.BIT;
				break;
			case BLOB:
				typeNumber = java.sql.Types.BLOB;
				break;
			case BOOLEAN:
				typeNumber = java.sql.Types.BOOLEAN;
				break;
			case CHAR:
				typeNumber = java.sql.Types.CHAR;
				break;
			case CLOB:
				typeNumber = java.sql.Types.CLOB;
				break;
			case DATE:
				typeNumber = java.sql.Types.DATE;
				break;
			case DECIMAL:
				typeNumber = java.sql.Types.DECIMAL;
				break;
			case DOUBLE:
				typeNumber = java.sql.Types.DOUBLE;
				break;
			case FLOAT:
				typeNumber = java.sql.Types.FLOAT;
				break;
			case INTEGER:
				typeNumber = java.sql.Types.INTEGER;
				break;
			case NUMERIC:
				typeNumber = java.sql.Types.NUMERIC;
				break;
			case NVARCHAR:
				typeNumber = java.sql.Types.NVARCHAR;
				break;
			case SMALLINT:
				typeNumber = java.sql.Types.SMALLINT;
				break;
			case TIME:
				typeNumber = java.sql.Types.TIME;
				break;
			case TIMESTAMP:
				typeNumber = java.sql.Types.TIMESTAMP;
				break;
			case TINYINT:
				typeNumber = java.sql.Types.TINYINT;
				break;
			case REAL:
				typeNumber = java.sql.Types.REAL;
				break;
			case VARCHAR:
				typeNumber = java.sql.Types.VARCHAR;
				break;
		}

		return typeNumber;
	}
}
