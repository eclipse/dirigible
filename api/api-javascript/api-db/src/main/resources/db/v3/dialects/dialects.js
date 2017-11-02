/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

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
	for(var databaseName in config){
		if(dialectResourcePath === config[databaseName])
			delete config[databaseName];
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
	for(var databaseNamePattern in config){
		if(dbProductName.indexOf(databaseNamePattern)>-1){
			var resourcePath = config[databaseNamePattern];
			var dialectResource = require(resourcePath);
			if(!dialectResource.get){
				throw Error('Missing factory function "get" in dialect resource '+ resourcePath);
			}
			return dialectResource.get(databaseNamePattern);
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
