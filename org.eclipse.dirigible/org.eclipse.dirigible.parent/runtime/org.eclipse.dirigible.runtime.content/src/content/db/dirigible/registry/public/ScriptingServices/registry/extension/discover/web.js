/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/WebContentCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'globe',
		color: 'yellow',
		path: '#/web/content',
		title: 'Web',
		description: 'Browse User Interfaces'
	};
};
exports.getRoute = function() {
	return {
		'location': '/web/content',
		'controller': 'WebContentCtrl',
		'template': 'templates/discover/web.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 2;
};