/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/WebWikiCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'book',
		color: 'yellow',
		path: '#/web/wiki',
		title: 'Wiki',
		description: 'Browse Documentation'
	};
};

exports.getRoute = function() {
	return {
		'location': '/web/wiki',
		'controller': 'WebWikiCtrl',
		'template': 'templates/discover/wiki.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 3;
};