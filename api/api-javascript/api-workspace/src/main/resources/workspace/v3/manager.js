/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.createWorkspace = function(name) {
	var workspaceInstance = java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'createWorkspace', [name], true);
	var workspace = new Workspace();
	workspace.uuid = workspaceInstance.uuid;
	return workspace;
};

exports.getWorkspace = function(name) {
	var workspaceInstance = java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'getWorkspace', [name], true);
	var workspace = new Workspace();
	workspace.uuid = workspaceInstance.uuid;
	return workspace;
};

exports.getWorkspacesNames = function() {
	var workspacesNames = java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'getWorkspacesNames', []);
	if (workspacesNames) {
		return JSON.parse(workspacesNames);
	}
	return workspacesNames;
};

exports.deleteWorkspace = function(name) {
	java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'deleteWorkspace', [name], true);
};

/**
 * Workspace object
 */
function Workspace() {

	this.getProjects = function() {
		var projectsInstance = java.invoke(this.uuid, 'getProjects', [], true);
		var projects = new Projects();
		projects.uuid = projectsInstance.uuid;
		return projects;
	};

	this.createProject = function(name) {
		var projectInstance = java.invoke(this.uuid, 'createProject', [name], true);
		var project = new Project();
		project.uuid = projectInstance.uuid;
		return project;
	};

	this.getProject = function(name) {
		var projectInstance = java.invoke(this.uuid, 'getProject', [name], true);
		var project = new Project();
		project.uuid = projectInstance.uuid;
		return project;
	};

	this.deleteProject = function(name) {
		java.invoke(this.uuid, 'deleteProject', [name]);
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};

	this.existsFolder = function(path) {
		return java.invoke(this.uuid, 'existsFolder', [path]);
	};

	this.existsFile = function(path) {
		return java.invoke(this.uuid, 'existsFile', [path]);
	};

	this.copyProject = function(source, target) {
		java.invoke(this.uuid, 'copyProject', [source, target]);
	};

	this.moveProject = function(source, target) {
		java.invoke(this.uuid, 'moveProject', [source, target]);
	};

}

/**
 * Projects object
 */
function Projects() {
	
	this.size = function() {
		var size = java.invoke(this.uuid, 'size', []);
		return size;
	};
	
	this.get = function(index) {
		var projectInstance = java.invoke(this.uuid, 'get', [index], true);
		var project = new Project();
		project.uuid = projectInstance.uuid;
		return project;
	};
	
}

/**
 * Project object
 */
function Project() {

	this.getName = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getName', []);
		return name;
	};
	
	this.getPath = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getPath', []);
		return name;
	};
	
	this.createFolder = function(path) {
		var folderInstance = java.invoke(this.uuid, 'createFolder', [path], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};

	this.existsFolder = function(path) {
		return java.invoke(this.uuid, 'existsFolder', [path]);
	};
	
	this.getFolder = function(path) {
		var folderInstance = java.invoke(this.uuid, 'getFolder', [path], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};
	
	this.getFolders = function(path) {
		var foldersInstance = java.invoke(this.uuid, 'getFolders', [path], true);
		var folders = new Folders();
		folders.uuid = foldersInstance.uuid;
		return folders;
	};
	
	this.deleteFolder = function(path) {
		return java.invoke(this.uuid, 'deleteFolder', [path]);
	};
	
	this.createFile = function(path) {
		var fileInstance = java.invoke(this.uuid, 'createFile', [path], true);
		var file = new File();
		file.uuid = fileInstance.uuid;
		return file;
	};
	
	this.existsFile = function(path) {
		return java.invoke(this.uuid, 'existsFile', [path]);
	};
	
	this.getFile = function(path) {
		var fileInstance = java.invoke(this.uuid, 'getFile', [path], true);
		var file = new File();
		file.uuid = fileInstance.uuid;
		return file;
	};
	
	this.getFiles = function(path) {
		var filesInstance = java.invoke(this.uuid, 'getFiles', [path], true);
		var files = new Files();
		files.uuid = filesInstance.uuid;
		return files;
	};
	
	this.deleteFile = function(path) {
		return java.invoke(this.uuid, 'deleteFile', [path]);
	};
	
}

/**
 * Folders object
 */
function Folders() {
	
	this.size = function() {
		var size = java.invoke(this.uuid, 'size', []);
		return size;
	};
	
	this.get = function(index) {
		var folderInstance = java.invoke(this.uuid, 'get', [index], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};
	
}

/**
 * Files object
 */
function Files() {
	
	this.size = function() {
		var size = java.invoke(this.uuid, 'size', []);
		return size;
	};
	
	this.get = function(index) {
		var folderInstance = java.invoke(this.uuid, 'get', [index], true);
		var folder = new File();
		folder.uuid = folderInstance.uuid;
		return folder;
	};
	
}

/**
 * Folder object
 */
function Folder() {

	this.getName = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getName', []);
		return name;
	};
	
	this.getPath = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getPath', []);
		return name;
	};
	
	this.createFolder = function(path) {
		var folderInstance = java.invoke(this.uuid, 'createFolder', [path], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};

	this.existsFolder = function(path) {
		return java.invoke(this.uuid, 'existsFolder', [path]);
	};
	
	this.getFolder = function(path) {
		var folderInstance = java.invoke(this.uuid, 'getFolder', [path], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};
	
	this.getFolders = function(path) {
		var foldersInstance = java.invoke(this.uuid, 'getFolders', [path], true);
		var folders = new Folders();
		folders.uuid = foldersInstance.uuid;
		return folders;
	};
	
	this.deleteFolder = function(path) {
		return java.invoke(this.uuid, 'deleteFolder', [path]);
	};
	
	this.createFile = function(path) {
		var fileInstance = java.invoke(this.uuid, 'createFile', [path], true);
		var file = new File();
		file.uuid = fileInstance.uuid;
		return file;
	};
	
	this.existsFile = function(path) {
		return java.invoke(this.uuid, 'existsFile', [path]);
	};
	
	this.getFile = function(path) {
		var fileInstance = java.invoke(this.uuid, 'getFile', [path], true);
		var file = new File();
		file.uuid = fileInstance.uuid;
		return file;
	};
	
	this.getFiles = function(path) {
		var filesInstance = java.invoke(this.uuid, 'getFiles', [path], true);
		var files = new Files();
		files.uuid = filesInstance.uuid;
		return files;
	};
	
	this.deleteFile = function(path) {
		return java.invoke(this.uuid, 'deleteFile', [path]);
	};
	
}

/**
 * File object
 */
function File() {

	this.getName = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getName', []);
		return name;
	};
	
	this.getPath = function() {
		var collectionInstance = java.invoke(this.uuid, 'getInternal', [], true);
		var name = java.invoke(collectionInstance.uuid, 'getPath', []);
		return name;
	};
	
	this.getContentType = function() {
		return java.invoke(this.uuid, 'getContentType', []);
	};
	
	this.isBinary = function() {
		return java.invoke(this.uuid, 'isBinary', []);
	};
	
	this.getContent = function() {
		var output = java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'getContent', [this.uuid]);
		if (output && output !== null) {
			return JSON.parse(output);
		}
		return output;
	};
	
	this.setContent = function(input) {
		var output = java.call('org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade', 'setContent', [this.uuid, JSON.stringify(input)]);
		return output;
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};
}

