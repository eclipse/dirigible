/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const bytes = require("io/bytes");

exports.createWorkspace = function(name) {
	const native = org.eclipse.dirigible.components.api.platform.WorkspaceFacade.createWorkspace(name);
	const workspace = new Workspace();
	workspace.native = native;
	return workspace;
};

exports.getWorkspace = function(name) {
	const native = org.eclipse.dirigible.components.api.platform.WorkspaceFacade.getWorkspace(name);
	const workspace = new Workspace();
	workspace.native = native;
	return workspace;
};

exports.getWorkspacesNames = function() {
	const workspacesNames = org.eclipse.dirigible.components.api.platform.WorkspaceFacade.getWorkspacesNames();
	if (workspacesNames) {
		return JSON.parse(workspacesNames);
	}
	return workspacesNames;
};

exports.deleteWorkspace = function(name) {
	org.eclipse.dirigible.components.api.platform.WorkspaceFacade.deleteWorkspace(name);
};

/**
 * Workspace object
 */
function Workspace() {

	this.getProjects = function() {
		const native = this.native.getProjects();
		const projects = new Projects();
		projects.native = native;
		return projects;
	};

	this.createProject = function(name) {
		const native = this.native.createProject(name);
		const project = new Project();
		project.native = native;
		return project;
	};

	this.getProject = function(name) {
		const native = this.native.getProject(name);
		const project = new Project();
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
		return this.native.size();
	};

	this.get = function(index) {
		const native = this.native.get(index);
		const project = new Project();
		project.native = native;
		return project;
	};

}

/**
 * Project object
 */
function Project() {

	this.getName = function() {
		const collection = this.native.getInternal();
		return collection.getName();
	};

	this.getPath = function() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	this.createFolder = function(path) {
		const native = this.native.createFolder(path);
		const folder = new Folder();
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
		const native = this.native.getFolder(path);
		const folder = new Folder();
		folder.native = native;
		return folder;
	};

	this.getFolders = function(path) {
		const native = this.native.getFolders(path);
		const folders = new Folders();
		folders.native = native;
		return folders;
	};

	this.deleteFolder = function(path) {
		return this.native.deleteFolder(path);
	};

	this.createFile = function(path, input) {
		const native = this.native.createFile(path, input);
		const file = new File();
		file.native = native;
		return file;
	};

	this.existsFile = function(path) {
		return this.native.existsFile(path);
	};

	this.getFile = function(path) {
		const native = this.native.getFile(path);
		const file = new File();
		file.native = native;
		return file;
	};

	this.getFiles = function(path) {
		const native = this.native.getFiles(path);
		const files = new Files();
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
		const size = this.native.size();
		return size;
	};

	this.get = function(index) {
		const native = this.native.get(index);
		const folder = new Folder();
		folder.native = native;
		return folder;
	};

}

/**
 * Files object
 */
function Files() {

	this.size = function() {
		const size = this.native.size();
		return size;
	};

	this.get = function(index) {
		const native = this.native.get(index);
		const folder = new File();
		folder.native = native;
		return folder;
	};

}

/**
 * Folder object
 */
function Folder() {

	this.getName = function() {
		const collection = this.native.getInternal();
		const name = collection.getName();
		return name;
	};

	this.getPath = function() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	this.createFolder = function(path) {
		const native = this.native.createFolder(path);
		const folder = new Folder();
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
		const native = this.native.getFolder(path);
		const folder = new Folder();
		folder.native = native;
		return folder;
	};

	this.getFolders = function(path) {
		const native = this.native.getFolders(path);
		const folders = new Folders();
		folders.native = native;
		return folders;
	};

	this.deleteFolder = function(path) {
		return this.native.deleteFolder(path);
	};

	this.createFile = function(path, input) {
		const native = this.native.createFile(path, input);
		const file = new File();
		file.native = native;
		return file;
	};

	this.existsFile = function(path) {
		return this.native.existsFile(path);
	};

	this.getFile = function(path) {
		const native = this.native.getFile(path);
		const file = new File();
		file.native = native;
		return file;
	};

	this.getFiles = function(path) {
		const native = this.native.getFiles(path);
		const files = new Files();
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
		const collection = this.native.getInternal();
		return collection.getName();
	};

	this.getPath = function() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	this.getContentType = function() {
		return this.native.getContentType();
	};

	this.isBinary = function() {
		return this.native.isBinary();
	};

	this.getContent = function() {
		const output = org.eclipse.dirigible.components.api.platform.WorkspaceFacade.getContent(this.native);
		if (output) {
			output;
		}
		return output;
	};

	this.getText = function() {
		const bytesOutput = this.getContent();
		return bytes.byteArrayToText(bytesOutput);
	};

	this.setContent = function(input) {
		const output = org.eclipse.dirigible.components.api.platform.WorkspaceFacade.setContent(this.native, input);
		return output;
	};

	this.setText = function(input) {
		const bytesInput = bytes.textToByteArray(input);
		return this.setContent(bytesInput);
	};

	this.exists = function() {
		return this.native.exists();
	};
}
