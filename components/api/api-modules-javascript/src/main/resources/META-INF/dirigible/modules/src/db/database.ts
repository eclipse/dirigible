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
/**
 * API Database
 *
 */
import * as bytes from "sdk/io/bytes";
const DatabaseFacade = Java.type("org.eclipse.dirigible.components.api.db.DatabaseFacade");
const DatabaseResultSetHelper = Java.type("org.eclipse.dirigible.components.data.management.helpers.DatabaseResultSetHelper");
const JSqlDate = Java.type("java.sql.Date");
const JSqlTimestamp = Java.type("java.sql.Timestamp");
const JSqlTime = Java.type("java.sql.Time");

const SQLTypes = Object.freeze({
	"BOOLEAN": 16,
	"DATE": 91,
	"TIME": 92,
	"TIMESTAMP": 93,
	"DOUBLE": 8,
	"FLOAT": 6,
	"REAL": 7,
	"TINYINT": -6,
	"SMALLINT": 5,
	"INTEGER": 4,
	"BIGINT": -5,
	"VARCHAR": 12,
	"CHAR": 1,
	"CLOB": 2005,
	"BLOB": 2004,
	"VARBINARY": -3,
	"DECIMAL": 3,
	"ARRAY": 2003,
	"NVARCHAR": -9,
	"NCLOB": 2011,
	"BIT": -7
});

export function getDatabaseTypes() {
	throw new Error("Deprecated");
};

export function getDataSources() {
	const datasources = DatabaseFacade.getDataSources();
	if (datasources) {
		return JSON.parse(datasources);
	}
	return datasources;
};

export function createDataSource(name, driver, url, username, password, properties) {
	throw new Error("Deprecated");
};

export function getMetadata(datasourceName) {
	let metadata;
	if (datasourceName) {
		metadata = DatabaseFacade.getMetadata(datasourceName);
	} else {
		metadata = DatabaseFacade.getMetadata();
	}

	if (metadata) {
		return JSON.parse(metadata);
	}
	return metadata;
};

export function getProductName(datasourceName) {
	let productName;
	if (datasourceName) {
		productName = DatabaseFacade.getProductName(datasourceName);
	} else {
		productName = DatabaseFacade.getProductName();
	}
	return productName;
};

export function getConnection(datasourceName) {
	const connection = new Connection();
	var native;
	if (datasourceName) {
		native = DatabaseFacade.getConnection(datasourceName);
	} else {
		native = DatabaseFacade.getConnection();
	}
	connection.native = native;
	return connection;
};

/**
 * Connection object
 */
function Connection() {

	this.prepareStatement = function (sql) {
		const preparedStatement = new PreparedStatement();
		const native = this.native.prepareStatement(sql);
		preparedStatement.native = native;
		return preparedStatement;
	};

	this.prepareCall = function (sql) {
		const callableStatement = new CallableStatement();
		const native = this.native.prepareCall(sql);
		callableStatement.native = native;
		return callableStatement;
	};

	this.close = function () {
		if (!this.isClosed()) {
			this.native.close();
		}
	};

	this.commit = function () {
		this.native.commit();
	};

	this.getAutoCommit = function () {
		return this.native.getAutoCommit();
	};

	this.getCatalog = function () {
		return this.native.getCatalog();
	};

	this.getSchema = function () {
		return this.native.getSchema();
	};

	this.getTransactionIsolation = function () {
		return this.native.getTransactionIsolation();
	};

	this.isClosed = function () {
		return this.native.isClosed();
	};

	this.isReadOnly = function () {
		return this.native.isReadOnly();
	};

	this.isValid = function () {
		return this.native.isValid();
	};

	this.rollback = function () {
		return this.native.rollback();
	};

	this.setAutoCommit = function (autoCommit) {
		this.native.setAutoCommit(autoCommit);
	};

	this.setCatalog = function (catalog) {
		this.native.setCatalog(catalog);
	};

	this.setReadOnly = function (readOnly) {
		this.native.setReadOnly(readOnly);
	};

	this.setSchema = function (schema) {
		this.native.setSchema(schema);
	};

	this.setTransactionIsolation = function (transactionIsolation) {
		this.native.setTransactionIsolation(transactionIsolation);
	};
}

