/* globals $ */
/* eslint-env node, dirigible */
"use strict";
var StatementBuilder = function(dialect){
	this.dialect = dialect;
};

StatementBuilder.prototype.insert = function(){
	this.operation = "INSERT";
	return this;
};
StatementBuilder.prototype.update = function(){
	this.operation = "UPDATE";
	return this;
};
StatementBuilder.prototype.remove = StatementBuilder.prototype['delete'] = function(){
	this.operation = "DELETE";
	return this;
};
StatementBuilder.prototype.select = function(){
	this.operation = "SELECT";
	return this;
};
StatementBuilder.prototype.createTable = function(tableName){
	this.tableName = tableName;
	this.operation = "CREATETABLE";
	return this;
};
StatementBuilder.prototype.dropTable = function(tableName){
	this.tableName = tableName;
	this.operation = "DROPTABLE";
	return this;
};
StatementBuilder.prototype.from = function(tableName, alias){
	if(!this.tables){
		this.tables = [];//why array? Get ready for future support of multiple table operations
	}
	this.tables.push({
		name: tableName,
		alias: alias
	});
	return this;
};
StatementBuilder.prototype.into = StatementBuilder.prototype.table = function(tableName){
	if(!this.tables){
		this.tables = [];//why array? Get ready for future support of multiple table operations
	}
	this.tables.push({
		name: tableName
	});
	return this;
};
StatementBuilder.prototype.order = function(orderField, asc){
	if(!this.orderFields){
		this.orderFields = [];//why array? Get ready for future support of multiple table operations
	}
	this.orderFields.push({
		name: orderField,
		order: asc===undefined || asc===true ? true : false
	});
	return this;
};
StatementBuilder.prototype.limit = function (_limit){

	this._limit = _limit;
	return this;	
};
StatementBuilder.prototype.offset = function (_offset){
	this._offset = _offset;
	return this;	
};
StatementBuilder.prototype.where = function(filter, parameterizedFields){
	if(!this.filters){
		this.filters = [];
	}
	this.filters.push(filter);
	if(!this.fieldSet)
		this.fieldSet = [];
	if(parameterizedFields && parameterizedFields.constructor!==Array){
		parameterizedFields = [parameterizedFields];
	}		
	if(parameterizedFields){
		if(!this.parameterizedFields)
			this.parameterizedFields = [];
		this.parameterizedFields = this.parameterizedFields.concat(parameterizedFields);
		if(!this.fieldValueSet)
			this.fieldValueSet = [];
		this.fieldValueSet = this.fieldValueSet
								.concat(parameterizedFields
										.map(function(){ 
											return '?'; 
										}));
	}
	return this;
};
StatementBuilder.prototype.left_join = function(table, tableAlias, joinStatement, parameterizedFields){
	if(!this.leftJoins){
		this.leftJoins = [];
	}
	this.leftJoins.push({
		table: table,
		alias: tableAlias,
		statement: joinStatement
	});	
	if(parameterizedFields && parameterizedFields.constructor!==Array){
		parameterizedFields = [parameterizedFields];
	}	
	if(parameterizedFields){
		if(!this.parameterizedFields)
			parameterizedFields = [];
		this.parameterizedFields = this.parameterizedFields.concat(parameterizedFields);
		if(!this.fieldValueSet)
			this.fieldValueSet = [];
		this.fieldValueSet = this.fieldValueSet
								.concat(parameterizedFields
											.map(function(){
												return '?'; 
											}));	
	}
	return this;
};
StatementBuilder.prototype.set = function(fieldDef, value){
	if(!this.updFieldSet)
		this.updFieldSet = [];
	this.updFieldSet.push(fieldDef);
	if(value===undefined){
		if(!this.parameterizedFields)
			this.parameterizedFields = [];
		this.parameterizedFields.push(fieldDef);
	}
	if(!this.fieldValueSet)
		this.fieldValueSet = [];
	this.fieldValueSet.push(value!==undefined?value:'?');
	return this;
};
StatementBuilder.prototype.field = function(fieldName, alias){
	if(!this.selectFields)
		this.selectFields = [];
	this.selectFields.push({
		name: fieldName,
		alias: alias
	});
	return this;
};
StatementBuilder.prototype.fieldDef = function(fieldDef){
	if(!this.fieldSet)
		this.fieldSet = [];
	this.fieldSet.push({
		dbName: fieldDef.dbName,
		type: fieldDef.type,
		size: fieldDef.size,
		required: fieldDef.required || true,
		pk: fieldDef.pk || false,
		defaultValue: fieldDef.defaultValue
	});
	return this;
};

StatementBuilder.prototype.toString = function(){
	if(this.operation===undefined)
		throw Error('Missing operation. Forgot to invoke insert/delete/update/select on the statement builder?');
	return this.dialect.builders[this.operation.toLowerCase()].apply(this);
};
StatementBuilder.prototype.toParams = function(){
	return {
		sql: this.toString(),
		parameters: this.parameterizedFields
	};
};

exports.StatementBuilder = StatementBuilder;

var getStatementBuilder = exports.getStatementBuilder = function(dialect){
	dialect = dialect || this.dialect;
	if(!dialect){
		var conn, databaseName;
		try{
			conn = require("db/database").getDatasource().getConnection();
			databaseName = conn.internalConnection.getMetaData().getDatabaseProductName();
		} finally {
			conn.close();
		}
		dialect = require("daoism/dialects/dialects").get().getDialect(databaseName);
	}
	return new StatementBuilder(dialect);
};

var Statements = exports.Statements = function(){
	this.$log = require('log/loggers').get('daoism/statements');
};
exports.get = function(){
	return new Statements();
};

Statements.prototype.builder = getStatementBuilder;

Statements.prototype.execute = function(statementBuilder, connection, entity){
	if(statementBuilder.constructor !== StatementBuilder)
		throw Error('Expected StatementBuilder argument but was: ' + (typeof statementBuilder));
	var parametricQuery = statementBuilder.toParams();
	var sql = parametricQuery.sql;
	this.$log.info('Executing SQL Statement: '+ sql);
	var statement, result;
 	statement = connection.prepareStatement(sql);
 	var parametricFields = parametricQuery.parameters;
 	if(parametricFields){
	 	for(var i=0; i<parametricFields.length; i++){
	 		var val = entity ? entity[parametricFields[i].name] : undefined;
	 		this.$log.info('Binding to parameter['+(i+1)+']: '+ val);
	 		statement['set'+parametricFields[i].type](i+1, val);
	 	} 	
 	}
 	if(statementBuilder.operation!==undefined){
 		if(statementBuilder.operation.toLowerCase()!=='select'){
 			result = statement.executeUpdate();
 		} else {
 			result = statement.executeQuery();
 		}
 	}
 	return result;
};
