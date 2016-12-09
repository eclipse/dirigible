/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/JobCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'tasks',
		color: 'orange',
		path: '#/integration/job',
		title: 'Jobs',
		description: 'Integration Jobs'
	};
};

exports.getRoute = function() {
	return {
		'location': '/integration/job',
		'controller': 'JobCtrl',
		'template': 'templates/discover/job.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 10;
};
