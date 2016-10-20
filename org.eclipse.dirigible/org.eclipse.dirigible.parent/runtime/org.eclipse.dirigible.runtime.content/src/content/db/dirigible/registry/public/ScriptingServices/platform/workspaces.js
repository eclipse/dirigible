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
	this.getInternalObject = workspaceGetInternalObject;
	this.getRoot = workspaceGetRoot;
}

function workspaceGetInternalObject() {
	return this.internalWorkspace;
}

function workspaceGetRoot() {
	var internalWorkspaceRoot = this.internalWorkspace.getRoot();
	return new WorkspaceRoot(internalWorkspaceRoot);
}

/**
 * WorkspaceRoot object
 */
function WorkspaceRoot(internalWorkspaceRoot) {
	this.internalWorkspaceRoot = internalWorkspaceRoot;
	this.getInternalObject = workspaceRootGetInternalObject;
	this.getProjects = workspaceRootGetProjects;
	this.getProject = workspaceRootGetProject;
}

function workspaceRootGetInternalObject() {
	return this.internalWorkspaceRoot;
}

function workspaceRootGetProjects() {
	var internalProjects = this.internalWorkspaceRoot.getProjects();
	var projects = [];
	for (var i=0;i<internalProjects.length;i++) {
    	var project = new Project(internalProjects[i]);
    	projects.push(project);
	}
	return projects;
}

function workspaceRootGetProject(projectName) {
	var internalProject = this.internalWorkspaceRoot.getProject(projectName);
	return new Project(internalProject);
}

/**
 * Project object
 */
function Project(internalProject) {
	this.internalProject = internalProject;
	this.getInternalObject = projectGetInternalObject;
	this.getName = projectGetName;
	this.create = projectCreate;
	this.delete = projectDelete;
	this.open = projectOpen;
	this.close = projectClose;
	this.getFolder = projectGetFolder;
	this.getFile = projectGetFile;
	this.exists = projectExists;
}

function projectGetInternalObject() {
	return this.internalProject;
}

function projectGetName() {
	return this.internalProject.getName();
}

function projectCreate() {
	return this.internalProject.create(null);
}

function projectDelete() {
	return this.internalProject.delete(true, true, null);
}

function projectOpen() {
	return this.internalProject.open(null);
}

function projectClose() {
	return this.internalProject.close(null);
}

function projectGetFolder(name) {
	var internalFolder = this.internalProject.getFolder(name);
	return new Folder(internalFolder);
}

function projectGetFile(name) {
	var internalFile = this.internalProject.getFile(name);
	return new File(internalFile);
}

function projectExists() {
	return this.internalProject.exists();
}

/**
 * Folder object
 */
function Folder(internalFolder) {
	this.internalFolder = internalFolder;
	this.getInternalObject = folderGetInternalObject;
	this.create = folderCreate;
	this.delete = folderDelete;
	this.getFolder = folderGetFolder;
	this.getFile = folderGetFile;
	this.getName = folderGetName;
	this.getFullPath = folderGetFullPath;
	this.exists = folderExists;
}

function folderGetInternalObject() {
	return this.internalFolder;
}

function folderCreate() {
	this.internalFolder.create(true, true, null);
}

function folderDelete() {
	this.internalFolder.delete(true, false, null);
}

function folderGetFolder(name) {
	var internalFolder = this.internalFolder.getFolder(name);
	return new Folder(internalFolder);
}

function folderGetFile(name) {
	var internalFile = this.internalFolder.getFile(name);
	return new File(internalFile);
}

function folderGetName() {
	return this.internalFolder.getName();
}

function folderGetFullPath() {
	return this.internalFolder.getFullPath().toString();
}

function folderExists() {
	return this.internalFolder.exists();
}



/**
 * File object
 */
function File(internalFile) {
	this.internalFile = internalFile;
	this.getInternalObject = fileGetInternalObject;
	this.create = fileCreate;
	this.delete = fileDelete;
	this.getContents = fileGetContents;
	this.setContents = fileSetContents;
	this.getName = fileGetName;
	this.getFullPath = fileGetFullPath;
	this.exists = fileExists;
}

function fileGetInternalObject() {
	return this.internalFile;
}

function fileCreate(inputStream) {
	this.internalFile.create(inputStream.getInternalObject(), true, null);
}

function fileDelete() {
	this.internalFile.delete(true, false, null);
}

function fileGetContents() {
	return new streams.InputStream(this.internalFile.getContents());
}

function fileSetContents(inputStream) {
	this.internalFile.setContents(inputStream.getInternalObject(), true, false, null);
}

function fileGetName() {
	return this.internalFile.getName();
}

function fileGetFullPath() {
	return this.internalFile.getFullPath().toString();
}

function fileExists() {
	return this.internalFile.exists();
}
