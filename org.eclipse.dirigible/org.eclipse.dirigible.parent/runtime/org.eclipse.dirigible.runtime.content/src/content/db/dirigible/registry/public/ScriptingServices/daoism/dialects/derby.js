/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var Derby = require('daoism/dialects/defaults').get();

var originalSqlTypeFor = Derby.sqlTypeFor;
Derby.sqlTypeFor = function(type, length){
	if(type === 'String'){
		if(length === undefined)
			return 'LONG VARCHAR';
		if(length === 1)
			return 'CHAR';				
		if(length < 32672)
			return 'VARCHAR';
		if(length >= 32672 && length < 32700)
			return 'LONG VARCHAR';
		if(length >= 32700)
			return 'CLOB';
	}
	return originalSqlTypeFor.apply(this, [type, length]);
};

Derby.builders.createtable = function(){
	var tableName = this.tableName || this.tables
								.map(function(table){
									return table.name;
								})[0];
	this.sql = 'CREATE TABLE ' + tableName;
	this.sql+= '(';
	var pks = [];
	for(var i in this.fieldSet){
		if(this.fieldSet[i].pk===true)
			pks.push(this.fieldSet[i]);
		var field =this.fieldSet[i];
		
		var notNullConstraint = field.required===false || field.pk===true ? ' NOT NULL' : '';
		
		var fieldType = Derby.sqlTypeFor(field.type, field.size);
		if(fieldType===undefined)
			throw Error('No SQL type mapped to field ' + field.dbName + ' type ' + field.type+ ' in Derby dialect');
		
		var defaultValueConstraint='';
		if(field.defaultValue!==undefined && field.pk!==true){
			defaultValueConstraint = field.defaultValue;
			if(field.type==='String')
				defaultValueConstraint = "'"+defaultValueConstraint+"'";
			defaultValueConstraint = ' DEFAULT ' + defaultValueConstraint;				
		}
		
		var sizeConstraint = field.size!==undefined && fieldType.toUpperCase()!=='LONG VARCHAR'?'('+field.size+')':'';
		
		this.sql += field.dbName+' '+fieldType + sizeConstraint + notNullConstraint+defaultValueConstraint+', ';
	}
	this.sql = this.sql.substring(0, this.sql.length-2);
	if(pks.length>0){	
		this.sql+=' , PRIMARY KEY (';
		for(var i=0;i<pks.length;i++){
			this.sql+=pks[i].dbName+', ';
		}
		this.sql = this.sql.substring(0, this.sql.length-2);
		this.sql+= ')';					
	}				
	this.sql+= ')';

	return this.sql;
};
Derby.builders.select = function(){
	this.sql = 'SELECT';
	if(!this.selectFields){
		this.sql += ' *';
	} else {
		this.sql += ' ' + this.selectFields
		.map(function(field){
			return field.alias?field.name + ' AS ' + field.alias: field.name;
		}).join(', ');
	}
	this.sql += ' FROM ' + this.tables
							.map(function(table){
								return table.name + (table.alias?' as '+ table.alias:'');
							}).join(',');
	if(this.leftJoins){
		this.sql += this.leftJoins
					.map(function(join){
						return ' LEFT JOIN ' + join.table + (join.alias?' \''+join.alias+'\'':'') + ' ON ' + join.statement;
					}).join(' ');
	}
	if(this.filters){
		this.sql += ' WHERE ' + this.filters.join(' AND ');
	}
	if(this.orderFields){
		this.sql += ' ORDER BY ' + this.orderFields
					.map(function(field){
						return field.name + ' ' + (field.order?'ASC':'DESC');
					})
					.join(', ');
	}
	if (this._limit!==undefined && this._offset!==undefined) {
        this.sql += " OFFSET " + this._offset +" ROWS FETCH NEXT " + this._limit + " ROWS ONLY";
    }
	return this.sql;
};

exports["Derby"] = Derby;

exports.get = function(){
	return Derby;
};
