/* globals $ */
/* eslint-env node, dirigible */

var extensionService = $.getExtensionService();

exports.getControllers = function() {
	var controllers = "";

	var extensions = extensionService.getExtensions("/registry/extension_points/registry_app_controller");
	for (var i=0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    controllers += extension.getController() + "\n";
	}
	return controllers;
};
