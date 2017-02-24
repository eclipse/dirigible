/* globals $ */
/* eslint-env node, dirigible */

var repository = require('platform/repository');
var cockpitExtensions = require('${packageName}/extension/cockpitExtensionUtils');
var response = require('net/http/response');

const APP_TEMPLATE_LOCATION = '/db/dirigible/registry/public/ScriptingServices/${packageName}/templates/appTemplate.js';

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
	var menuItems = cockpitExtensions.getMenuItems();
	for (var i = 0 ; i < menuItems.length; i++) {
		uniqueRoutes[menuItems[i].path] = menuItems[i].link;
	}
	var sidebarItems = cockpitExtensions.getSidebarItems();
	for (i = 0 ; i < sidebarItems.length; i++) {
		uniqueRoutes[sidebarItems[i].path] = sidebarItems[i].link;
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
