/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var Dialects = exports.Dialects = function(configKey){
	this.configKey = configKey || "dialectResourcesRegistry";
};

Dialects.prototype.register = function(databaseNamePattern, dialectResourcePath){
	var env = require('core/globals');
	var config = env.get(this.configKey);
	if(!config)
		config = {};
	config[databaseNamePattern] = dialectResourcePath;
	env.set(this.configKey, config);
};

Dialects.prototype.unregister = function(databaseNamePattern){
	var env = require('core/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	delete config[databaseNamePattern];
	env.set(this.configKey, config);
};

Dialects.prototype.unregisterDialectResource = function(dialectResourcePath){
	var env = require('core/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	for(var dbName in config){
		if(dialectResourcePath === config[dbName])
			delete config[dbName];
	}
	env.set(this.configKey, config);
};

Dialects.prototype.getDialect = function(dbProductName){
	var env = require('core/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	for(var dbNamePattern in config){
		if(dbProductName.indexOf(dbNamePattern)>-1){
			var resourcePath = config[dbNamePattern];
			var dialectResource = require(resourcePath);
			if(!dialectResource.get){
				throw Error('Missing factory function "get" in dialect resource '+ resourcePath);
			}
			return dialectResource.get(dbNamePattern);
		}
	}
	return;
};


exports.get = function(configKey){
	var dialects = new Dialects(configKey);
	//TODO: move this to a one-time config phase
	dialects.register("Default", "daoism/dialects/defaults");
	dialects.register("Derby", "daoism/dialects/derby");
	return dialects;
};
