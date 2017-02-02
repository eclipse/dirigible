/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java org */
/* eslint-env node, dirigible */

exports.getDatasource = function() {
	var internalDatasource = $.getDatasource();
	return new Datasource(internalDatasource);
};

exports.getNamedDatasource = function(name) {
	var internalDatasource = $.getNamedDatasource(name);
	return new Datasource(internalDatasource);
};

/**
 * Datasource object
 */
function Datasource(internalDatasource) {
	this.internalDatasource = internalDatasource;

	this.internalDbUtils = $.getDatabaseUtils(this.internalDatasource);

	this.getInternalObject = function() {
		return this.internalDatasource;
	};

	this.getConnection = function() {
		var internalConnection = this.internalDatasource.getConnection();
		return new Connection(internalConnection);
	};

	this.getSequence = function(name) {
		return new Sequence(this.internalDbUtils, name);
	};

	this.getPaging = function() {
		return new Paging(this.internalDbUtils);
	};
}

/**
 * Connection object
 */
function Connection(internalConnection) {
	this.internalConnection = internalConnection;

	this.getInternalObject = function() {
		return this.internalConnection;
	};

	this.prepareStatement = function(sql) {
		var internalStatement = this.internalConnection.prepareStatement(sql);
		return new Statement(internalStatement);
	};

	this.close = function() {
		this.internalConnection.close();
	};

	this.commit = function() {
		this.internalConnection.commit();
	};

	this.getAutoCommit = function() {
		return this.internalConnection.getAutoCommit();
	};

	this.getCatalog = function() {
		return this.internalConnection.getCatalog();
	};

	// getClientInfo
	// getMetaData

	this.getSchema = function() {
		return this.internalConnection.getSchema();
	};

	this.getTransactionIsolation = function() {
		return this.internalConnection.getTransactionIsolation();
	};

	this.isClosed = function() {
		return this.internalConnection.isClosed();
	};

	this.isReadOnly = function() {
		return this.internalConnection.isReadOnly();
	};

	this.isValid = function() {
		return this.internalConnection.isValid();
	};

	// prepareCall
	this.rollback = function() {
		this.internalConnection.rollback();
	};

	this.setAutoCommit = function(autoCommit) {
		this.internalConnection.setAutoCommit(autoCommit);
	};

	this.setCatalog = function(catalog) {
		this.internalConnection.setCatalog(catalog);
	};

	// setClientInfo
	this.setReadOnly = function(readOnly) {
		this.internalConnection.setReadOnly(readOnly);
	};

	this.setSchema = function(schema) {
		this.internalConnection.setSchema(schema);
	};

	this.setTransactionIsolation = function(transactionIsolation) {
		this.internalConnection.setTransactionIsolation(transactionIsolation);
	};
}

/**
 * Statement object
 */
function Statement(internalStatement) {
	this.internalStatement = internalStatement;

	this.getInternalObject = function() {
		return this.internalStatement;
	};

	this.close = function() {
		this.internalStatement.close();
	};

	this.execute = function() {
		return this.internalStatement.execute();
	};

	this.executeQuery = function() {
		var internalResultset = this.internalStatement.executeQuery();
		return new ResultSet(internalResultset);
	};

	this.executeUpdate = function() {
		return this.internalStatement.executeUpdate();
	};

	// getMetaData
	// setBigDecimal
	// setBlob
	
	this.SQLTypes = Object.freeze({
		"BOOLEAN": java.sql.Types.BOOLEAN,	
		"DATE": java.sql.Types.DATE,
		"DOUBLE": java.sql.Types.DOUBLE,
		"FLOAT": java.sql.Types.FLOAT,		
		"SMALLINT": java.sql.Types.SMALLINT,
		"INTEGER": java.sql.Types.INTEGER,
		"BIGINT": java.sql.Types.BIGINT,		
		"VARCHAR": java.sql.Types.VARCHAR,
		"TIME": java.sql.Types.TIME,
		"TIMESTAMP": java.sql.Types.TIMESTAMP
	});
	
	this.setNull = function(index, sqlType){
		this.internalStatement.setNull(index, sqlType);
	};

	this.setBoolean = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setBoolean(index, value);
		else
			this.setNull(index, this.SQLTypes.BOOLEAN);
	};

	// setByte
	// setBytes
	// setClob

	this.setDate = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setDate(index, new java.sql.Date(value.getTime()));
		else
			this.setNull(index, this.SQLTypes.DATE);
	};

	this.setDouble = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setDouble(index, value);
		else
			this.setNull(index, this.SQLTypes.DOUBLE);
	};

	this.setFloat = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setFloat(index, value);
		else
			this.setNull(index, this.SQLTypes.FLOAT);
	};

	this.setInt = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setInt(index, value);
		else
			this.setNull(index, this.SQLTypes.INTEGER);
	};

	this.setLong = function(index, value) {
		if(value!==null && value!==undefined)
			this.internalStatement.setLong(index, value);
		else{
			this.setNull(index, this.SQLTypes.BIGINT);
		}

	};

	this.setShort = function(index, value) {
		if(value!==null && value!==undefined)	
			this.internalStatement.setShort(index, value);
		else
			this.setNull(index, this.SQLTypes.SMALLINT);
	};

	this.setString = function(index, value) {
		if(value!==null && value!==undefined)		
			this.internalStatement.setString(index, value);
		else
			this.setNull(index, this.SQLTypes.VARCHAR);
	};

	this.setTime = function(index, value) {
		if(value!==null && value!==undefined)			
			this.internalStatement.setTime(index, new java.sql.Time(value.getTime()));
		else
			this.setNull(index, this.SQLTypes.TIME);			
	};

	this.setTimestamp = function(index, value) {
		if(value!==null && value!==undefined)		
			this.internalStatement.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
		else
			this.setNull(index, this.SQLTypes.TIMESTAMP);
	};
}

