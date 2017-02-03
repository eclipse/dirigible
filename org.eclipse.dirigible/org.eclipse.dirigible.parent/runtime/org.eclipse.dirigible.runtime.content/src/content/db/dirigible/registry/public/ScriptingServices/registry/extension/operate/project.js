/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/operate/ProjectCtrl.js';

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "sign-in",
		color: "lila",
		path: "#/content/project",
		title: "Import",
		description: "Import Project"
	};
};

//exports.getDescription = function() {
//	return {
//		"icon": "fa-sign-in",
//		"title": "Import Projects",
//		"content": "Import Project service provide the end-point for importing project content in design time format (source). This is useful for constructing a PROD instance (consisting only of Runtime components) by importing one or many ready to use source projects."
//	};
//};

exports.getRoute = function() {
	return {
		'location': '/content/project',
		'controller': 'ProjectCtrl',
		'template': 'templates/operate/project.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 6;
};
