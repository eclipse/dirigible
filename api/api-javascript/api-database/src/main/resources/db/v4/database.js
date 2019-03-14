/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Database
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

var bytes = require('io/v4/bytes');

exports.getDatabaseTypes = function() {
	var types = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getDatabaseTypes();
	if (types) {
		return JSON.parse(types);
	}
	return types;
};

exports.getDataSources = function(databaseType) {
	var datasources = databaseType ? 
		org.eclipse.dirigible.api.v3.db.DatabaseFacade.getDataSources(databaseType) :
		org.eclipse.dirigible.api.v3.db.DatabaseFacade.getDataSources();
	if (datasources) {
		return JSON.parse(datasources);
	}
	return datasources;
};

exports.getMetadata = function(databaseType, datasourceName) {
	var metadata;
	if (databaseType && databaseType) {
		metadata = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getMetadata(databaseType, datasourceName);
	} else if (databaseType && !databaseType) {
		metadata = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getMetadata(databaseType);
	} else {
		metadata = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getMetadata();
	}
	
	if (metadata) {
		return JSON.parse(metadata);
	}
	return metadata;
};

exports.getProductName = function(databaseType, datasourceName) {
	var productName;
	if (databaseType && databaseType) {
		productName = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getProductName(databaseType, datasourceName);
	} else if (databaseType && !databaseType) {
		productName = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getProductName(databaseType);
	} else {
		productName = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getProductName();
	}
	return productName;
};

exports.getConnection = function(databaseType, datasourceName) {
	var connection = new Connection();
	var native;
	if (databaseType && databaseType) {
		native = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getConnection(databaseType, datasourceName);
	} else if (databaseType && !databaseType) {
		native = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getConnection(databaseType);
	} else {
		native = org.eclipse.dirigible.api.v3.db.DatabaseFacade.getConnection();
	}
	connection.native = native;
	return connection;
};

/**
 * Connection object
 */
function Connection() {
	
	this.prepareStatement = function(sql) {
		var statement = new Statement();
		var native = this.native.prepareStatement(sql);
		statement.native = native;
		return statement;
	};

	this.close = function() {
		this.native.close();
	};

	this.commit = function() {
		this.native.commit();
	};

	this.getAutoCommit = function() {
		return this.native.getAutoCommit();
	};

	this.getCatalog = function() {
		return this.native.getCatalog();
	};

	this.getSchema = function() {
		return this.native.getSchema();
	};

	this.getTransactionIsolation = function() {
		return this.native.getTransactionIsolation();
	};

	this.isClosed = function() {
		return this.native.isClosed();
	};

	this.isReadOnly = function() {
		return this.native.isReadOnly();
	};

	this.isValid = function() {
		return this.native.isValid();
	};

	this.rollback = function() {
		return this.native.rollback();
	};

	this.setAutoCommit = function(autoCommit) {
		this.native.setAutoCommit(autoCommit);
	};

	this.setCatalog = function(catalog) {
		this.native.setCatalog(catalog);
	};

	this.setReadOnly = function(readOnly) {
		this.native.setReadOnly(readOnly);
	};

	this.setSchema = function(schema) {
		this.native.setSchema(schema);
	};

	this.setTransactionIsolation = function(transactionIsolation) {
		this.native.setTransactionIsolation(transactionIsolation);
	};
}


/**
 * Statement object
 */
