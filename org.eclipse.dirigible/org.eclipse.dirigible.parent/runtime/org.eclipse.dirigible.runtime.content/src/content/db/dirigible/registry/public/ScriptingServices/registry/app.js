/* globals $ */
/* eslint-env node, dirigible */

var fileUtils = require("registry/utils/fileUtils");

processRequest();

function processRequest() {
	var app = getApp();
	sendResponse(app, "text/javascript");
}

function getApp() {
	var app = fileUtils.getContent("/db/dirigible/registry/public/ScriptingServices/registry/templates/app.js");
	app += getConfig();
	app += getControllers();
	return app;
}

function getConfig() {
	var routesExtensions = require("registry/extensions/routesExtensions.js");

	var config = fileUtils.getContent("/db/dirigible/registry/public/ScriptingServices/registry/templates/app_config_start.js");
	config += routesExtensions.getRoutes();
	config += fileUtils.getContent("/db/dirigible/registry/public/ScriptingServices/registry/templates/app_config_end.js");
	return config;
}

function getControllers() {
	var homeItemExtensions = require("registry/extensions/homeItemExtensions.js");
	var controllerExtensions = require("registry/extensions/controllerExtensions.js");
	var menuItemExtensions = require("registry/extensions/menuItemExtensions.js");

	var controllers = controllerExtensions.getControllers();
	controllers += menuItemExtensions.getControllers();
	controllers += homeItemExtensions.getControllers();
	return controllers;
}

function sendResponse(content, contentType) {
	var response = require("net/http/response");

	response.setContentType(contentType);
	response.print(content);
	response.flush();
	response.close();	
}