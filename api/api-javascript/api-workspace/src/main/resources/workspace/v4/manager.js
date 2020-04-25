/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

exports.createWorkspace = function(name) {
	var native = org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.createWorkspace(name);
	var workspace = new Workspace();
	workspace.native = native;
	return workspace;
};

exports.getWorkspace = function(name) {
	var native = org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.getWorkspace(name);
	var workspace = new Workspace();
	workspace.native = native;
	return workspace;
};

exports.getWorkspacesNames = function() {
	var workspacesNames = org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.getWorkspacesNames();
	if (workspacesNames) {
		return JSON.parse(workspacesNames);
	}
	return workspacesNames;
};

exports.deleteWorkspace = function(name) {
	org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.deleteWorkspace(name);
};

/**
 * Workspace object
 */
function Workspace() {

	this.getProjects = function() {
		var native = this.native.getProjects();
		var projects = new Projects();
		projects.native = native;
		return projects;
	};

	this.createProject = function(name) {
		var native = this.native.createProject(name);
		var project = new Project();
		project.native = native;
		return project;
	};

	this.getProject = function(name) {
		var native = this.native.getProject(name);
		var project = new Project();
		project.native = native;
		return project;
	};

	this.deleteProject = function(name) {
		this.native.deleteProject(name);
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.existsFolder = function(path) {
		return this.native.existsFolder(path);
	};

	this.existsFile = function(path) {
		return this.native.existsFile(path);
	};

	this.copyProject = function(source, target) {
		this.native.copyProject(source, target);
	};

	this.moveProject = function(source, target) {
		this.native.moveProject(source, target);
	};

}

/**
 * Projects object
 */
function Projects() {
	
	this.size = function() {
		var size = this.native.size();
		return size;
	};
	
	this.get = function(index) {
		var native = this.native.get(index);
		var project = new Project();
		project.native = native;
		return project;
	};
	
}

/**
 * Project object
 */
function Project() {

	this.getName = function() {
		var collection = this.native.getInternal();
		var name = collection.getName();
		return name;
	};
	
	this.getPath = function() {
		var collection = this.native.getInternal();
		var name = collection.getPath();
		return name;
	};
	
	this.createFolder = function(path) {
		var native = this.native.createFolder(path);
		var folder = new Folder();
		folder.native = native;
		return folder;
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.existsFolder = function(path) {
		return this.native.existsFolder(path);
	};
	
	this.getFolder = function(path) {
		var native = this.native.getFolder(path);
		var folder = new Folder();
		folder.native = native;
		return folder;
	};
	
	this.getFolders = function(path) {
		var native = this.native.getFolders(path);
		var folders = new Folders();
		folders.native = native;
		return folders;
	};
	
	this.deleteFolder = function(path) {
		return this.native.deleteFolder(path);
	};
	
	this.createFile = function(path, input) {
		var native = this.native.createFile(path, input);
		var file = new File();
		file.native = native;
		return file;
	};
	
	this.existsFile = function(path) {
		return this.native.existsFile(path);
	};
	
	this.getFile = function(path) {
		var native = this.native.getFile(path);
		var file = new File();
		file.native = native;
		return file;
	};
	
	this.getFiles = function(path) {
		var native = this.native.getFiles(path);
		var files = new Files();
		files.native = native;
		return files;
	};
	
	this.deleteFile = function(path) {
		return this.native.deleteFile(path);
	};
	
}

/**
 * Folders object
 */
function Folders() {
	
	this.size = function() {
		var size = this.native.size();
		return size;
	};
	
	this.get = function(index) {
		var native = this.native.get(index);
		var folder = new Folder();
		folder.native = native;
		return folder;
	};
	
}

/**
 * Files object
 */
function Files() {
	
	this.size = function() {
		var size = this.native.size();
		return size;
	};
	
	this.get = function(index) {
		var native = this.native.get(index);
		var folder = new File();
		folder.native = native;
		return folder;
	};
	
}

/**
 * Folder object
 */
function Folder() {

	this.getName = function() {
		var collection = this.native.getInternal();
		var name = collection.getName();
		return name;
	};
	
	this.getPath = function() {
		var collection = this.native.getInternal();
		var path = collection.getPath();
		return path;
	};
	
	this.createFolder = function(path) {
		var folder = this.native.createFolder(path);
		var folder = new Folder();
		folder.native = native;
		return folder;
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.existsFolder = function(path) {
		return this.native.existsFolder(path);
	};
	
	this.getFolder = function(path) {
		var folder = this.native.getFolder(path);
		var folder = new Folder();
		folder.native = native;
		return folder;
	};
	
	this.getFolders = function(path) {
		var folders = this.native.getFolders(path);
		var folders = new Folders();
		folders.native = native;
		return folders;
	};
	
	this.deleteFolder = function(path) {
		return this.native.deleteFolder(path);
	};
	
	this.createFile = function(path, input) {
		var file = this.native.createFile(path, input);
		var file = new File();
		file.native = native;
		return file;
	};
	
	this.existsFile = function(path) {
		return this.native.existsFile(path);
	};
	
	this.getFile = function(path) {
		var file = this.native.getFile(path);
		var file = new File();
		file.native = native;
		return file;
	};
	
	this.getFiles = function(path) {
		var native = this.native.getFiles(path);
		var files = new Files();
		files.native = native;
		return files;
	};
	
	this.deleteFile = function(path) {
		return this.native.deleteFile(path);
	};
	
}

/**
 * File object
 */
function File() {

	this.getName = function() {
		var collection = this.native.getInternal();
		var name = collection.getName();
		return name;
	};
	
	this.getPath = function() {
		var collection = this.native.getInternal();
		var path = collection.getPath();
		return path;
	};
	
	this.getContentType = function() {
		return this.native.getContentType();
	};
	
	this.isBinary = function() {
		return this.native.isBinary();
	};
	
	this.getContent = function() {
		var output = org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.getContent(this.native);
		if (output && output !== null) {
			output;
		}
		return output;
	};
	
	this.setContent = function(input) {
		var output = org.eclipse.dirigible.api.v3.workspace.WorkspaceFacade.setContent(this.native, input);
		return output;
	};

	this.exists = function() {
		return this.native.exists();
	};
}
