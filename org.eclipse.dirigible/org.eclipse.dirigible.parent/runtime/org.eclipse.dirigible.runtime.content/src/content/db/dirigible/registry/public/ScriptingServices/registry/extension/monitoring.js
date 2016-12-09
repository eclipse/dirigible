/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/MonitoringCtrl.js';

exports.getType = function() {
	return 'Home';
};

exports.getMenuItem = function() {
	return {
		name: 'Monitor',
		link: '#/monitoring'
	};
};

exports.getHomeItem = function(){
	return {
		image: 'area-chart',
		color: 'red',
		path: '#/monitoring',
		title: 'Monitor',
		description: 'Basic Metrics'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-area-chart",
		"title": "Monitor",
		"content": "Monitor the basic metrics of a live Eclipse Dirigible instance as well as inspect the applications and audit logs."
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring',
		'controller': 'MonitoringCtrl',
		'template': 'templates/monitoring.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 4;
};
