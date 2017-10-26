/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var Dialects = exports.Dialects = function(configKey){
	this.configKey = configKey || "dialectResourcesRegistry";
};

Dialects.prototype.register = function(databaseNamePattern, dialectResourcePath){
	var env = require('core/v3/globals');
	var config = env.get(this.configKey);
	if(!config)
		config = {};
	else
		config = JSON.parse(config);
	config[databaseNamePattern] = dialectResourcePath;
	env.set(this.configKey, JSON.stringify(config));
};

Dialects.prototype.unregister = function(databaseNamePattern){
	var env = require('core/v3/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	else
		config = JSON.parse(config);
	delete config[databaseNamePattern];
	env.set(this.configKey, JSON.stringify(config));
};

Dialects.prototype.unregisterDialectResource = function(dialectResourcePath){
	var env = require('core/v3/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	else
		config = JSON.parse(config);
	for(var dbName in config){
		if(dialectResourcePath === config[dbName])
			delete config[dbName];
	}
	env.set(this.configKey, JSON.stringify(config));
};

Dialects.prototype.getDialect = function(dbProductName){
	var env = require('core/v3/globals');
	var config = env.get(this.configKey);
	if(!config)
		return;
	else
		config = JSON.parse(config);
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
	dialects.register("Default", "db/v3/dialects/defaults");
	dialects.register("Derby", "db/v3/dialects/derby");
	return dialects;
};
