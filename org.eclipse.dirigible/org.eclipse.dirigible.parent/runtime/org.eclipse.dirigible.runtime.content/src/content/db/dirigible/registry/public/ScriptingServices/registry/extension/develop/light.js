/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/develop/LightIDECtrl.js';

exports.getType = function() {
	return 'Develop';
};

exports.getHomeItem = function() {
	return {
		image: "mobile",
		color: "lblue",
		path: "#/workspace",
		title: "Light IDE",
		description: "Lightweight Development"
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-mobile' ,
		'title': 'Light IDE' ,
		'content': 'For quick fixes with simple source code editing and publishing capabilities, there is a lightweight development environment convenient even from mobile devices.'	
	};
};

exports.getRoute = function() {
	return {
		'location': '/workspace',
		'controller': 'LightIDECtrl',
		'template': 'templates/develop/lightide.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 2;
};
