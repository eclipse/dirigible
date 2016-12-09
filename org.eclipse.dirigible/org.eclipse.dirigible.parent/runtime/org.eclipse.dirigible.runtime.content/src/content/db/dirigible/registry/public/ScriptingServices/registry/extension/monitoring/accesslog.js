/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/monitoring/AccessLogCtrl.js';

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'ticket',
		color: 'lblue',
		path: '#/monitoring/acclog',
		title: 'Access',
		description: 'Access Log'
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/acclog',
		'controller': 'AccessLogCtrl',
		'template': 'templates/monitoring/accesslog.html'
	};
};

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};

exports.getOrder = function() {
	return 5;
};