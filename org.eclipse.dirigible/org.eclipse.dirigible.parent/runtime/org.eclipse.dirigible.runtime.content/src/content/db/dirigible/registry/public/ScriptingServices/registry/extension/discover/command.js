/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/CommandCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'gear',
		color: 'lblue',
		path: '#/scripting/command',
		title: 'Command',
		description: 'Command Services'
	};
};

exports.getRoute = function() {
	return {
		'location': '/scripting/command',
		'controller': 'CommandCtrl',
		'template': 'templates/discover/command.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 7;
};