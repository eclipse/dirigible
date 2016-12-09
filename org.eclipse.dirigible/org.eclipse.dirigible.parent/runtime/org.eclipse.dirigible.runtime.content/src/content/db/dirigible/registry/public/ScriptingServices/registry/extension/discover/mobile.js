/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/discover/MobileCtrl.js';

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'mobile-phone',
		color: 'lila',
		path: '#/mobile',
		title: 'Mobile',
		description: 'Native Mobile Apps'
	};
};

exports.getRoute = function() {
	return {
		'location': '/mobile',
		'controller': 'MobileCtrl',
		'template': 'templates/discover/mobile.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 4;
};
