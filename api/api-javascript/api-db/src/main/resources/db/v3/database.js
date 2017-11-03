/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

exports.getDatabaseTypes = function() {
	var types = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getDatabaseTypes', []);
	if (types) {
		return JSON.parse(types);
	}
	return types;
};

exports.getDataSources = function(databaseType) {
	var datasources = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getDataSources', [databaseType]);
	if (datasources) {
		return JSON.parse(datasources);
	}
	return datasources;
};

exports.getMetadata = function(databaseType, datasourceName) {
	var metadata = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getMetadata', [databaseType, datasourceName]);
	if (metadata) {
		return JSON.parse(metadata);
	}
	return metadata;
};

exports.getConnection = function(databaseType, datasourceName) {
	var connectionInstance = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'getConnection', [databaseType, datasourceName], true);
	var connection = new Connection();
	connection.uuid = connectionInstance.uuid;
	return connection;
};

/**
 * Connection object
 */
function Connection() {
	
	this.prepareStatement = function(sql) {
		var statementInstance = java.invoke(this.uuid, 'prepareStatement', [sql], true);
		var statement = new Statement();
		statement.uuid = statementInstance.uuid;
		return statement;
	};

	this.close = function() {
		java.invoke(this.uuid, 'close', []);
	};

	this.commit = function() {
		java.invoke(this.uuid, 'commit', []);
	};

	this.getAutoCommit = function() {
		return java.invoke(this.uuid, 'getAutoCommit', []);
	};

	this.getCatalog = function() {
		return java.invoke(this.uuid, 'getCatalog', []);
	};

	this.getSchema = function() {
		return java.invoke(this.uuid, 'getSchema', []);
	};

	this.getTransactionIsolation = function() {
		return java.invoke(this.uuid, 'getTransactionIsolation', []);
	};

	this.isClosed = function() {
		return java.invoke(this.uuid, 'isClosed', []);
	};

	this.isReadOnly = function() {
		return java.invoke(this.uuid, 'isReadOnly', []);
	};

	this.isValid = function() {
		return java.invoke(this.uuid, 'isValid', []);
	};

	this.rollback = function() {
		return java.invoke(this.uuid, 'rollback', []);
	};

	this.setAutoCommit = function(autoCommit) {
		java.invoke(this.uuid, 'setAutoCommit', [autoCommit]);
	};

	this.setCatalog = function(catalog) {
		java.invoke(this.uuid, 'setCatalog', [catalog]);
	};

	this.setReadOnly = function(readOnly) {
		java.invoke(this.uuid, 'setReadOnly', [readOnly]);
	};

	this.setSchema = function(schema) {
		java.invoke(this.uuid, 'setSchema', [schema]);
	};

	this.setTransactionIsolation = function(transactionIsolation) {
		java.invoke(this.uuid, 'setTransactionIsolation', [transactionIsolation]);
	};
}


/**
 * Statement object
 */
