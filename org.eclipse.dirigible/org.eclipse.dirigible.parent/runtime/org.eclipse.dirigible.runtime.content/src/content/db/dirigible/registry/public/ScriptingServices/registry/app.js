/* globals $ */
/* eslint-env node, dirigible */

var repository = require('platform/repository');
var generator = require('platform/generator');
var registryExtensions = require('registry/extension/registryExtensionUtils');

const APP_TEMPLATE_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/template/appTemplate.js';

processRequest();

function processRequest() {
	var app = getApp();
	sendResponse(app, 'text/javascript');
}

function getApp() {
	var parameters = {
		'routes': registryExtensions.getRoutes(),
		'controllers': registryExtensions.getControllers()
	};
	var app = repository.getResource(APP_TEMPLATE_LOCATION).getTextContent();
	return generator.generate(app, parameters);
}

function sendResponse(content, contentType) {
	var response = require('net/http/response');

	response.setContentType(contentType);
	response.print(content);
	response.flush();
	response.close();	
}
