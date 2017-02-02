/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var cmisObjectLib = require("docs_explorer/lib/object_lib");

var cmisSession = cmis.getSession();

function ChildSerializer(cmisObject){
	this.name = cmisObject.getName();
	this.type = cmisObject.getType();
	this.id = cmisObject.getId();
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

exports.readFolder = function(folder){
	return new FolderSerializer(folder);
};

exports.createFolder = function(parentFolder, name){
	var properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_FOLDER;
	properties[cmis.NAME] = name;
	var newFolder = parentFolder.createFolder(properties);
	
	return new FolderSerializer(newFolder);
};

exports.getFolderOrRoot = function(folderPath){
	var folder = null;
	try {
		folder = exports.getFolder(folderPath);
	} catch(e){
		folder = cmisSession.getRootFolder();
	}
	return folder;
};

exports.getFolder = function(path){
	return cmisObjectLib.getObject(path);
};

exports.deleteTree = function(folder){
	folder.deleteTree();
};