function Statement(internalStatement) {

	this.close = function() {
		java.invoke(this.uuid, 'close', []);
	};

	this.executeQuery = function() {
		var resultsetInstance = java.invoke(this.uuid, 'executeQuery', [], true);
		var resultset = new ResultSet();
		resultset.uuid = resultsetInstance.uuid;
		return resultset;
	};

	this.executeUpdate = function() {
		return java.invoke(this.uuid, 'executeUpdate', []);
	};

	// getMetaData
	// setBigDecimal
	// setBlob
	
	this.SQLTypes = Object.freeze({
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
		"CHAR": 1
		
	});
	
	this.setNull = function(index, sqlType){
		java.invoke(this.uuid, 'setNull', [index, sqlType]);
	};

	this.setBoolean = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setBoolean', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.BOOLEAN);
		}
	};

	// setByte
	// setBytes
	// setClob

	this.setDate = function(index, value) {
		if(value!==null && value!==undefined) {
			var dateInstance = java.instantiate('java.sql.Date', [value.getTime()]);
			try {
				java.invoke(this.uuid, 'setDate', [index, dateInstance.uuid]);
			} finally {
				java.free(dateInstance.uuid);
			}
		} else {
			this.setNull(index, this.SQLTypes.DATE);
		}
	};

	this.setDouble = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setDouble', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.DOUBLE);
		}
	};

	this.setFloat = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setFloat', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.FLOAT);
		}
	};

	this.setInt = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setInt', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.INTEGER);
		}
	};

	this.setLong = function(index, value) {
		index = parseInt(index, 10);//Rhino things.. 
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setLong', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.BIGINT);
		}
	};

	this.setShort = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setShort', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.SMALLINT);
		}
	};

	this.setString = function(index, value) {
		if(value!==null && value!==undefined) {
			java.invoke(this.uuid, 'setString', [index, value]);
		} else {
			this.setNull(index, this.SQLTypes.VARCHAR);
		}
	};

	this.setTime = function(index, value) {
		if(value!==null && value!==undefined) {
			var timeInstance = java.instantiate('java.sql.Time', [value.getTime()]);
			try {
				java.invoke(this.uuid, 'setTime', [index, timeInstance.uuid]);
			} finally {
				java.free(timeInstance.uuid);
			}
		} else {
			this.setNull(index, this.SQLTypes.TIME);
		}
	};

	this.setTimestamp = function(index, value) {
		if(value!==null && value!==undefined) {
			var timestampInstance = java.instantiate('java.sql.Timestamp', [value.getTime()]);
			try {
				java.invoke(this.uuid, 'setTimestamp', [index, timestampInstance.uuid]);
			} finally {
				java.free(timestampInstance.uuid);
			}
		} else {
			this.setNull(index, this.SQLTypes.TIMESTAMP);
		}
	};
}

/**
 * ResultSet object
 */
function ResultSet(internalResultset) {

	this.close = function() {
		java.invoke(this.uuid, 'close', []);
	};

	// getBigDecimal
	// getBlob

	this.getBoolean = function(identifier) {
		return java.invoke(this.uuid, 'getBoolean', [identifier]);
	};

	// getByte
	// getBytes
	// getClob

	this.getDate = function(identifier) {
		var dateInstance = java.invoke(this.uuid, 'getDate', [identifier], true);
		var date = new Date(java.invoke(dateInstance.uuid, 'getTime', []));
		return date;
	};

	this.getDouble = function(identifier) {
		return java.invoke(this.uuid, 'getDouble', [identifier]);
	};

	this.getFloat = function(identifier) {
		return java.invoke(this.uuid, 'getFloat', [identifier]);
	};

	this.getInt = function(identifier) {
		return java.invoke(this.uuid, 'getInt', [identifier]);
	};

	this.getLong = function(identifier) {
		return java.invoke(this.uuid, 'getLong', [identifier]);
	};

	this.getShort = function(identifier) {
		return java.invoke(this.uuid, 'getShort', [identifier]);
	};

	this.getString = function(identifier) {
		return java.invoke(this.uuid, 'getString', [identifier]);
	};

	this.getTime = function(identifier) {
		var dateInstance = java.invoke(this.uuid, 'getTime', [identifier], true);
		var date = new Date(java.invoke(dateInstance.uuid, 'getTime', []));
		return date;
	};

	this.getTimestamp = function(identifier) {
		var dateInstance = java.invoke(this.uuid, 'getTimestamp', [identifier], true);
		var date = new Date(java.invoke(dateInstance.uuid, 'getTime', []));
		return date;
	};

	this.isAfterLast = function(identifier) {
		return java.invoke(this.uuid, 'isAfterLast', []);
	};

	this.isBeforeFirst = function(identifier) {
		return java.invoke(this.uuid, 'isBeforeFirst', []);
	};

	this.isClosed = function(identifier) {
		return java.invoke(this.uuid, 'isClosed', []);
	};

	this.isFirst = function(identifier) {
		return java.invoke(this.uuid, 'isFirst', []);
	};

	this.isLast = function(identifier) {
		return java.invoke(this.uuid, 'isLast', []);
	};

	this.next = function() {
		return java.invoke(this.uuid, 'next', []);
	};
}

