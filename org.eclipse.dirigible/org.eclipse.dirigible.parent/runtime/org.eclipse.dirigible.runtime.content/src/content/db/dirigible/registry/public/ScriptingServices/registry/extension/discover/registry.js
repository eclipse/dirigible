/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/RegistryContentCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'search',
		color: 'blue',
		path: '#/content',
		title: 'Registry',
		description: 'Browse Registry Content'
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-search',
		'title': 'Browse Content',
		'content': 'Browse the raw content of the Registry containing all the published artifacts. Inspect the source of the HTML or Wiki pages as well as the code for the scripting services in JavaScript, Java, SQL and Shell Commands'
	};
};

exports.getRoute = function() {
	return {
		'location': '/content',
		'controller': 'RegistryContentCtrl',
		'template': 'templates/discover/registry.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};


exports.getOrder = function() {
	return 1;
};
