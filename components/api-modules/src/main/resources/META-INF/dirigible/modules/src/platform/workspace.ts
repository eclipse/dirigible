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
import * as bytes from "@dirigible/io/bytes";
const WorkspaceFacade = Java.type("org.eclipse.dirigible.components.api.platform.WorkspaceFacade");

export function createWorkspace(name) {
	const native = WorkspaceFacade.createWorkspace(name);
	return new Workspace(native);
};

export function getWorkspace(name) {
	const native = WorkspaceFacade.getWorkspace(name);
	return new Workspace(native);
};

export function getWorkspacesNames() {
	const workspacesNames = WorkspaceFacade.getWorkspacesNames();
	if (workspacesNames) {
		return JSON.parse(workspacesNames);
	}
	return workspacesNames;
};

export function deleteWorkspace(name) {
	WorkspaceFacade.deleteWorkspace(name);
};

/**
 * Workspace object
 */
class Workspace {

	constructor(private native) { }

	getProjects() {
		const native = this.native.getProjects();
		return new Projects(native);
	};

	createProject(name) {
		const native = this.native.createProject(name);
		return new Project(native);
	};

	getProject(name) {
		const native = this.native.getProject(name);
		return new Project(native);
	};

	deleteProject(name) {
		this.native.deleteProject(name);
	};

	exists() {
		return this.native.exists();
	};

	existsFolder(path) {
		return this.native.existsFolder(path);
	};

	existsFile(path) {
		return this.native.existsFile(path);
	};

	copyProject(source, target) {
		this.native.copyProject(source, target);
	};

	moveProject(source, target) {
		this.native.moveProject(source, target);
	};

}

/**
 * Projects object
 */
class Projects {

	constructor(private native) { }

	size() {
		return this.native.size();
	};

	get(index) {
		const native = this.native.get(index);
		return new Project(native);
	};

}

/**
 * Project object
 */
class Project {

	constructor(private native) { }

	getName() {
		const collection = this.native.getInternal();
		return collection.getName();
	};

	getPath() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	createFolder(path) {
		const native = this.native.createFolder(path);
		return new Folder(native);
	};

	exists() {
		return this.native.exists();
	};

	existsFolder(path) {
		return this.native.existsFolder(path);
	};

	getFolder(path) {
		const native = this.native.getFolder(path);
		return new Folder(native);
	};

	getFolders(path) {
		const native = this.native.getFolders(path);
		return new Folders(native);
	};

	deleteFolder(path) {
		return this.native.deleteFolder(path);
	};

	createFile(path, input) {
		const native = this.native.createFile(path, input);
		return new File(native);
	};

	existsFile(path) {
		return this.native.existsFile(path);
	};

	getFile(path) {
		const native = this.native.getFile(path);
		return new File(native);
	};

	getFiles(path) {
		const native = this.native.getFiles(path);
		return new Files(native);
	};

	deleteFile(path) {
		return this.native.deleteFile(path);
	};

}

/**
 * Folders object
 */
class Folders {

	constructor(private native) { }

	size() {
		const size = this.native.size();
		return size;
	};

	get(index) {
		const native = this.native.get(index);
		return new Folder(native);
	};

}

/**
 * Files object
 */
class Files {

	constructor(private native) { }

	size() {
		const size = this.native.size();
		return size;
	};

	get(index) {
		const native = this.native.get(index);
		return new File(native);
	};

}

/**
 * Folder object
 */
class Folder {

	constructor(private native) { }

	getName() {
		const collection = this.native.getInternal();
		const name = collection.getName();
		return name;
	};

	getPath() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	createFolder(path) {
		const native = this.native.createFolder(path);
		return new Folder(native);
	};

	exists() {
		return this.native.exists();
	};

	existsFolder(path) {
		return this.native.existsFolder(path);
	};

	getFolder(path) {
		const native = this.native.getFolder(path);
		return new Folder(native);
	};

	getFolders(path) {
		const native = this.native.getFolders(path);
		return new Folders(native);
	};

	deleteFolder(path) {
		return this.native.deleteFolder(path);
	};

	createFile(path, input) {
		const native = this.native.createFile(path, input);
		return new File(native);
	};

	existsFile(path) {
		return this.native.existsFile(path);
	};

	getFile(path) {
		const native = this.native.getFile(path);
		return new File(native);
	};

	getFiles(path) {
		const native = this.native.getFiles(path);
		return new Files(native);
	};

	deleteFile(path) {
		return this.native.deleteFile(path);
	};

}

/**
 * File object
 */
class File {

	constructor(private native) { }

	getName() {
		const collection = this.native.getInternal();
		return collection.getName();
	};

	getPath() {
		const collection = this.native.getInternal();
		return collection.getPath();
	};

	getContentType() {
		return this.native.getContentType();
	};

	isBinary() {
		return this.native.isBinary();
	};

	getContent() {
		const output = WorkspaceFacade.getContent(this.native);
		if (output) {
			output;
		}
		return output;
	};

	getText() {
		const bytesOutput = this.getContent();
		return bytes.byteArrayToText(bytesOutput);
	};

	setContent(input) {
		const output = WorkspaceFacade.setContent(this.native, input);
		return output;
	};

	setText(input) {
		const bytesInput = bytes.textToByteArray(input);
		return this.setContent(bytesInput);
	};

	exists() {
		return this.native.exists();
	};
}