/**
 * ResultSet object
 */
function ResultSet(internalResultset) {
	this.internalResultset = internalResultset;

	this.getInternalObject = function() {
		return this.internalResultset;
	};

	this.close = function() {
		this.internalResultset.close();
	};

	// getBigDecimal
	// getBlob

	this.getBoolean = function(identifier) {
		return this.internalResultset.getBoolean(identifier);
	};

	// getByte
	// getBytes
	// getClob

	this.getDate = function(identifier) {
		return new Date(this.internalResultset.getDate(identifier).getTime());
	};

	this.getDouble = function(identifier) {
		return this.internalResultset.getDouble(identifier);
	};

	this.getFloat = function(identifier) {
		return this.internalResultset.getFloat(identifier);
	};

	this.getInt = function(identifier) {
		return this.internalResultset.getInt(identifier);
	};

	this.getLong = function(identifier) {
		return this.internalResultset.getLong(identifier);
	};

	this.getShort = function(identifier) {
		return this.internalResultset.getShort(identifier);
	};

	this.getString = function(identifier) {
		return this.internalResultset.getString(identifier);
	};

	this.getTime = function(identifier) {
		return new Date(this.internalResultset.getTime(identifier).getTime());
	};

	this.getTimestamp = function(identifier) {
		return new Date(this.internalResultset.getTimestamp(identifier).getTime());
	};

	this.isAfterLast = function(identifier) {
		return this.internalResultset.isAfterLast(identifier);
	};

	this.isBeforeFirst = function(identifier) {
		return this.internalResultset.isBeforeFirst(identifier);
	};

	this.isClosed = function(identifier) {
		return this.internalResultset.isClosed(identifier);
	};

	this.isFirst = function(identifier) {
		return this.internalResultset.isFirst(identifier);
	};

	this.isLast = function(identifier) {
		return this.internalResultset.isLast(identifier);
	};

	this.next = function() {
		return this.internalResultset.next();
	};
}

/**
 * Sequence object
 */
function Sequence(internalDbUtils, name) {
	this.internalDbUtils = internalDbUtils;

	this.getInternalObject = function() {
		return this.internalDbUtils;
	};

	this.name = name;

	this.getName = function() {
		return this.name;
	};

	this.create = function(start) {
		return this.internalDbUtils.createSequence(this.name, start);
	};

	this.next = function() {
		return this.internalDbUtils.getNext(this.name);
	};

	this.drop = function() {
		return this.internalDbUtils.dropSequence(this.name);
	};

	this.exists = function() {
		return this.internalDbUtils.existSequence(this.name);
	};

	//	this.createLimitAndOffset = dbutilsCreateLimitAndOffset;
	//	this.createTopAndStart = dbutilsCreateTopAndStart;
}

/**
 * Paging object
 */
function Paging(internalDbUtils) {
	this.internalDbUtils = internalDbUtils;

	this.getInternalObject = function() {
		return this.internalDbUtils;
	};

	this.genLimitAndOffset = function(limit, offset) {
		return this.internalDbUtils.createLimitAndOffset(limit, offset);
	};

	this.genTopAndStart = function(limit, offset) {
		return this.internalDbUtils.createTopAndStart(limit, offset);
	};
}
