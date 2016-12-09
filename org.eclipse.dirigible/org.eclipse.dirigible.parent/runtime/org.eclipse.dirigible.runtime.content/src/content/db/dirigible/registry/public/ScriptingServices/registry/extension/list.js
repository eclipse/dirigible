/* eslint-env node, dirigible */

var repository = require('platform/repository');

const CONTROLLER_LOCATION = '/db/dirigible/registry/public/ScriptingServices/registry/extension/controller/DefaultListCtrl.js';

exports.getController = function() {
	return repository.getResource(CONTROLLER_LOCATION).getTextContent();
};