/**
 * Statement object
 */
function PreparedStatement(internalStatement?) {

	this.close = function () {
		this.native.close();
	};

	this.getResultSet = function () {
		const resultset = new ResultSet();
		const native = this.native.getResultSet();
		resultset.native = native;
		return resultset;
	};

	this.execute = function () {
		return this.native.execute();
	};

	this.executeQuery = function () {
		const resultset = new ResultSet();
		resultset.native = this.native.executeQuery();
		return resultset;
	};

	this.executeUpdate = function () {
		return this.native.executeUpdate();
	};

	this.setNull = function (index, sqlType) {
		this.native.setNull(index, sqlType);
	};

	this.setBinaryStream = function (parameter, inputStream, length) {
		if (length) {
			this.native.setBinaryStream(parameter, inputStream, length);
		} else {
			this.native.setBinaryStream(parameter, inputStream);
		}
	};

	this.setBoolean = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setBoolean(index, value);
		} else {
			this.setNull(index, SQLTypes.BOOLEAN);
		}
	};

	this.setByte = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setByte(index, value);
		} else {
			this.setNull(index, SQLTypes.TINYINT);
		}
	};

	this.setBlob = function (index, value) {
		if (value !== null && value !== undefined) {
			let blob = createBlobValue(this.native, value);
			this.native.setBlob(index, blob);
		} else {
			this.setNull(index, SQLTypes.BLOB);
		}
	};

	this.setClob = function (index, value) {
		if (value !== null && value !== undefined) {
			let clob = createClobValue(this.native, value);
			this.native.setClob(index, clob);
		} else {
			this.setNull(index, SQLTypes.CLOB);
		}
	};

	this.setNClob = function (index, value) {
		if (value !== null && value !== undefined) {
			let nclob = createNClobValue(this.native, value);
			this.native.setNClob(index, nclob);
		} else {
			this.setNull(index, SQLTypes.NCLOB);
		}
	};

	this.setBytesNative = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setBytes(index, value);
		} else {
			this.setNull(index, SQLTypes.VARBINARY);
		}
	};

	this.setBytes = function (index, value) {
		if (value !== null && value !== undefined) {
			var data = bytes.toJavaBytes(value);
			this.native.setBytes(index, data);
		} else {
			this.setNull(index, SQLTypes.VARBINARY);
		}
	};

	this.setDate = function (index, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let dateInstance = new JSqlDate(date.getTime());
			this.native.setDate(index, dateInstance);
		} else {
			this.setNull(index, SQLTypes.DATE);
		}
	};

	this.setDouble = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setDouble(index, value);
		} else {
			this.setNull(index, SQLTypes.DOUBLE);
		}
	};

	this.setFloat = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setFloat(index, value);
		} else {
			this.setNull(index, SQLTypes.FLOAT);
		}
	};

	this.setInt = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setInt(index, value);
		} else {
			this.setNull(index, SQLTypes.INTEGER);
		}
	};

	this.setLong = function (index, value) {
		index = parseInt(index, 10); //Rhino things..
		if (value !== null && value !== undefined) {
			this.native.setLong(index, value);
		} else {
			this.setNull(index, SQLTypes.BIGINT);
		}
	};

	this.setShort = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setShort(index, value);
		} else {
			this.setNull(index, SQLTypes.SMALLINT);
		}
	};

	this.setString = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setString(index, value);
		} else {
			this.setNull(index, SQLTypes.VARCHAR);
		}
	};

	this.setTime = function (index, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timeInstance = new JSqlTime(date.getTime());
			this.native.setTime(index, timeInstance);
		} else {
			this.setNull(index, SQLTypes.TIME);
		}
	};

	this.setTimestamp = function (index, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timestampInstance = new JSqlTimestamp(date.getTime());
			this.native.setTimestamp(index, timestampInstance);
		} else {
			this.setNull(index, SQLTypes.TIMESTAMP);
		}
	};

	this.setBigDecimal = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setBigDecimal(index, value);
		} else {
			this.setNull(index, SQLTypes.DECIMAL);
		}
	};

	this.setNString = function (index, value) {
		if (value !== null && value !== undefined) {
			this.native.setNString(index, value);
		} else {
			this.setNull(index, SQLTypes.NVARCHAR);
		}
	};

	this.execute = function () {
		return this.native.execute();
	};

	this.addBatch = function () {
		this.native.addBatch();
	};

	this.executeBatch = function () {
		return this.native.executeBatch();
	};

	this.getMetaData = function () {
		return this.native.getMetaData();
	};

	this.getMoreResults = function () {
		return this.native.getMoreResults();
	};

	this.getParameterMetaData = function () {
		return this.native.getParameterMetaData();
	};

	this.getSQLWarning = function () {
		return this.native.getWarnings();
	};

	this.isClosed = function () {
		return this.native.isClosed();
	};
}

