/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var ORM = exports.ORM = function(orm){
	this.orm = orm;
	for(var i in orm){
		this[i] = orm[i];
	}
	this.statementsLib = require("daoism/statements").get();
	var self = this;
	this.statements = {
		insert: function(){
			var qb = self.statementsLib.builder().insert().into(self.dbName);
		 	for(var i=0; i<self.properties.length; i++){
		 		qb.set(self.properties[i]);
		 	}
	        return qb;
		},
		update: function(entity){
			if(!entity)
				throw Error('Illegal argument: entity[' + entity + ']');
			var updFieldDefs = self.properties.filter(function(property){
				return Object.keys(entity).indexOf(property.name)>-1 && (!property.allowedOps || property.allowedOps.indexOf('update')>-1);
			});
			var qb = self.statementsLib.builder().update().table(self.dbName);
			for(var i=0; i<updFieldDefs.length; i++){
				if(!updFieldDefs[i].id)
					qb.set(updFieldDefs[i]);
			}
			qb.where(self.getPrimaryKey().dbName+'=?', [self.getPrimaryKey()]);
			return qb;
		},
		"delete": function(){
			return self.statementsLib.builder().remove().from(self.dbName)
					.where(self.getPrimaryKey().dbName + "=?", [self.getPrimaryKey()]);
		},
		find: function(){
			return self.statementsLib.builder().select().from(self.dbName)
					.where(self.getPrimaryKey().dbName + "=?", [self.getPrimaryKey()]);
		},
		count: function(){
			return self.statementsLib.builder().select().from(self.dbName).field('COUNT(*)');
		},
		list: function(settings){
			settings = settings || {};
			var limit = settings.limit;
			var offset = settings.offset;
			var sort = settings.sort;	
			var order = settings.order;
			var qb = self.statementsLib.builder().select().from(self.dbName);
	        //add where clause for any relations 
	        var keyDefinitions = self.associationKeys().filter(function(joinKey){
	        	for(var settingName in settings){
	        		if(settingName === joinKey)
	        			return joinKey;
	        	}
	        	return;
	        }).filter(function(keyDef){
	        	return keyDef!==undefined;
	        }).map(function(key){
	        	var matchedDefinition = self.orm.properties.filter(function(property){
	        		return key === property.name;
	        	});
	        	return matchedDefinition?matchedDefinition[0]:undefined;
	        }).filter(function(keyDef){
	        	return keyDef!==undefined;
	        });
	        if(keyDefinitions.length>0){
	    	    for(var i=0; i<keyDefinitions; i++){
		        	var def = keyDefinitions[i];
		       		qb.where(def.dbName + '=?', [def]);
		        }
	        }

	        if (sort !== undefined) {
	            if(sort.constructor !== Array)
	            	sort = [sort];
	            for(var i in sort){
	            	var _order = true;//ASC
	            	//TODO: change to be able order per sort property
			        if (order !== undefined) {
			            if(['asc','desc'].indexOf((''+order).toLowerCase())>-1){
			            	_order = order.toLowerCase() === 'desc' ? false : true;
			            }
			        }
			      	qb.order(sort[i], _order);
            	}
	        }
	        if (limit !== undefined && offset !== undefined) {
	            qb.limit(limit).offset(offset);
	        }
	        return qb;
		}
	};
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
