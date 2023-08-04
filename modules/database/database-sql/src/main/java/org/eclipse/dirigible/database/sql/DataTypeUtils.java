/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql;

import static java.text.MessageFormat.format;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The Class DataTypeUtils.
 */
public class DataTypeUtils {

	/**  The default length. */
	public static final int VARCHAR_DEFAULT_LENGTH = 512;
	
	/** The Constant DECIMAL_DEFAULT_LENGTH. */
	public static final int DECIMAL_DEFAULT_LENGTH = 100000;

	/** The Constant DATABASE_TYPE_TO_DATA_TYPE. */
	private static final Map<Integer, DataType> DATABASE_TYPE_TO_DATA_TYPE = Collections.synchronizedMap(new HashMap<Integer, DataType>());

	/** The Constant STRING_TO_DATABASE_TYPE. */
	private static final Map<String, Integer> STRING_TO_DATABASE_TYPE = Collections.synchronizedMap(new HashMap<String, Integer>());

	/** The Constant JAVA_TYPE_TO_DATABASE_TYPE. */
	private static final Map<Class, Integer> JAVA_TYPE_TO_DATABASE_TYPE = Collections.synchronizedMap(new HashMap<Class, Integer>());

	/** The Constant DATABASE_TYPE_TO_JAVA_TYPE. */
	private static final Map<Integer, Class> DATABASE_TYPE_TO_JAVA_TYPE = Collections.synchronizedMap(new HashMap<Integer, Class>());

