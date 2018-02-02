/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('cms/v3/cmis');
var cmisSession = cmis.getSession();

exports.getObject = function(path){
	try {
		if (path === null || path === undefined) {
			return null;
		}		
		return cmisSession.getObjectByPath(path);
	} catch(e) {
		console.error('Error [%s] in getting an object by path [%s]', e.message, path);
	}
	return null;
};

exports.getById = function(id) {
	return cmisSession.getObject(id);
};

exports.deleteObject = function(object){
	object.delete();
};

exports.renameObject = function(object, newName){
	object.rename(newName);
};
