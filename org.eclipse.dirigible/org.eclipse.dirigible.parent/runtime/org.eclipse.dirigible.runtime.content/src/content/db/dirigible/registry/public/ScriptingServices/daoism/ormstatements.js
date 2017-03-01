/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var Statements = require("daoism/statements").Statements;
var ORMStatements = function(orm, dialect){
	Statements.call(this);
	this.$log = require('log/loggers').get('daoism/ormstatements');
	this.orm = orm;
	this.dialect = dialect;
};

ORMStatements.prototype = Object.create(Statements.prototype);
ORMStatements.prototype.constructor = ORMStatements;

ORMStatements.prototype.createTable = function(){
	var stmnt = this.builder(this.dialect);
	stmnt.createTable(this.orm.dbName);
	for(var i=0; i<this.orm.properties.length;i++){
		var fieldDef = this.orm.properties[i];
		fieldDef["pk"] = fieldDef.id;
		stmnt.fieldDef(fieldDef);
	}
	return stmnt;
};

ORMStatements.prototype.dropTable = function(){
	return this.builder(this.dialect).dropTable(this.orm.dbName);
};

ORMStatements.prototype.insert = function(){
	var stmnt = this.builder(this.dialect).insert().into(this.orm.dbName);
 	for(var i=0; i<this.orm.properties.length; i++){
 		stmnt.set(this.orm.properties[i]);
 	}
    return stmnt;
};

ORMStatements.prototype.update = function(entity){
	if(!entity)
		throw Error('Illegal argument: entity[' + entity + ']');
	var updFieldDefs = this.orm.properties.filter(function(property){
		return Object.keys(entity).indexOf(property.name)>-1 && (!property.allowedOps || property.allowedOps.indexOf('update')>-1);
	});
	var stmnt = this.builder(this.dialect).update().table(this.orm.dbName);
	for(var i=0; i<updFieldDefs.length; i++){
		if(!updFieldDefs[i].id)
			stmnt.set(updFieldDefs[i]);
	}
	stmnt.where(this.orm.getPrimaryKey().dbName+'=?', [this.orm.getPrimaryKey()]);
	return stmnt;
};
ORMStatements.prototype["delete"] = ORMStatements.prototype.remove = function(){
	var stmnt = this.builder(this.dialect).remove().from(this.orm.dbName);
	if(arguments[0]!==undefined){
		var filterFieldNames = arguments[0];
		if(filterFieldNames.constructor!==Array)
			filterFieldNames= [filterFieldNames];
		for(var i=0; i<filterFieldNames.length; i++){
			var property = this.orm.getProperty(filterFieldNames[i]);
			if(!property)
				throw Error('Unknown property name: ' + filterFieldNames[i]);
			stmnt.where(property.dbName + "=?", [property]);
		}
	}
	return stmnt;
};
ORMStatements.prototype.find = function(params){
	var stmnt = this.builder(this.dialect).select();
	if(params!==undefined && params.select!==undefined){
		var selectedFields = params.select.constructor === Array ? params.select : [params.select];
		for(var i=0; i<selectedFields.length; i++){
			var property = this.orm.getProperty(selectedFields[i]);
			if(!property)
				throw Error('Unknown field name ['+ selectedFields[i] + ']')
			stmnt = stmnt.field(property.dbName);
		}
	}
	stmnt = stmnt.from(this.orm.dbName)
		.where(this.orm.getPrimaryKey().dbName + "=?", [this.orm.getPrimaryKey()]);
	return stmnt;
};
ORMStatements.prototype.count = function(){
	return this.builder(this.dialect).select().from(this.orm.dbName).field('COUNT(*)');
};
ORMStatements.prototype.list= function(settings){
	settings = settings || {};
	var limit = settings.$limit || settings.limit;
	var offset = settings.$offset || settings.offset;
	var sort = settings.$sort || settings.sort;
	var order = settings.$order || settings.order;
	var selectedFields = settings.$select || settings.select;
	
	var stmnt = this.builder(this.dialect).select().from(this.orm.dbName);

	//add selected fields if any
	if(selectedFields){
		for(var i=0; i<selectedFields.length; i++){
			var property = this.orm.getProperty(selectedFields[i]);
			if(!property)
				throw Error('Unknown field name ['+ selectedFields[i] + ']')
			stmnt = stmnt.field(property.dbName);
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
	    	if(settings.$filter && settings.$filter.indexOf(def.name)>-1)
	    		stmnt.where(def.dbName + ' LIKE ?', [def]);
	   		else
	   			stmnt.where(def.dbName + '=?', [def]);
        }
    }

    if (sort !== undefined) {
        for(var i=0; i<sort.length; i++){
        	var _order = true;//ASC
        	//TODO: change to be able order per sort property
	        if (order !== undefined) {
	            if(['asc','desc'].indexOf((''+order).toLowerCase())>-1){
	            	_order = order.toLowerCase() === 'desc' ? false : true;
	            }
	        }
	      	stmnt.order(this.orm.getProperty(sort[i]).dbName, _order);
    	}
    }
    if (limit !== undefined && offset !== undefined) {
        stmnt.limit(limit).offset(offset);
    }
    return stmnt;
};

exports.forDatasource = function(orm, ds){
	var conn, databaseName;
	try{
		conn = ds.getConnection();
		databaseName = conn.internalConnection.getMetaData().getDatabaseProductName();
	} finally {
		conn.close();
	}
	var dialect = require("daoism/dialects/dialects").get().getDialect(databaseName);
	var stmnts = new ORMStatements(orm, dialect);
	return stmnts;
};