function Statement(internalStatement) {

	this.close = function() {
		this.native.close();
	};

	this.executeQuery = function() {
		var resultset = new ResultSet();
		var native = this.native.executeQuery();
		resultset.native = native;
		return resultset;
	};

	this.executeUpdate = function() {
		return this.native.executeUpdate();
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
		"CHAR": 1,
		"CLOB": 2005,
		"BLOB": 2004,
		"VARBINARY": -3
	});
	
	this.setNull = function(index, sqlType){
		this.native.setNull(index, sqlType);
	};

	this.setBoolean = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setBoolean(index, value);
		} else {
			this.setNull(index, this.SQLTypes.BOOLEAN);
		}
	};
	
	this.setByte = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setByte(index, value);
		} else {
			this.setNull(index, this.SQLTypes.TINYINT);
		}
	};
	
	this.setClob = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setClob(index, value);
		} else {
			this.setNull(index, this.SQLTypes.CLOB);
		}
	};
	
	this.setBlob = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setBlob(index, value);
		} else {
			this.setNull(index, this.SQLTypes.BLOB);
		}
	};
	
	this.setBytesNative = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setBytes(index, value);
		} else {
			this.setNull(index, this.SQLTypes.VARBINARY);
		}
	};
	
	this.setBytes = function(index, value) {
		if(value!==null && value!==undefined) {
			var data = bytes.toJavaBytes(value);
			this.native.setBytes(index, data);
		} else {
			this.setNull(index, this.SQLTypes.VARBINARY);
		}
	};

	this.setDate = function(index, value) {
		if(value!==null && value!==undefined) {
			var dateInstance = new java.sql.Date(value.getTime());
			this.native.setDate(index, dateInstance);
		} else {
			this.setNull(index, this.SQLTypes.DATE);
		}
	};

	this.setDouble = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setDouble(index, value);
		} else {
			this.setNull(index, this.SQLTypes.DOUBLE);
		}
	};

	this.setFloat = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setFloat(index, value);
		} else {
			this.setNull(index, this.SQLTypes.FLOAT);
		}
	};

	this.setInt = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setInt(index, value);
		} else {
			this.setNull(index, this.SQLTypes.INTEGER);
		}
	};

	this.setLong = function(index, value) {
		index = parseInt(index, 10);//Rhino things.. 
		if(value!==null && value!==undefined) {
			this.native.setLong(index, value);
		} else {
			this.setNull(index, this.SQLTypes.BIGINT);
		}
	};

	this.setShort = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setShort(index, value);
		} else {
			this.setNull(index, this.SQLTypes.SMALLINT);
		}
	};

	this.setString = function(index, value) {
		if(value!==null && value!==undefined) {
			this.native.setString(index, value);
		} else {
			this.setNull(index, this.SQLTypes.VARCHAR);
		}
	};

	this.setTime = function(index, value) {
		if(value!==null && value!==undefined) {
			var timeInstance = new java.sql.Time(value.getTime());
			this.native.setTime(index, timeInstance);
		} else {
			this.setNull(index, this.SQLTypes.TIME);
		}
	};

	this.setTimestamp = function(index, value) {
		if(value!==null && value!==undefined) {
			var timestampInstance = new java.sql.Timestamp(value.getTime());
			this.native.setTimestamp(index, timestampInstance);
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
		this.native.close();
	};

	this.getBlob = function(identifier) {
		return this.native.getBlob(identifier);
	};
	
	this.getBigDecimal = function(identifier) {
		return this.native.getBigDecimal(identifier);
	};

	this.getBoolean = function(identifier) {
		return this.native.getBoolean(identifier);
	};
	
	this.getByte = function(identifier) {
		return this.native.getByte(identifier);
	};
	
	this.getBytesNative = function(identifier) {
		return this.native.getBytes(identifier);
	};
	
	this.getBytes = function(identifier) {
		var data = this.native.getBytes(identifier);
		return bytes.toJavaScriptBytes(data);
	};
	
	this.getClob = function(identifier) {
		return this.native.getClob(identifier);
	};

	this.getDate = function(identifier) {
		var dateInstance = this.native.getDate(identifier);
		var date = new Date(dateInstance.getTime());
		return date;
	};

	this.getDouble = function(identifier) {
		return this.native.getDouble(identifier);
	};

	this.getFloat = function(identifier) {
		return this.native.getFloat(identifier);
	};

	this.getInt = function(identifier) {
		return this.native.getInt(identifier);
	};

	this.getLong = function(identifier) {
		return this.native.getLong(identifier);
	};

	this.getShort = function(identifier) {
		return this.native.getShort(identifier);
	};

	this.getString = function(identifier) {
		return this.native.getString(identifier);
	};

	this.getTime = function(identifier) {
		var dateInstance = this.native.getTime(identifier);
		var date = new Date(dateInstance.getTime());
		return date;
	};

	this.getTimestamp = function(identifier) {
		var dateInstance = this.native.getTimestamp(identifier);
		var date = new Date(dateInstance.getTime());
		return date;
	};

	this.isAfterLast = function(identifier) {
		return this.native.isAfterLast();
	};

	this.isBeforeFirst = function(identifier) {
		return this.native.isBeforeFirst();
	};

	this.isClosed = function(identifier) {
		return this.native.isClosed();
	};

	this.isFirst = function(identifier) {
		return this.native.isFirst();
	};

	this.isLast = function(identifier) {
		return this.native.isLast();
	};

	this.next = function() {
		return this.native.next();
	};
}
