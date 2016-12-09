/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/operate/CloneCtrl.js';

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "toggle-on",
		color: "green",
		path: "#/content/clone",
		title: "Clone",
		description: "Clone Instance"
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-toggle-on",
		"title": " Clone Instances",
		"content": "Clone the whole Repository content including the Users Workspaces and Configurations. Clone Import service provide the end-point for importing cloned content from another instance. Clone Export service helps in exporting the content of the whole repository as a zip file ready for further import."
	};
};

exports.getRoute = function() {
	return {
		'location': '/content/clone',
		'controller': 'CloneCtrl',
		'template': 'templates/operate/clone.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 2;
};
