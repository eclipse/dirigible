/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = "/${packageName}/${fileNameNoExtension}_database_crud_hooks";

var extensionService = require('core/extensions');

exports.beforeCreate = function(connection, entity) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.beforeCreate === "function") {
		    extension.beforeCreate(connection, entity);
    	}
	}
};

exports.afterCreate = function(connection, entity) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.afterCreate === "function") {
	    	extension.afterCreate(connection, entity);
    	}
	}
};

exports.beforeUpdate = function(connection, entity) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.beforeUpdate === "function") {
	    	extension.beforeUpdate(connection, entity);
    	}
	}
};

exports.afterUpdate = function(connection, entity) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.afterUpdate === "function") {
	    	extension.afterUpdate(connection, entity);
    	}
	}
};

exports.beforeDelete = function(connection, id) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.beforeDelete === "function") {
	    	extension.beforeDelete(connection, id);
    	}
	}
};

exports.afterDelete = function(connection, id) {
	var extensions = getExtensions();
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    if (typeof extension.afterDelete === "function") {
	    	extension.afterDelete(connection, id);
    	}
	}
};

function getExtensions () {
	return extensionService.getExtensions(EXT_POINT_NAME);
}
