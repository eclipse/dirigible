/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var folderLib = require("docs_explorer/lib/folder_lib");

var cmisSession = cmis.getSession();

exports.getObject = function(path){
	return cmisSession.getObjectByPath(path);
};

exports.getById = function(id) {
	return cmisSession.getObject(id);
};

exports.deleteObject = function(object){
	object.delete();
};

exports.renameObject = function(object, newName){
	object.getInternalObject().rename(newName);
};
