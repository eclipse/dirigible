/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

var streams = require('io/streams');

exports.getWorkspace = function() {
	return new Workspace($.getWorkspacesService().getWorkspace($.getRequest()));
};

exports.getUserWorkspace = function(user) {
	return new Workspace($.getWorkspacesService().getUserWorkspace(user, $.getRequest()));
};

/**
 * Workspace object
 */
function Workspace(internalWorkspace) {
	this.internalWorkspace = internalWorkspace;

	this.getInternalObject = function() {
		return this.internalWorkspace;
	};

	this.getRoot = function() {
		var internalWorkspaceRoot = this.internalWorkspace.getRoot();
		return new WorkspaceRoot(internalWorkspaceRoot);
	};
}

/**
 * WorkspaceRoot object
 */
function WorkspaceRoot(internalWorkspaceRoot) {
	this.internalWorkspaceRoot = internalWorkspaceRoot;

	this.getInternalObject = function() {
		return this.internalWorkspaceRoot;
	};

	this.getProjects = function() {
		var internalProjects = this.internalWorkspaceRoot.getProjects();
		var projects = [];
		for (var i=0;i<internalProjects.length;i++) {
	    	var project = new Project(internalProjects[i]);
	    	projects.push(project);
		}
		return projects;
	};

	this.getProject = function(projectName) {
		var internalProject = this.internalWorkspaceRoot.getProject(projectName);
		return new Project(internalProject);
	};
}

/**
 * Project object
 */
function Project(internalProject) {
	this.internalProject = internalProject;

	this.getInternalObject = function() {
		return this.internalProject;
	};

	this.getName = function() {
		return this.internalProject.getName();
	};

	this.create = function() {
		return this.internalProject.create(null);
	};

	this.delete = function() {
		return this.internalProject.delete(true, true, null);
	};

	this.open = function() {
		return this.internalProject.open(null);
	};

	this.close = function() {
		return this.internalProject.close(null);
	};

	this.getFolder = function(name) {
		var internalFolder = this.internalProject.getFolder(name);
		return new Folder(internalFolder);
	};

	this.getFile = function(name) {
		var internalFile = this.internalProject.getFile(name);
		return new File(internalFile);
	};

	this.exists = function() {
		return this.internalProject.exists();
	};
}

/**
 * Folder object
 */
function Folder(folder) {
	this.internalFolder = folder;

	this.getInternalObject = function() {
		return this.internalFolder;
	};

	this.create = function() {
		this.internalFolder.create(true, true, null);
	};

	this.delete = function() {
		this.internalFolder.delete(true, false, null);
	};

	this.getFolder = function(name) {
		var internalFolder = this.internalFolder.getFolder(name);
		return new Folder(internalFolder);
	};

	this.getFile = function(name) {
		var internalFile = this.internalFolder.getFile(name);
		return new File(internalFile);
	};

	this.getName = function() {
		return this.internalFolder.getName();
	};

	this.getFullPath = function() {
		return this.internalFolder.getFullPath().toString();
	};

	this.exists = function() {
		return this.internalFolder.exists();
	};
}

/**
 * File object
 */
function File(internalFile) {
	this.internalFile = internalFile;

	this.getInternalObject = function() {
		return this.internalFile;
	};

	this.create = function(inputStream) {
		this.internalFile.create(inputStream.getInternalObject(), true, null);
	};

	this.delete = function() {
		this.internalFile.delete(true, false, null);
	};

	this.getContents = function() {
		return new streams.InputStream(this.internalFile.getContents());
	};

	this.setContents = function(inputStream) {
		this.internalFile.setContents(inputStream.getInternalObject(), true, false, null);
	};

	this.getName = function() {
		return this.internalFile.getName();
	};

	this.getFullPath = function() {
		return this.internalFile.getFullPath().toString();
	};

	this.exists = function() {
		return this.internalFile.exists();
	};
}