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
	this.getInternalObject = datasourceGetInternalObject;
	this.getConnection = datasourceGetConnection;
	this.getSequence = datasourceGetSequence;
	this.getPaging = datasourceGetPaging;
}

function datasourceGetInternalObject() {
	return this.internalDatasource;
}

function datasourceGetConnection() {
	var internalConnection = this.internalDatasource.getConnection();
	return new Connection(internalConnection);
}

function datasourceGetSequence(name) {
	return new Sequence(this.internalDbUtils, name);
}

function datasourceGetPaging() {
	return new Paging(this.internalDbUtils);
}

/**
 * Connection object
 */
function Connection(internalConnection) {
	this.internalConnection = internalConnection;
	this.getInternalObject = connectionGetInternalObject;
	this.prepareStatement = prepareStatement;
	this.close = connectionClose;
	this.commit = connectionCommit;
	this.getAutoCommit = connectionGetAutoCommit;
	this.getCatalog = connectionGetCatalog;
	// getClientInfo
	// getMetaData
	this.getSchema = connectionGetSchema;
	this.getTransactionIsolation = connectionGetTransactionIsolation;
	this.isClosed = connectionIsClosed;
	this.isReadOnly = connectionIsReadOnly;
	this.isValid = connectionIsValid;
	// prepareCall
	this.rollback = connectionRollback;
	this.setAutoCommit = connectionSetAutoCommit;
	this.setCatalog = connectionSetCatalog;
	// setClientInfo
	this.setReadOnly = connectionSetReadOnly;
	this.setSchema = connectionSetSchema;
	this.setTransactionIsolation = connectionSetTransactionIsolation;
}

function connectionGetInternalObject() {
	return this.internalConnection;
}

function prepareStatement(sql) {
	var internalStatement = this.internalConnection.prepareStatement(sql);
	return new Statement(internalStatement);
}

function connectionClose() {
	this.internalConnection.close();
}

function connectionCommit() {
	this.internalConnection.commit();
}

function connectionGetAutoCommit() {
	return this.internalConnection.getAutoCommit();
}

function connectionGetCatalog() {
	return this.internalConnection.getCatalog();
}

function connectionGetSchema() {
	return this.internalConnection.getSchema();
}

function connectionGetTransactionIsolation() {
	return this.internalConnection.getTransactionIsolation();
}

function connectionIsClosed() {
	return this.internalConnection.isClosed();
}

function connectionIsReadOnly() {
	return this.internalConnection.isReadOnly();
}

function connectionIsValid() {
	return this.internalConnection.isValid();
}

function connectionRollback() {
	this.internalConnection.rollback();
}

function connectionSetAutoCommit(autoCommit) {
	this.internalConnection.setAutoCommit(autoCommit);
}

function connectionSetCatalog(catalog) {
	this.internalConnection.setCatalog(catalog);
}

function connectionSetReadOnly(readOnly) {
	this.internalConnection.setReadOnly(readOnly);
}

function connectionSetSchema(schema) {
	this.internalConnection.setSchema(schema);
}

function connectionSetTransactionIsolation(transactionIsolation) {
	this.internalConnection.setTransactionIsolation(transactionIsolation);
}


/**
 * Statement object
 */
function Statement(internalStatement) {
	this.internalStatement = internalStatement;
	this.getInternalObject = statementGetInternalObject;
	this.close = statementClose;
	
	this.execute = statementExecute;
	this.executeQuery = statementExecuteQuery;
	this.executeUpdate = statementExecuteUpdate;
	// getMetaData
	// setBigDecimal
	// setBlob
	this.setBoolean = statementSetBoolean;
	// setByte
	// setBytes
	// setClob
	this.setDate = statementSetDate;
	this.setDouble = statementSetDouble;
	this.setFloat = statementSetFloat;
	this.setInt = statementSetInt;
	this.setLong = statementSetLong;
	this.setShort = statementSetShort;
	this.setString = statementSetString;
	this.setTime = statementSetTime;
	this.setTimestamp = statementSetTimestamp;
}

function statementGetInternalObject() {
	return this.internalStatement;
}

function statementClose() {
	this.internalStatement.close();
}

function statementExecute() {
	return this.internalStatement.execute();
}

function statementExecuteQuery() {
	var internalResultset = this.internalStatement.executeQuery();
	return new ResultSet(internalResultset);
}

function statementExecuteUpdate() {
	return this.internalStatement.executeUpdate();
}

function statementSetBoolean(index, value) {
	this.internalStatement.setBoolean(index, value);
}

function statementSetDate(index, value) {
	this.internalStatement.setDate(index, new java.sql.Date(value.getTime()));
}

function statementSetDouble(index, value) {
	this.internalStatement.setDouble(index, value);
}

function statementSetFloat(index, value) {
	this.internalStatement.setFloat(index, value);
}

function statementSetInt(index, value) {
	this.internalStatement.setInt(index, value);
}

