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
"use strict";

var ORMStatements = function(orm, dialect){
	this.$log = require('log/v4/logging').getLogger('db.dao.ormstatements');
	this.orm = orm;
	this.orm.tableName = this.orm.table;
	this.orm.properties.forEach(function(property) {
		property.columnName = property.column;
	});
	this.dialect = dialect || require('db/v4/sql').getDialect();
};
ORMStatements.prototype.constructor = ORMStatements;

ORMStatements.prototype.createTable = function(){
	const builder = this.dialect.create().table(this.orm.table);

	this.orm.properties.forEach(function(property){
		const column = this.orm.toColumn(property);
		if(property.type.toUpperCase() === 'VARCHAR'){
			if(property.length === undefined)
				property.length = 255;
			builder.columnVarchar(column.name, property.length, column.primaryKey === 'true', column.nullable === 'true', property.unique);
		} else if(property.type.toUpperCase() === 'CHAR'){
			if(property.length === undefined)
				property.length = 1;
			property.length = parseInt(property.length, 10);
			builder.columnChar(column.name, property.length, column.primaryKey === 'true', column.nullable === 'true', property.unique);
		} else {
			builder.column(column.name, column.type, column.primaryKey === 'true', column.nullable === 'true', property.unique);
		}
	}.bind(this));

	return builder;
};

ORMStatements.prototype.dropTable = function(){
	return this.dialect.drop().table(this.orm.table);
};

ORMStatements.prototype.insert = function(){
	const builder = this.dialect.insert().into(this.orm.table);
	this.orm.properties.forEach(function(property){
		builder.column(property.column).value('?', property);
	});
    return builder;
};

ORMStatements.prototype.update = function(entity){
	if(!entity)
		throw Error('Illegal argument: entity[' + entity + ']');

	const builder = this.dialect.update().table(this.orm.table);
	this.orm.properties.filter(function(property){
		return Object.keys(entity).indexOf(property.name)>-1 && (!property.allowedOps || property.allowedOps.indexOf('update')>-1);
	}).forEach(function(property){
		if(!property.id)
			builder.set(property.column, '?', property);
	});
	const pkProperty = this.orm.getPrimaryKey();
	builder.where(pkProperty.column+'=?', [pkProperty]);
	return builder;
};
ORMStatements.prototype["delete"] = ORMStatements.prototype.remove = function(){
	const builder = this.dialect.delete().from(this.orm.table);
	if(arguments[0]!==undefined){
		let filterFieldNames = arguments[0];
		if(filterFieldNames.constructor!==Array)
			filterFieldNames= [filterFieldNames];
		for(let i=0; i<filterFieldNames.length; i++){
			const property = this.orm.getProperty(filterFieldNames[i]);
			if(!property)
				throw Error('Unknown property name: ' + filterFieldNames[i]+" in $filter");
			builder.where(property.column + "=?", [property]);
		}
	}
	return builder;
};
ORMStatements.prototype.find = function(params){
	let builder = this.dialect.select();
	if(params!==undefined && params.select!==undefined){
		const selectedFields = params.select.constructor === Array ? params.select : [params.select];
		for(let i=0; i<selectedFields.length; i++){
			const property = this.orm.getProperty(selectedFields[i]);
			if(!property)
				throw Error('Unknown field name ['+ selectedFields[i] + '] in $select');
			builder = builder.column(property.column);
		}
	}
	builder = builder.from(this.orm.table)
		.where(this.orm.getPrimaryKey().column + "=?", [this.orm.getPrimaryKey()]);
	return builder;
};
ORMStatements.prototype.count = function(){
	return this.dialect.select().column('COUNT(*)').from(this.orm.table);
};
ORMStatements.prototype.list= function(settings){
	let i;
	settings = settings || {};
	const limit = settings.$limit || settings.limit;
	const offset = settings.$offset || settings.offset;
	const sort = settings.$sort || settings.sort;
	const order = settings.$order || settings.order;
	const selectedFields = settings.$select || settings.select;

	const builder = this.dialect.select().from(this.orm.table);

	//add selected fields if any
	if(selectedFields){
		for(i = 0; i<selectedFields.length; i++){
			const property = this.orm.getProperty(selectedFields[i]);
			if(!property)
				throw Error('Unknown field name ['+ selectedFields[i] + '] in $select');
			builder.column(property.column);
		}
	}

    //add where clause for any fields
	const propertyDefinitions = this.orm.properties.filter(function (property) {
		for (var settingName in settings) {
			if (settingName === property.name)
				return true;
		}
		return false;
	});
	if(propertyDefinitions.length>0){
		for(i = 0; i<propertyDefinitions.length; i++){
			const def = propertyDefinitions[i];
			if(settings.$filter && settings.$filter.indexOf(def.name)>-1){
	    		builder.where(def.column + ' LIKE ?', [def]);
	   		} else {
				const val = settings[def.name];
				if(val === null || val === undefined){
	   				builder.where(def.column + ' IS NULL', [def]);
	   			} else {
	   				if(val.indexOf && val.indexOf('>')>-1){
		   				builder.where(def.column + ' > ?', [def]);
		   			} else if(val.indexOf && val.indexOf('<')>-1){
		   				builder.where(def.column + ' < ?', [def]);
		   			} else{
		   				builder.where(def.column + '=?', [def]);
	   				}
	   			}
        	}
    	}
    }

    if (sort !== undefined) {
    	var _sort = sort.split(',');
        for(i = 0; i<_sort.length; i++){
			let _order = true;//ASC
        	//TODO: change to be able to order per sort property
	        if (order !== undefined) {
	            if(['asc','desc'].indexOf(String(order).toLowerCase())>-1){
	            	_order = order.toLowerCase() === 'desc' ? false : true;
	            }
	        }
	        if (this.orm.getProperty(_sort[i])) {
	        	builder.order(this.orm.getProperty(_sort[i]).column, _order);
	        } else {
	        	console.error('Column: ' + _sort[i] + ' not present in ' + JSON.stringify(this.orm));
	        }

    	}
    }
    if (limit !== undefined && offset !== undefined) {
        builder.limit(parseInt(limit,10)).offset(parseInt(offset,10));
    }
    return builder;
};

exports.ORMStatements = ORMStatements;

exports.create = function(orm, connection){
	let dialect;
	if(connection)
		dialect = require('db/v4/sql').getDialect(connection);
	const stmnts = new ORMStatements(orm, dialect);
	return stmnts;
};
