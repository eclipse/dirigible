/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var ORM = exports.ORM = function(orm){
	this.orm = orm;
	for(var i in orm){
		this[i] = orm[i];
	}
};

ORM.prototype.getPrimaryKey = function(){
	if(!this.idProperty){
		if(!this.properties || !this.properties.length)
			throw Error('Invalid orm configuration - no properties are defined');
		var id = this.properties.filter(function(property){
			return property.id;
		});
		if(!id.length)
			throw Error('Invalid orm configuration - no id property is defined');
		this.idProperty = id[0];
	}
	return this.idProperty;
};

ORM.prototype.getProperty = function(name){
	if(name === undefined)
		throw Error('Illegal argument: name['+name+']');
	if(!this.properties || !this.properties.length)
		throw Error('Invalid orm configuration - no properties are defined');
	var property = this.properties.filter(function(property){
		return property.name === name;
	});
	return property.length>0?property[0]:undefined;
};

ORM.prototype.getMandatoryProperties = function(){
	if(!this.mandatoryProperties){
		if(!this.properties || !this.properties.length)
			throw Error('Invalid orm configuration - no properties are defined');
		var mandatories = this.properties.filter(function(property){
			return property.required;
		});
		this.mandatoryProperties = mandatories;
	}
	return this.mandatoryProperties;
};

ORM.prototype.getOptionalProperties = function(){
	if(!this.optionalProperties){
		if(!this.properties || !this.properties.length)
			throw Error('Invalid orm configuration - no properties are defined');
		var mandatories = this.properties.filter(function(property){
			return !property.required;
		});
		this.optionalProperties = mandatories;
	}
	return this.optionalProperties;
};

ORM.prototype.associationKeys = function(){
	var keys = [];
	if(this.associationSets && Object.keys(this.associationSets).length>0){
		for(var associationName in this.associationSets){
			keys.push(this.associationSets[associationName].joinKey);
		}
	}
	return keys;
};

ORM.prototype.getAssociation = function(associationName){
	if(this.associationSets){
		return this.associationSets[associationName];
	}
	return;
};

exports.get = function(orm){
	return new ORM(orm);
};
