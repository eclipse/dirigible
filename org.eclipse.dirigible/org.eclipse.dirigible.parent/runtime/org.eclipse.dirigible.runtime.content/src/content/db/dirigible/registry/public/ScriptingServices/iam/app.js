/* globals $ */
/* eslint-env node, dirigible */

var repository = require('platform/repository');
var launchpadExtensions = require('iam/extension/launchpadExtensionUtils');
var response = require('net/http/response');

const APP_TEMPLATE_LOCATION = '/db/dirigible/registry/public/ScriptingServices/iam/templates/appTemplate.js';

processRequest();

function processRequest() {
	sendResponse(getApp(), 'text/javascript');
}

function getApp() {
	var routesPlaceholder = "+++_ROUTES_PLACEHOLDER_+++";
	var template = repository.getResource(APP_TEMPLATE_LOCATION).getTextContent();
	return template.replace(routesPlaceholder, getRoutes());
}

function getRoutes() {
	var routes = [];
	var uniqueRoutes = {};
	var menuItems = launchpadExtensions.getMenuItems();
	for (var i = 0 ; i < menuItems.length; i++) {
		uniqueRoutes[menuItems[i].path] = menuItems[i].link;
	}
	var homeItems = launchpadExtensions.getHomeItems();
	for (i = 0 ; i < homeItems.length; i++) {
		uniqueRoutes[homeItems[i].path] = homeItems[i].link;
	}
	for (var route in uniqueRoutes) {
		routes.push(createRoute(route));
	}
	return routes.join("");
}

function createRoute(location) {
	return '.when(\'' + location + '\', {'
		+ 'templateUrl: \'templates/view.html\''
		+ '})';
}

function sendResponse(content, contentType) {
	response.setContentType(contentType);
	response.print(content);
	response.flush();
	response.close();	
}