/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/FlowCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'caret-square-o-right',
		color: 'orange',
		path: '#/integration/flow',
		title: 'Flows',
		description: 'Integration Flows'
	};
};

exports.getRoute = function() {
	return {
		'location': '/integration/flow',
		'controller': 'FlowCtrl',
		'template': 'templates/discover/flow.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 9;
};