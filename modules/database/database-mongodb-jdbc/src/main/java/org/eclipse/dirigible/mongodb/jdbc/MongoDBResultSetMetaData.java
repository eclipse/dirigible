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
package org.eclipse.dirigible.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bson.BsonType;
import org.bson.codecs.BsonTypeClassMap;

public class MongoDBResultSetMetaData implements ResultSetMetaData {

	private int columnCount;
	private SortedMap<String, BsonType> keyMap = new TreeMap<String, BsonType>();
	private String collectionName;
	private BsonTypeClassMap bsonTojavaTypeMap = new BsonTypeClassMap();

	public MongoDBResultSetMetaData(String collectionName){
		 this.collectionName =  collectionName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
	        return (T) this;
	    }
	    throw new SQLException("No wrapper for " + iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		 return iface != null && iface.isAssignableFrom(getClass());
	}

	public Map<String, BsonType> keys() {
		return this.keyMap ;
	}
	
	@Override
	public int getColumnCount() throws SQLException {
		return this.columnCount;
	}
	
	void setColumnCount(int count){
		this.columnCount = count;
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		return columnNullableUnknown;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return 25;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnLabel(int column) throws SQLException {
		Entry<String, BsonType> entry = (Entry<String, BsonType>) this.keyMap.entrySet().toArray()[column-1];
		return entry.getKey();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnName(int column) throws SQLException {
		Entry<String, BsonType> entry = (Entry<String, BsonType>) this.keyMap.entrySet().toArray()[column-1];
		return entry.getKey();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		return this.collectionName;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		Entry<String, BsonType> entry = (Entry<String, BsonType>) this.keyMap.entrySet().toArray()[column-1];
		//return entries[column-1].getValue().getValue();//TODO: this returns the BSON type ordinal. What we need is a mapping to SQL types ordinals.
		return this.getSqlType(entry.getValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnTypeName(int column) throws SQLException {
		Entry<String, BsonType> entry = (Entry<String, BsonType>) this.keyMap.entrySet().toArray()[column-1];
		//return entries[column-1].getValue().toString();// return MongoDB specific datatype name.
		return this.getSqlTypeName(entry.getValue());
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		Entry<String, BsonType> entry = (Entry<String, BsonType>) this.keyMap.entrySet().toArray()[column-1];
		return this.bsonTojavaTypeMap.get(entry.getValue()).getCanonicalName();
	}
	
	int getSqlType(BsonType bsonType){
		switch(bsonType){
			case OBJECT_ID: { return Types.VARCHAR;}
			case ARRAY: { return Types.ARRAY;}
			case BINARY: { return Types.BINARY; }
			case BOOLEAN: { return Types.BOOLEAN;}
			/*case DATE_TIME: { return Types.DATE}*/
			case DOCUMENT: { return Types.OTHER; }
			case DOUBLE: { return Types.DOUBLE; }
			case INT32: {return Types.INTEGER; }
			case INT64: {return Types.INTEGER; }
			case STRING: { return Types.VARCHAR; }
			case TIMESTAMP: { return Types.TIMESTAMP;}
			default: break;
		}
		return Integer.MIN_VALUE;
	}
	
	String getSqlTypeName(BsonType bsonType){
		switch(bsonType){
			case ARRAY: { return "ARRAY";}
			case BINARY: { return "BINARY"; }
			case BOOLEAN: { return "BOOLEAN";}
			/*case DATE_TIME: { return Types.DATE}*/
			case DOCUMENT: { return "OTHER"; }
			case DOUBLE: { return "DOUBLE"; }
			case INT32: {return "INTEGER"; }
			case INT64: {return "INTEGER"; }
			case STRING: { return "VARCHAR"; }
			case TIMESTAMP: { return "TIMESTAMP";}
			case OBJECT_ID: { return "STRING";}
			default: break;
		}
		return null;
	}
	
}