function CallableStatement() {

	this.getResultSet = function () {
		const resultset = new ResultSet();
		resultset.native = this.native.getResultSet();
		return resultset;
	};

	this.executeQuery = function () {
		const resultset = new ResultSet();
		resultset.native = this.native.executeQuery();
		return resultset;
	};

	this.executeUpdate = function () {
		return this.native.executeUpdate();
	};

	this.registerOutParameter = function (parameterIndex, sqlType) {
		this.native.registerOutParameter(parameterIndex, sqlType);
	};

	this.registerOutParameterByScale = function (parameterIndex, sqlType, scale) {
		this.native.registerOutParameter(parameterIndex, sqlType, scale);
	};

	this.registerOutParameterByTypeName = function (parameterIndex, sqlType, typeName) {
		this.native.registerOutParameter(parameterIndex, sqlType, typeName);
	};

	this.wasNull = function () {
		return this.native.wasNull();
	};

	this.getString = function (parameter) {
		return this.native.getString(parameter);
	};

	this.getBoolean = function (parameter) {
		return this.native.getBoolean(parameter);
	};

	this.getByte = function (parameter) {
		return this.native.getByte(parameter);
	};

	this.getShort = function (parameter) {
		return this.native.getShort(parameter);
	};

	this.getInt = function (parameter) {
		return this.native.getInt(parameter);
	};

	this.getLong = function (parameter) {
		return this.native.getLong(parameter);
	};

	this.getFloat = function (parameter) {
		return this.native.getFloat(parameter);
	};

	this.getDouble = function (parameter) {
		return this.native.getDouble(parameter);
	};

	this.getDate = function (parameter) {
		return this.native.getDate(parameter);
	};

	this.getTime = function (parameter) {
		return this.native.getTime(parameter);
	};

	this.getTimestamp = function (parameter) {
		return this.native.getTimestamp(parameter);
	};

	this.getObject = function (parameter) {
		return this.native.getObject(parameter);
	};

	this.getBigDecimal = function (parameter) {
		return this.native.getBigDecimal(parameter);
	};

	this.getRef = function (parameter) {
		return this.native.getRef(parameter);
	};

	this.getBytes = function (parameter) {
		let data = this.native.getBytes(parameter);
		return bytes.toJavaScriptBytes(data);
	};

	this.getBytesNative = function (parameter) {
		return this.native.getBytes(parameter);
	};

	this.getBlob = function (parameter) {
		let data = readBlobValue(this.native.getBlob(parameter));
		return bytes.toJavaScriptBytes(data);
	};

	this.getBlobNative = function (parameter) {
		return readBlobValue(this.native.getBlob(parameter));
	};

	this.getClob = function (parameter) {
		return readClobValue(this.native.getClob(parameter));
	};

	this.getNClob = function (parameter) {
		return readNClobValue(this.native.getNClob(parameter));
	};

	this.getNString = function (parameter) {
		return this.native.getNString(parameter);
	};

	this.getArray = function (parameter) {
		return this.native.getArray(parameter);
	};

	this.getURL = function (parameter) {
		return this.native.getURL(parameter);
	};

	this.getRowId = function (parameter) {
		return this.native.getRowId(parameter);
	};

	this.getSQLXML = function (parameter) {
		return this.native.getSQLXML(parameter);
	};

	this.setURL = function (parameter, value) {
		this.native.setURL(parameter, value);
	};

	this.setNull = function (parameter, sqlTypeStr, typeName) {
		const sqlType = Number.isInteger(sqlTypeStr) ? sqlTypeStr : SQLTypes[sqlTypeStr];
		if (typeName !== undefined && typeName !== null) {
			this.native.setNull(parameter, sqlType, typeName);
		} else {
			this.native.setNull(parameter, sqlType);
		}
	};

	this.setBoolean = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setBoolean(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.BOOLEAN);
		}
	};

	this.setByte = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setByte(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.BIT);
		}
	};

	this.setShort = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setShort(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.SMALLINT);
		}
	};

	this.setInt = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setInt(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.INTEGER);
		}
	};

	this.setLong = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setLong(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.BIGINT);
		}
	};

	this.setFloat = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setFloat(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.FLOAT);
		}
	};

	this.setDouble = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setDouble(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.DOUBLE);
		}
	};

	this.setBigDecimal = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setBigDecimal(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.DECIMAL);
		}
	};

	this.setString = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setString(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.VARCHAR);
		}
	};

	this.setBytes = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setBytes(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.ARRAY);
		}
	};

	this.setDate = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let dateInstance = new JSqlDate(date.getTime());
			this.native.setDate(parameter, dateInstance);
		} else {
			this.setNull(parameter, SQLTypes.DATE);
		}
	};

	this.setTime = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timeInstance = new JSqlTime(date.getTime());
			this.native.setTime(parameter, timeInstance);
		} else {
			this.setNull(parameter, SQLTypes.TIME);
		}
	};

	this.setTimestamp = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timestampInstance = new JSqlTimestamp(date.getTime());
			this.native.setTimestamp(parameter, timestampInstance);
		} else {
			this.setNull(parameter, SQLTypes.TIMESTAMP);
		}
	};

	this.setAsciiStream = function (parameter, inputStream, length) {
		if (length) {
			this.native.setAsciiStream(parameter, inputStream, length);
		} else {
			this.native.setAsciiStream(parameter, inputStream);
		}
	};

	this.setBinaryStream = function (parameter, inputStream, length) {
		if (length) {
			this.native.setBinaryStream(parameter, inputStream, length);
		} else {
			this.native.setBinaryStream(parameter, inputStream);
		}
	};

	this.setObject = function (parameter, value, targetSqlType, scale) {
		if (scale !== undefined && scale !== null && targetSqlType !== undefined && targetSqlType !== null) {
			this.native.setObject(parameter, value, targetSqlType, scale);
		} else if (targetSqlType !== undefined && targetSqlType !== null) {
			this.native.setObject(parameter, value, targetSqlType);
		} else {
			this.native.setObject(parameter, value);
		}
	};

	this.setRowId = function (parameter, value) {
		this.native.setRowId(parameter, value);
	};

	this.setNString = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setNString(parameter, value);
		} else {
			this.setNull(parameter, SQLTypes.NVARCHAR);
		}
	};

	this.setSQLXML = function (parameter, value) {
		if (value !== null && value !== undefined) {
			this.native.setSQLXML(parameter, value);
		} else {
			throw Error("Nullable SQLXML type not supported.");
		}
	};

	this.setBlob = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let blob = createBlobValue(this.native, value);
			this.native.setBlob(parameter, blob);
		} else {
			this.setNull(parameter, SQLTypes.BLOB);
		}
	};

	this.setClob = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let clob = createClobValue(this.native, value);
			this.native.setClob(parameter, clob);
		} else {
			this.setNull(parameter, SQLTypes.CLOB);
		}
	};

	this.setNClob = function (parameter, value) {
		if (value !== null && value !== undefined) {
			let nclob = createNClobValue(this.native, value);
			this.native.setNClob(parameter, nclob);
		} else {
			this.setNull(parameter, SQLTypes.NCLOB);
		}
	};

	this.execute = function () {
		return this.native.execute();
	};

	this.getMoreResults = function () {
		return this.native.getMoreResults();
	};

	this.getParameterMetaData = function () {
		return this.native.getParameterMetaData();
	};

	this.isClosed = function () {
		return this.native.isClosed();
	};


	this.close = function () {
		this.native.close();
	};
}

