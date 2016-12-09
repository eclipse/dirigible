/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = '/registry/extension_point/launchpad';

var extensions = require('core/extensions');

exports.getMenu = function() {
	var menu = [];
	var registryExtensions = getRegistryExtensions();
	for (var i = 0; i < registryExtensions.length; i ++) {
		if (isFunction(registryExtensions[i].getMenuItem)) {
			menu.push(registryExtensions[i].getMenuItem());
		}
	}
	return menu;
};

exports.getHomeData = function(type) {
	var homeData = [];
	var registryExtensions = getRegistryExtensions(type);
	for (var i = 0; i < registryExtensions.length; i ++) {
		if (isFunction(registryExtensions[i].getHomeItem)) {
			homeData.push(registryExtensions[i].getHomeItem());
		}
	}
	return homeData;
};

exports.getRoutes = function() {
	var routes = '';
	var registryExtensions = getRegistryExtensions();
	for (var i = 0; i < registryExtensions.length; i ++) {
		if (isFunction(registryExtensions[i].getRoute)) {
			routes += createRoute(registryExtensions[i].getRoute()) + '\n';
		}
	}
	return routes;
};

exports.getControllers = function() {
	var controllers = '';
	var registryExtensions = getRegistryExtensions();
	for (var i = 0; i < registryExtensions.length; i ++) {
		if (isFunction(registryExtensions[i].getController)) {
			controllers += registryExtensions[i].getController() + '\n';
		}
	}
	return controllers;
};

exports.getDescriptions = function(type) {
	var descriptions = [];
	var registryExtensions = getRegistryExtensions(type);
	console.error('Description type is: ' + type);
	
	for (var i = 0; i < registryExtensions.length; i ++) {
		if (isFunction(registryExtensions[i].getDescription)) {
			descriptions.push(registryExtensions[i].getDescription());
		}
	}
	return descriptions;
};

function getRegistryExtensions(type) {
	var registryExtensions = [];
	var extensionNames = extensions.getExtensions(EXT_POINT_NAME);
	for (var i = 0; i < extensionNames.length; i ++) {
		var extension = extensions.getExtension(extensionNames[i], EXT_POINT_NAME);
		var extensionModule = require(extension.getLocation());
		if (type) {
			if (isFunction(extensionModule.getType) && type === extensionModule.getType()) {
				registryExtensions.push({
					'order': isFunction(extensionModule.getOrder) ? extensionModule.getOrder() : Number.MAX_VALUE,
					'module': extensionModule
				});
			}
		} else {
			registryExtensions.push({
					'order': isFunction(extensionModule.getOrder) ? extensionModule.getOrder() : Number.MAX_VALUE,
					'module': extensionModule
				});
		}
	}

	sort(registryExtensions);

	var extensionModules = [];
	for (i = 0; i < registryExtensions.length; i ++) {
		extensionModules.push(registryExtensions[i].module);
	}
	console.log(extensionModules);
	return extensionModules;
}

function isFunction(f) {
	return typeof f === 'function';
}

function createRoute(route) {
	var angularJsRoute = '.when(\'' + route.location + '\', {';
		if (route.controller) {
			angularJsRoute += 'controller: \'' + route.controller + '\', ';
		}
		angularJsRoute += 'templateUrl: \'' + route.template + '\'';
		angularJsRoute += '})';
	return angularJsRoute;
}

function sort(data) {
	data.sort(function(a, b) {
		return a.order - b.order;
	});
}
