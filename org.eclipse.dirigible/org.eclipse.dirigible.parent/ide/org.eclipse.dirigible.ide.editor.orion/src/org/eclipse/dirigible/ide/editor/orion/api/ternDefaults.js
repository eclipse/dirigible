/*******************************************************************************
 * @license
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/* eslint-env amd*/
/* eslint-disable missing-nls */
define([
	'i18n!javascript/nls/messages',
	'json!tern/defs/ecma5.json',
	'json!tern/defs/ecma6.json',
	'json!tern/defs/browser.json',
	'json!tern/defs/chai.json',
	'json!tern/defs/dirigible.json',
	'json!tern/defs/tabris.json',
	
	//tern defaults
	"tern/plugin/angular",
	"tern/plugin/doc_comment",
	'tern/plugin/node',
	'tern/plugin/requirejs',
	
	//orion defaults
	"javascript/ternPlugins/amqp",
	"javascript/ternPlugins/eslint",
	"javascript/ternPlugins/express",
	"javascript/ternPlugins/html",
	"javascript/ternPlugins/jsdoc",
	"javascript/ternPlugins/mongodb",
	"javascript/ternPlugins/mysql",
	"javascript/ternPlugins/open_impl",
	"javascript/ternPlugins/outliner",
	"javascript/ternPlugins/plugins",
	"javascript/ternPlugins/postgres",
	"javascript/ternPlugins/redis",
	"javascript/ternPlugins/refs",
], function(Messages, ecma5, ecma6, browser, chai, dirigible, tabris) {
	
	var defs = [ecma5, ecma6, browser, chai, dirigible, tabris];
	var defNames = ["ecma5", "ecma6", "browser", "chai"]; //these are in the same order to avoid a walk of the array
	
	var plugins = {
		required: {
			"doc_comment": {
				"name": Messages["ternDocPluginName"],
				"description": Messages["ternDocPluginDescription"],
				"fullDocs": true,
				"version": "0.12.0"
			},
			"plugins": {
				"name": Messages["ternPluginsPluginName"],
				"description": Messages["ternPluginsPluginDescription"],
				"version": "1.0"
			},
			"open_impl": {
				"name": Messages["openImplPluginName"],
				"description": Messages["openImplPluginDescription"],
				"version": "1.0"
			},
			"html": {
				"name": Messages["htmlDepPluginName"],
				"description": Messages["htmlDepPluginDescription"],
				"version": "1.0"
			},
			"refs": {
				"name": Messages["findTypesName"],
				"description": Messages["findTypesDescription"],
				"version": "1.0"
			},
			"jsdoc": {
				"name": Messages["jsdocPluginName"],
				"description": Messages["jsdocPluginDescription"],
				"version": "1.0"
			},
			"eslint": {
				"name": Messages["eslintPluginName"],
				"description": Messages["eslintPluginDescription"],
				"version": "1.0"
			},
			"outliner": {
				"name": Messages["outlinerPluginName"],
				"description": Messages["outlinerPluginDescription"],
				"version": "1.0"
			}
		},
		optional: {
			"amqp": {
				"name": Messages["orionAMQPPluginName"],
				"description": Messages["orionAMQPPluginDescription"],
				"version": "0.9.1",
				"env": "amqp"
			},
			"angular": {
				"name": Messages["orionAngularPluginName"],
				"description": Messages["orionAngularPluginDescription"],
				"version": "0.12.0"
			},
			"express": {
				"name": Messages["orionExpressPluginName"],
				"description": Messages["orionExpressPluginDescription"],
				"version": "4.12.4",
				"env": "express"
			},
			"mongodb": {
				"name": Messages["orionMongoDBPluginName"],
				"description": Messages["orionMongoDBPluginDescription"],
				"version": "1.1.21",
				"env": "mongodb"
			},
			"mysql": {
				"name": Messages["orionMySQLPluginName"],
				"description": Messages["orionMySQLPluginDescription"],
				"version": "2.7.0",
				"env": "mysql"
			},
			"node": {
				"name": Messages["orionNodePluginName"],
				"description": Messages["orionNodePluginDescription"],
				"version": "0.12.0"
			},
			"postgres": {
				"name": Messages["orionPostgresPluginName"],
				"description": Messages["orionPostgresPluginDescription"],
				"version": "4.4.0",
				"env": "pg"
			},
			"redis": {
				"name": Messages["orionRedisPluginName"],
				"description": Messages["orionRedisPluginDescription"],
				"version": "0.12.1",
				"env": "redis"
			},
			"requirejs": {
				"name": Messages["orionRequirePluginName"],
				"description": Messages["orionRequirePluginDescription"],
				"version": "0.12.0"
			}
		}
	};
	
	return {
		plugins: plugins,
		defs: defs,
		defNames: defNames,
		pluginsDir: 'tern/plugin/'
	};
});