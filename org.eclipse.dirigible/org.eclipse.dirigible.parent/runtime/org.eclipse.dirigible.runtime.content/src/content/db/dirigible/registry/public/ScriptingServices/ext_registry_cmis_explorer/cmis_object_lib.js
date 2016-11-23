/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var cmisSession = cmis.getSession();

exports.deleteObject = function(objectId){
	var object = cmisSession.getObject(objectId);
	object.delete();
};

exports.renameObject = function(objectId, newName){
	var object = cmisSession.getObject(objectId);
	object.getInternalObject().rename(newName);
};