function statementSetLong(index, value) {
	this.internalStatement.setLong(index, value);
}

function statementSetShort(index, value) {
	this.internalStatement.setShort(index, value);
}

function statementSetString(index, value) {
	this.internalStatement.setString(index, value);
}

function statementSetTime(index, value) {
	this.internalStatement.setTime(index, new java.sql.Time(value.getTime()));
}

function statementSetTimestamp(index, value) {
	this.internalStatement.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
}

/**
 * ResultSet object
 */
function ResultSet(internalResultset) {
	this.internalResultset = internalResultset;
	this.getInternalObject = resultsetGetInternalObject;
	this.close = resultsetClose;
	// getBigDecimal
	// getBlob
	this.getBoolean = resultsetGetBoolean;
	// getByte
	// getBytes
	// getClob
	this.getDate = resultsetGetDate;
	this.getDouble = resultsetGetDouble;
	this.getFloat = resultsetGetFloat;
	this.getInt = resultsetGetInt;
	this.getLong = resultsetGetLong;
	this.getShort = resultsetGetShort;
	this.getString = resultsetGetString;
	this.getTime = resultsetGetTime;
	this.getTimestamp = resultsetGetTimestamp;
	this.isAfterLast = resultsetIsAfterLast;
	this.isBeforeFirst = resultsetIsBeforeFirst;
	this.isClosed = resultsetIsClosed;
	this.isFirst = resultsetIsFirst;
	this.isLast = resultsetIsLast;
	this.next = resultsetNext;
}

function resultsetGetInternalObject() {
	return this.internalResultset;
}

function resultsetClose() {
	this.internalResultset.close();
}

function resultsetGetBoolean(identifier) {
	return this.internalResultset.getBoolean(identifier);
}

function resultsetGetDate(identifier) {
	return new Date(this.internalResultset.getDate(identifier).getTime());
}

function resultsetGetDouble(identifier) {
	return this.internalResultset.getDouble(identifier);
}

function resultsetGetFloat(identifier) {
	return this.internalResultset.getFloat(identifier);
}

function resultsetGetInt(identifier) {
	return this.internalResultset.getInt(identifier);
}

function resultsetGetLong(identifier) {
	return this.internalResultset.getLong(identifier);
}

function resultsetGetShort(identifier) {
	return this.internalResultset.getShort(identifier);
}

function resultsetGetString(identifier) {
	return this.internalResultset.getString(identifier);
}

function resultsetGetTime(identifier) {
	return new Date(this.internalResultset.getTime(identifier).getTime());
}

function resultsetGetTimestamp(identifier) {
	return new Date(this.internalResultset.getTimestamp(identifier).getTime());
}

function resultsetIsAfterLast(identifier) {
	return this.internalResultset.isAfterLast(identifier);
}

function resultsetIsBeforeFirst(identifier) {
	return this.internalResultset.isBeforeFirst(identifier);
}

function resultsetIsClosed(identifier) {
	return this.internalResultset.isClosed(identifier);
}

function resultsetIsFirst(identifier) {
	return this.internalResultset.isFirst(identifier);
}

function resultsetIsLast(identifier) {
	return this.internalResultset.isLast(identifier);
}

function resultsetNext() {
	return this.internalResultset.next();
}


/**
 * Sequence object
 */
function Sequence(internalDbUtils, name) {
	this.internalDbUtils = internalDbUtils;
	this.getInternalObject = sequenceGetInternalObject;
	this.name = name;
	this.getName = sequenceGetName;
	this.create = sequenceCreateSequence;
	this.next = sequenceNextSequence;
	this.drop = sequenceDropSequence;
	this.exists = sequenceExistsSequence;
//	this.createLimitAndOffset = dbutilsCreateLimitAndOffset;
//	this.createTopAndStart = dbutilsCreateTopAndStart;
}

function sequenceGetInternalObject() {
	return this.internalDbUtils;
}

function sequenceGetName() {
	return this.name;
}

function sequenceCreateSequence(start) {
	return this.internalDbUtils.createSequence(this.name, start);
}

function sequenceNextSequence() {
	return this.internalDbUtils.getNext(this.name);
}

function sequenceDropSequence() {
	return this.internalDbUtils.dropSequence(this.name);
}

function sequenceExistsSequence() {
	return this.internalDbUtils.existSequence(this.name);
}

/**
 * Paging object
 */
function Paging(internalDbUtils) {
	this.internalDbUtils = internalDbUtils;
	this.getInternalObject = pagingGetInternalObject;
	this.genLimitAndOffset = pagingCreateLimitAndOffset;
	this.genTopAndStart = pagingCreateTopAndStart;
}

function pagingGetInternalObject() {
	return this.internalDbUtils;
}

function pagingCreateLimitAndOffset(limit, offset) {
	return this.internalDbUtils.createLimitAndOffset(limit, offset);
}

function pagingCreateTopAndStart(limit, offset) {
	return this.internalDbUtils.createTopAndStart(limit, offset);
}
