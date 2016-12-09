/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/DevelopCtrl.js';

exports.getType = function() {
	return 'Home';
};

exports.getMenuItem = function() {
	return {
		name: 'Develop',
		link: '#/develop'
	};
};

exports.getHomeItem = function(){
	return {
		image: 'edit',
		color: 'blue',
		path: '#/develop',
		title: 'Develop',
		description: 'Development Toolkits'
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-edit',
		'title': 'Develop',
		'content': 'Eclipse Dirigible provides three major types of toolkits covering the Development phase of your solution:',
		'listItems': [{
			'url': '../../index.html',
			'title': 'WebIDE',
			'description': ' - fully functional yet powerful, browser based IDE'
		}, {
			'url': '#/workspace',
			'title': 'LightIDE',
			'description': ' - limited code-editing only, browser based IDE'
		}, {
			'description': 'DesktopIDE - Eclipse based desktop IDE'
		}]
	};

};
exports.getRoute = function() {
	return {
		'location': '/develop',
		'controller': 'DevelopCtrl',
		'template': 'templates/develop.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 1;
};
