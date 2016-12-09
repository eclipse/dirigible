/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/JavaScriptCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'file-code-o',
		color: 'lblue',
		path: '#/scripting/javascript',
		title: 'JavaScript',
		description: 'JavaScript Services'
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-server',
		'title': 'Find Endpoints',
		'content': 'Navigate throughout all the registered service endpoints and perform test calls. Web content ususally is served as is, while Wiki pages first go thru transformation. The services are executed by the corresponding scripting engine for the given language. The same applies for the Flows and Jobs definitions.'
	};
};

exports.getRoute = function() {
	return {
		'location': '/scripting/javascript',
		'controller': 'JavaScriptCtrl',
		'template': 'templates/discover/javascript.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 5;
};
