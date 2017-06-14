package org.eclipse.dirigible.database.squle;

import static java.text.MessageFormat.format;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.database.api.DatabaseException;

public class DataTypeUtils {
	
	public static final int VARCHAR_DEFAULT_LENGTH = 512;
	
	private static final Map<Integer, DataType> DATABASE_TYPE_TO_DATA_TYPE = Collections.synchronizedMap(new HashMap<Integer, DataType>());
	private static final Map<Class, Integer> JAVA_TYPE_TO_DATABASE_TYPE = Collections.synchronizedMap(new HashMap<Class, Integer>());
	private static final Map<Integer, Class> DATABASE_TYPE_TO_JAVA_TYPE = Collections.synchronizedMap(new HashMap<Integer, Class>());

	static {
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.VARCHAR, DataType.VARCHAR);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.CHAR, DataType.CHAR);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.TIMESTAMP, DataType.TIMESTAMP);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.TIME, DataType.TIME);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.INTEGER, DataType.INTEGER);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BIGINT, DataType.BIGINT);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.REAL, DataType.REAL);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.DOUBLE, DataType.DOUBLE);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.DATE, DataType.DATE);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BOOLEAN, DataType.BOOLEAN);
		DATABASE_TYPE_TO_DATA_TYPE.put(Types.BLOB, DataType.BLOB);
		
		JAVA_TYPE_TO_DATABASE_TYPE.put(String.class, Types.VARCHAR);
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
		
		DATABASE_TYPE_TO_JAVA_TYPE.put(Types.VARCHAR, String.class);
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
		
	}
	
	public static boolean isDatabaseTypeSupported(Integer type) {
		return DATABASE_TYPE_TO_DATA_TYPE.containsKey(type);
	}
	
	public static String getDatabaseTypeName(Integer type) {
		if (isDatabaseTypeSupported(type)) {
			return DATABASE_TYPE_TO_DATA_TYPE.get(type).toString();
		}
		throw new DatabaseException(format("Type {0} not supported", type));
	}
	
	public static Integer getDatabaseTypeByJavaType(Class clazz) {
		Integer type = JAVA_TYPE_TO_DATABASE_TYPE.get(clazz);
		if (type == null) {
			throw new DatabaseException(format("Class {0} does not have mapping to a data type", clazz));
		}
		return type;
	}
	
	public static Class getJavaTypeByDatabaseType(Integer type) {
		Class clazz = DATABASE_TYPE_TO_JAVA_TYPE.get(type);
		if (clazz == null) {
			throw new DatabaseException(format("Type {0} does not have mapping to a java type", type));
		}
		return clazz;
	}
	
	public static String getDatabaseTypeNameByJavaType(Class clazz) {
		Integer type = getDatabaseTypeByJavaType(clazz);
		return getDatabaseTypeName(type);
	}
	
	public static boolean isBlob(String dataType) {
		return DataType.BLOB.toString().equals(dataType);
	}

	public static boolean isBoolean(String dataType) {
		return DataType.BOOLEAN.toString().equals(dataType);
	}

	public static boolean isDouble(String dataType) {
		return DataType.DOUBLE.toString().equals(dataType);
	}

	public static boolean isReal(String dataType) {
		return DataType.REAL.toString().equals(dataType);
	}

	public static boolean isBigint(String dataType) {
		return DataType.BIGINT.toString().equals(dataType);
	}

	public static boolean isInteger(String dataType) {
		return DataType.INTEGER.toString().equals(dataType);
	}

	public static boolean isTimestamp(String dataType) {
		return DataType.TIMESTAMP.toString().equals(dataType);
	}

	public static boolean isTime(String dataType) {
		return DataType.TIME.toString().equals(dataType);
	}

	public static boolean isDate(String dataType) {
		return DataType.DATE.toString().equals(dataType);
	}

	public static boolean isChar(String dataType) {
		return DataType.CHAR.toString().equals(dataType);
	}

	public static boolean isVarchar(String dataType) {
		return DataType.VARCHAR.toString().equals(dataType);
	}
	
}
