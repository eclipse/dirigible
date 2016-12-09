/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/HomeCtrl.js';

exports.getRoute = function() {
	return {
		'location': '/home',
		'controller': 'HomeCtrl',
		'template': 'templates/home.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};