/**
 * ResultSet object
 */
function ResultSet(internalResultset?) {

	this.toJson = function (limited) {
		if (limited === undefined || limited === false) {
			limited = false;
		}
		return DatabaseResultSetHelper.toJson(this.native, limited, false);
	};

	this.close = function () {
		this.native.close();
	};

	this.getBigDecimal = function (identifier) {
		return this.native.getBigDecimal(identifier);
	};

	this.getBoolean = function (identifier) {
		return this.native.getBoolean(identifier);
	};

	this.getByte = function (identifier) {
		return this.native.getByte(identifier);
	};

	this.getBytes = function (identifier) {
		let data = this.native.getBytes(identifier);
		return bytes.toJavaScriptBytes(data);
	};

	this.getBytesNative = function (identifier) {
		return this.native.getBytes(identifier);
	};

	this.getBlob = function (identifier) {
		let data = readBlobValue(this.native.getBlob(identifier));
		return bytes.toJavaScriptBytes(data);
	};

	this.getBlobNative = function (identifier) {
		return readBlobValue(this.native.getBlob(identifier));
	};

	this.getClob = function (identifier) {
		return readClobValue(this.native.getClob(identifier));
	};

	this.getNClob = function (columnIndex) {
		return readNClobValue(this.native.getNClob(columnIndex));
	}

	this.getDate = function (identifier) {
		const dateInstance = this.native.getDate(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	this.getDouble = function (identifier) {
		return this.native.getDouble(identifier);
	};

	this.getFloat = function (identifier) {
		return this.native.getFloat(identifier);
	};

	this.getInt = function (identifier) {
		return this.native.getInt(identifier);
	};

	this.getLong = function (identifier) {
		return this.native.getLong(identifier);
	};

	this.getShort = function (identifier) {
		return this.native.getShort(identifier);
	};

	this.getString = function (identifier) {
		return this.native.getString(identifier);
	};

	this.getTime = function (identifier) {
		const dateInstance = this.native.getTime(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	this.getTimestamp = function (identifier) {
		const dateInstance = this.native.getTimestamp(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	this.isAfterLast = function () {
		return this.native.isAfterLast();
	};

	this.isBeforeFirst = function () {
		return this.native.isBeforeFirst();
	};

	this.isClosed = function () {
		return this.native.isClosed();
	};

	this.isFirst = function () {
		return this.native.isFirst();
	};

	this.isLast = function () {
		return this.native.isLast();
	};

	this.next = function () {
		return this.native.next();
	};

	this.getMetaData = function () {
		return this.native.getMetaData();
	}

	this.getNString = function (columnIndex) {
		return this.native.getNString(columnIndex);
	}
}

function isHanaDatabase(connection) {
	let isHanaDatabase = false;
	let metadata = connection.getMetaData();
	if (metadata !== null && metadata !== undefined) {
		isHanaDatabase = metadata.getDatabaseProductName() === "HDB";
	}
	return isHanaDatabase;
}

function readBlobValue(value) {
	return value.getBytes(1, value.length());
}

function createBlobValue(native, value) {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'Blob' value as the connection is null");
		}
		let blob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_BLOB (?) FROM DUMMY;");
				ps.setBytes(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					blob = rs.getBlob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			blob = connection.createBlob();
			blob.setBytes(1, value);
		}
		return blob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'Clob' value: ${e.message}`);
	}
}

function readClobValue(value) {
	return value.getSubString(1, value.length());
}

function createClobValue(native, value) {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'Clob' value as the connection is null");
		}
		let clob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_CLOB (?) FROM DUMMY;");
				ps.setString(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					clob = rs.getClob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			clob = connection.createClob();
			clob.setString(1, value);
		}
		return clob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'Clob' value: ${e.message}`);
	}
}

function readNClobValue(value) {
	return value.getSubString(1, value.length());
}

function createNClobValue(native, value) {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'NClob' value as the connection is null");
		}
		let nclob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_NCLOB (?) FROM DUMMY;");
				ps.setString(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					nclob = rs.getNClob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			nclob = connection.createNClob();
			nclob.setString(1, value);
		}
		return nclob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'NClob' value: ${e.message}`);
	}
}

function getDateValue(value) {
	if (typeof value === "string") {
		return new Date(value);
	}
	return value;
}