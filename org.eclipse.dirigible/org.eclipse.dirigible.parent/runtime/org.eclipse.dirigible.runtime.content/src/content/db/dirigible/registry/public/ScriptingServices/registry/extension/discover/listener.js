/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/ListenerCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'assistive-listening-systems',
		color: 'orange',
		path: '#/integration/listener',
		title: 'Listeners',
		description: 'Integration Listeners'
	};
};

exports.getRoute = function() {
	return {
		'location': '/integration/listener',
		'controller': 'ListenerCtrl',
		'template': 'templates/discover/listener.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 11;
};
