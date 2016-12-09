/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/operate/TransportCtrl.js';

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "truck",
		color: "blue",
		path: "#/content/import",
		title: "Transport",
		description: "Transport Content"
	};
};

exports.getRoute = function() {
	return {
		'location': '/content/import',
		'controller': 'TransportCtrl',
		'template': 'templates/operate/transport.html'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-truck",
		"title": "Transport Content",
		"content": "Transport the public artifacts from the Registry. Import Content service provide the end-point for importing public registry content from another instance. Export Content service helps in exporting the content of the public registry as a zip file ready for further import."
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 1;
};
