/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/monitoring/ConfigureCtrl.js';

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'wrench',
		color: 'blue',
		path: '#/monitoring/manage',
		title: 'Configure',
		description: 'Configure locations'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-wrench",
		"title": "Manage Location",
		"content": "Register the locations to which the statistics shall be collected."
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/manage',
		'controller': 'ConfigureCtrl',
		'template': 'templates/monitoring/configure.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 1;
};