	static {
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.VARCHAR, DataType.VARCHAR);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.NVARCHAR, DataType.NVARCHAR);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.CHAR, DataType.CHAR);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.TIMESTAMP, DataType.TIMESTAMP);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.TIME, DataType.TIME);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.INTEGER, DataType.INTEGER);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BIGINT, DataType.BIGINT);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.SMALLINT, DataType.SMALLINT);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.REAL, DataType.REAL);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.FLOAT, DataType.FLOAT);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.DOUBLE, DataType.DOUBLE);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.DATE, DataType.DATE);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BOOLEAN, DataType.BOOLEAN);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BLOB, DataType.BLOB);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.DECIMAL, DataType.DECIMAL);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BIT, DataType.BIT);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.ARRAY, DataType.ARRAY);

		STRING_TO_DATABASE_TYPE.put("VARCHAR", Types.VARCHAR);
		STRING_TO_DATABASE_TYPE.put("NVARCHAR", Types.NVARCHAR);
		STRING_TO_DATABASE_TYPE.put("CHAR", Types.CHAR);
		STRING_TO_DATABASE_TYPE.put("CHARACTER", Types.CHAR);
		STRING_TO_DATABASE_TYPE.put("TIMESTAMP", Types.TIMESTAMP);
		STRING_TO_DATABASE_TYPE.put("TIME", Types.TIME);
		STRING_TO_DATABASE_TYPE.put("INTEGER", Types.INTEGER);
		STRING_TO_DATABASE_TYPE.put("INT4", Types.INTEGER);
		STRING_TO_DATABASE_TYPE.put("BIGINT", Types.BIGINT);
		STRING_TO_DATABASE_TYPE.put("INT8", Types.INTEGER);
		STRING_TO_DATABASE_TYPE.put("SMALLINT", Types.SMALLINT);
		STRING_TO_DATABASE_TYPE.put("INT2", Types.INTEGER);
		STRING_TO_DATABASE_TYPE.put("REAL", Types.REAL);
		STRING_TO_DATABASE_TYPE.put("FLOAT4", Types.REAL);
		STRING_TO_DATABASE_TYPE.put("FLOAT", Types.FLOAT);
		STRING_TO_DATABASE_TYPE.put("DOUBLE", Types.DOUBLE);
		STRING_TO_DATABASE_TYPE.put("FLOAT8", Types.DOUBLE);
		STRING_TO_DATABASE_TYPE.put("DATE", Types.DATE);
		STRING_TO_DATABASE_TYPE.put("BOOLEAN", Types.BOOLEAN);
		STRING_TO_DATABASE_TYPE.put("BLOB", Types.BLOB);
		STRING_TO_DATABASE_TYPE.put("DECIMAL", Types.DECIMAL);
		STRING_TO_DATABASE_TYPE.put("BIT", Types.BIT);
		STRING_TO_DATABASE_TYPE.put("ARRAY", Types.ARRAY);

		JAVA_TYPE_TO_DATABASE_TYPE.put(String.class, Types.VARCHAR);
		JAVA_TYPE_TO_DATABASE_TYPE.put(String.class, Types.NVARCHAR);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Integer.class, Types.INTEGER);
		JAVA_TYPE_TO_DATABASE_TYPE.put(int.class, Types.INTEGER);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Long.class, Types.BIGINT);
		JAVA_TYPE_TO_DATABASE_TYPE.put(long.class, Types.BIGINT);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Float.class, Types.FLOAT);
		JAVA_TYPE_TO_DATABASE_TYPE.put(float.class, Types.FLOAT);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Double.class, Types.DOUBLE);
		JAVA_TYPE_TO_DATABASE_TYPE.put(double.class, Types.DOUBLE);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Boolean.class, Types.BOOLEAN);
		JAVA_TYPE_TO_DATABASE_TYPE.put(boolean.class, Types.BOOLEAN);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Date.class, Types.TIMESTAMP);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Time.class, Types.TIME);
		JAVA_TYPE_TO_DATABASE_TYPE.put(Timestamp.class, Types.TIMESTAMP);
		JAVA_TYPE_TO_DATABASE_TYPE.put(byte[].class, Types.BLOB);
		JAVA_TYPE_TO_DATABASE_TYPE.put(List.class, Types.ARRAY);

		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.VARCHAR, String.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.NVARCHAR, String.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.CHAR, String.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.TIMESTAMP, Timestamp.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.TIME, Time.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.SMALLINT, int.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.INTEGER, int.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.BIGINT, long.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.REAL, float.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.FLOAT, float.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.DOUBLE, double.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.DECIMAL, double.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.DATE, Date.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.BOOLEAN, boolean.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.BLOB, byte[].class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.DECIMAL, Double.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.BIT, boolean.class);
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.ARRAY, List.class);

	}

	/**
	 * Checks if is database type supported.
	 *
	 * @param type
	 *            the type
	 * @return true, if is database type supported
	 */
	public static boolean isDatabaseTypeSupported(Integer type) {
		return DATABASE_TYPE_TO_DATA_TYPE.containsKey(type);
	}
	
	/**
	 * Gets the database type.
	 *
	 * @param type
	 *            the type
	 * @return the database type
	 */
	public static DataType getDatabaseType(Integer type) {
		if (isDatabaseTypeSupported(type)) {
			return DATABASE_TYPE_TO_DATA_TYPE.get(type);
		}
		throw new SqlException(format("Type {0} not supported", type));
	}

	/**
	 * Gets the database type name.
	 *
	 * @param type
	 *            the type
	 * @return the database type name
	 */
	public static String getDatabaseTypeName(Integer type) {
		if (isDatabaseTypeSupported(type)) {
			return DATABASE_TYPE_TO_DATA_TYPE.get(type).toString();
		}
		throw new SqlException(format("Type {0} not supported", type));
	}

	/**
	 * Gets the database type by java type.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the database type by java type
	 */
	public static Integer getDatabaseTypeByJavaType(Class clazz) {
		Integer type = JAVA_TYPE_TO_DATABASE_TYPE.get(clazz);
		if (type == null) {
			for (Entry<Class, Integer> entry : JAVA_TYPE_TO_DATABASE_TYPE.entrySet()) {
				if (entry.getKey().isAssignableFrom(clazz)) {
					return entry.getValue();
				}
			}
			throw new SqlException(format("Class {0} does not have mapping to a data type", clazz));
		}
		return type;
	}

	/**
	 * Gets the java type by database type.
	 *
	 * @param type
	 *            the type
	 * @return the java type by database type
	 */
	public static Class getJavaTypeByDatabaseType(Integer type) {
		Class clazz = DATABASE_TYPE_TO_JAVA_TYPE.get(type);
		if (clazz == null) {
			throw new SqlException(format("Type {0} does not have mapping to a java type", type));
		}
		return clazz;
	}

	/**
	 * Gets the sql type by data type.
	 *
	 * @param type
	 *            the type
	 * @return the sql type by data type
	 */
	public static Integer getSqlTypeByDataType(String type) {
		type = type.toUpperCase();
		if (STRING_TO_DATABASE_TYPE.containsKey(type)) {
			return STRING_TO_DATABASE_TYPE.get(type);
		}
		throw new SqlException(format("Type {0} not supported", type));
	}

	/**
	 * Gets the database type name by java type.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the database type name by java type
	 */
	public static String getDatabaseTypeNameByJavaType(Class clazz) {
		Integer type = getDatabaseTypeByJavaType(clazz);
		return getDatabaseTypeName(type);
	}

	/**
	 * Checks if is blob.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is blob
	 */
	public static boolean isBlob(String dataType) {
		return DataType.BLOB.toString().equals(dataType);
	}

	/**
	 * Checks if is boolean.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is boolean
	 */
	public static boolean isBoolean(String dataType) {
		return DataType.BOOLEAN.toString().equals(dataType);
	}

	/**
	 * Checks if is double.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is double
	 */
	public static boolean isDouble(String dataType) {
		return DataType.DOUBLE.toString().equals(dataType);
	}

	/**
	 * Checks if is real.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is real
	 */
	public static boolean isReal(String dataType) {
		return DataType.REAL.toString().equals(dataType);
	}
	
	/**
	 * Checks if is float.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is real
	 */
	public static boolean isFloat(String dataType) {
		return DataType.FLOAT.toString().equals(dataType);
	}

	/**
	 * Checks if is bigint.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is bigint
	 */
	public static boolean isBigint(String dataType) {
		return DataType.BIGINT.toString().equals(dataType);
	}

	/**
	 * Checks if is smallint.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is smallint
	 */
	public static boolean isSmallint(String dataType) {
		return DataType.SMALLINT.toString().equals(dataType);
	}

	/**
	 * Checks if is integer.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is integer
	 */
	public static boolean isInteger(String dataType) {
		return DataType.INTEGER.toString().equals(dataType);
	}

	/**
	 * Checks if is tinyint.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is tinyint
	 */
	public static boolean isTinyint(String dataType) {
		return DataType.TINYINT.toString().equals(dataType);
	}

	/**
	 * Checks if is timestamp.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is timestamp
	 */
	public static boolean isTimestamp(String dataType) {
		return DataType.TIMESTAMP.toString().equals(dataType);
	}

	/**
	 * Checks if is time.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is time
	 */
	public static boolean isTime(String dataType) {
		return DataType.TIME.toString().equals(dataType);
	}

	/**
	 * Checks if is date.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is date
	 */
	public static boolean isDate(String dataType) {
		return DataType.DATE.toString().equals(dataType);
	}

	/**
	 * Checks if is char.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is char
	 */
	public static boolean isChar(String dataType) {
		return DataType.CHAR.toString().equals(dataType);
	}

	/**
	 * Checks if is varchar.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is varchar
	 */
	public static boolean isVarchar(String dataType) {
		return DataType.VARCHAR.toString().equals(dataType);
	}
	
	/**
	 * Checks if is nvarchar.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is varchar
	 */
	public static boolean isNvarchar(String dataType) {
		return DataType.NVARCHAR.toString().equals(dataType);
	}

	/**
	 * Checks if is decimal.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is decimal
	 */
	public static boolean isDecimal(String dataType) {
		return DataType.DECIMAL.toString().equals(dataType);
	}
	
	/**
	 * Checks if is bit.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is bit
	 */
	public static boolean isBit(String dataType) {
		return DataType.BIT.toString().equals(dataType);
	}

	/**
	 * Checks if is array.
	 *
	 * @param dataType
	 *            the data type
	 * @return true, if is array
	 */
	public static boolean isArray(String dataType) {
		return DataType.ARRAY.toString().equals(dataType);
	}
}
