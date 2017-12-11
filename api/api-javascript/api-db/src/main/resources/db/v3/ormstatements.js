/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

"use strict";

var ORMStatements = function(orm, dialect){
	this.$log = require('log/logging').getLogger('db.dao.ormstatements');
	this.orm = orm;
	this.dialect = dialect || require('db/v3/sql').getDialect();
};
ORMStatements.prototype.constructor = ORMStatements;

ORMStatements.prototype.createTable = function(){
	var builder = this.dialect.create().table(this.orm.table);

	this.orm.properties.forEach(function(property){
		var column = this.orm.toColumn(property);
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
	var builder = this.dialect.insert().into(this.orm.table);
	this.orm.properties.forEach(function(property){
		builder.column(property.column).value('?', property);
	});
    return builder;
};

ORMStatements.prototype.update = function(entity){
	if(!entity)
		throw Error('Illegal argument: entity[' + entity + ']');
	
	var builder = this.dialect.update().table(this.orm.table);
	this.orm.properties.filter(function(property){
		return Object.keys(entity).indexOf(property.name)>-1 && (!property.allowedOps || property.allowedOps.indexOf('update')>-1);
	}).forEach(function(property){
		if(!property.id)
			builder.set(property.column, '?', property);
	});
	var pkProperty = this.orm.getPrimaryKey();
	builder.where(pkProperty.column+'=?', [pkProperty]);
	return builder;
};
ORMStatements.prototype["delete"] = ORMStatements.prototype.remove = function(){
	var builder = this.dialect.delete().from(this.orm.table);
	if(arguments[0]!==undefined){
		var filterFieldNames = arguments[0];
		if(filterFieldNames.constructor!==Array)
			filterFieldNames= [filterFieldNames];
		for(var i=0; i<filterFieldNames.length; i++){
			var property = this.orm.getProperty(filterFieldNames[i]);
			if(!property)
				throw Error('Unknown property name: ' + filterFieldNames[i]+" in $filter");
			builder.where(property.column + "=?", [property]);
		}
	}
	return builder;
};
ORMStatements.prototype.find = function(params){
	var builder = this.dialect.select();
	if(params!==undefined && params.select!==undefined){
		var selectedFields = params.select.constructor === Array ? params.select : [params.select];
		for(var i=0; i<selectedFields.length; i++){
			var property = this.orm.getProperty(selectedFields[i]);
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
	settings = settings || {};
	var limit = settings.$limit || settings.limit;
	var offset = settings.$offset || settings.offset;
	var sort = settings.$sort || settings.sort;
	var order = settings.$order || settings.order;
	var selectedFields = settings.$select || settings.select;

	var builder = this.dialect.select().from(this.orm.table);

	//add selected fields if any
	if(selectedFields){
		for(var i=0; i<selectedFields.length; i++){
			var property = this.orm.getProperty(selectedFields[i]);
			if(!property)
				throw Error('Unknown field name ['+ selectedFields[i] + '] in $select');
			builder.column(property.column);
		}
	}

    //add where clause for any fields
    var propertyDefinitions = this.orm.properties.filter(function(property){
    	for(var settingName in settings){
    		if(settingName === property.name)
    			return true;
    	}
    	return false;
    });
    if(propertyDefinitions.length>0){
		for(var i=0; i<propertyDefinitions.length; i++){
        	var def = propertyDefinitions[i];
	    	if(settings.$filter && settings.$filter.indexOf(def.name)>-1){
	    		builder.where(def.column + ' LIKE ?', [def]);
	   		} else {
				var val = settings[def.name];
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
        for(var i=0; i<sort.length; i++){
        	var _order = true;//ASC
        	//TODO: change to be able to order per sort property
	        if (order !== undefined) {
	            if(['asc','desc'].indexOf(String(order).toLowerCase())>-1){
	            	_order = order.toLowerCase() === 'desc' ? false : true;
	            }
	        }
	      	builder.order(this.orm.getProperty(sort[i]).column, _order);
    	}
    }
    if (limit !== undefined && offset !== undefined) {
        builder.limit(parseInt(limit,10)).offset(parseInt(offset,10));
    }
    return builder;
};

exports.ORMStatements = ORMStatements;

exports.create = function(orm, connection){
	var dialect;
	if(connection)
		dialect = require('db/v3/sql').getDialect(connection);
	var stmnts = new ORMStatements(orm, dialect);
	return stmnts;
};
