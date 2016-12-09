/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/operate/CmisCtrl.js';

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "book",
		color: "red",
		path: "#/content/cmis",
		title: "Documents",
		description: "Manage Documents"
	};
};

exports.getRoute = function() {
	return {
		'location': '/content/cmis',
		'controller': 'CmisCtrl',
		'template': 'templates/operate/cmis.html'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-book",
		"title": "Manage Documents",
		"content": "Browse a document repository, upload and download documents, create folders, etc."
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 4;
};