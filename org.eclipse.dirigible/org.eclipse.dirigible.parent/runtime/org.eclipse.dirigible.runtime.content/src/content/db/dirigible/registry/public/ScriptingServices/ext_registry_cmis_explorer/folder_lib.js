/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var response = require("net/http/response");

var cmisSession = cmis.getSession();

function ChildSerializer(cmisObject){
	this.id = cmisObject.getId();
	this.name = cmisObject.getName();
	this.type = cmisObject.getType();
}

function FolderSerializer(cmisFolder){
	this.name = cmisFolder.getName();
	this.id = cmisFolder.getId();
	this.path = cmisFolder.getPath();
	this.parentId = null;
	this.children = [];
	
	var parent = cmisFolder.getFolderParent();
	if (parent.getInternalObject() !== null){
		this.parentId = parent.getId();
	}

	var children = cmisFolder.getChildren();
	for (var i in children){
		var child = new ChildSerializer(children[i]);
		this.children.push(child);
	}
}

exports.getFolder = function(folderId){
	var folder;
	if (folderId === null){
		folder = cmisSession.getRootFolder();
	} else {
		folder = cmisSession.getObject(folderId);
	}
	
	return new FolderSerializer(folder);
};

exports.createFolder = function(parentFolderId, name){
	var parentFolder;
	if (parentFolderId == null){
		parentFolder = cmisSession.getRootFolder();
	} else{
		parentFolder = cmisSession.getObject(parentFolderId);
	}

	var properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_FOLDER;
	properties[cmis.NAME] = name;
	var newFolder = parentFolder.createFolder(properties);
	
	return new FolderSerializer(newFolder);
};
