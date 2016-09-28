/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = "/registry/extension_points/routes";

const TEMPLATE_START_LOCATION = "/db/dirigible/registry/public/ScriptingServices/registry/templates/routes_start.js";
const TEMPLATE_END_LOCATION = "/db/dirigible/registry/public/ScriptingServices/registry/templates/routes_end.js";

var fileUtils = require("registry/utils/fileUtils");
var extensionService = $.getExtensionService();

exports.getRoutes = function() {
	var controller = fileUtils.getContent(TEMPLATE_START_LOCATION);
	controller += getContent();
	controller += fileUtils.getContent(TEMPLATE_END_LOCATION);
	return controller;
};

function getContent() {
	var extensions = extensionService.getExtensions(EXT_POINT_NAME);
	var content = "";
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    content += createRoutes(extension);
	}
	return content;
}

function createRoutes(extension) {
	var content = "";
	var routes = extension.getRoutes();
	for(var i = 0; i < routes.length; i ++) {
    	content += ".when('" + routes[i].location + "', {";
    	if(routes[i].controller) {
    		content += "controller: '" + routes[i].controller + "', ";
		}
    	content += "templateUrl: '" + routes[i].templateUrl + "'})\n";
	}
	return content;
}