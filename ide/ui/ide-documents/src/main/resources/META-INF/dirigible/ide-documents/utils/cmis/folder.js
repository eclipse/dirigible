/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let cmis = require("cms/v4/cmis");
let user = require("security/v4/user");
let objectUtils = require("ide-documents/utils/cmis/object");

let cmisSession = cmis.getSession();

function ChildSerializer(cmisObject) {
	this.name = cmisObject.getName();
	this.type = cmisObject.getType().getId();
	this.id = cmisObject.getId();

	let readAccessDefinitions = getReadAccessDefinitions(this.id);
	let writeAccessDefinitions = getWriteAccessDefinitions(this.id);

	if (readAccessDefinitions.length > 0 || writeAccessDefinitions.length > 0) {
		this.restrictedAccess = true;
	}
	let readOnly;
	let writeOnly;
	let pathReadAccessDefinitionFound = false;
	let pathWriteAccessDefinitionFound = false;
	if (readAccessDefinitions.length > 0) {
		for (let i = 0; i < readAccessDefinitions.length; i++) {
			if (readAccessDefinitions[i].path === this.id) {
				readOnly = hasAccess([readAccessDefinitions[i]]);
				pathReadAccessDefinitionFound = true;
				break;
			}
		}
	}
	if (writeAccessDefinitions.length > 0) {
		for (let i = 0; i < writeAccessDefinitions.length; i++) {
			if (writeAccessDefinitions[i].path === this.id) {
				writeOnly = hasAccess([writeAccessDefinitions[i]]);
				pathWriteAccessDefinitionFound = true;
				break;
			}
		}
	}

	if (!pathReadAccessDefinitionFound && !pathWriteAccessDefinitionFound) {
		let readOnlyAccessDefinitions = readAccessDefinitions.filter(e => e.method === cmis.METHOD_READ);
		let writeOnlyAccessDefinitions = writeAccessDefinitions.filter(e => e.method === cmis.METHOD_WRITE);
		if (readOnlyAccessDefinitions.length > 0) {
			readOnly = hasAccess(readOnlyAccessDefinitions);
		}
		if (writeOnlyAccessDefinitions.length > 0) {
			writeOnly = hasAccess(writeOnlyAccessDefinitions);
		}
	}

	if (readOnly && !writeOnly || !readOnly && writeOnly) {
		this.readOnly = readOnly;
		this.writeOnly = writeOnly;
	}
}

function FolderSerializer(cmisFolder) {

	this.name = cmisFolder.getName();
	this.id = cmisFolder.getId();
	this.path = cmisFolder.getPath();
	this.parentId = null;
	this.children = [];


	if (!cmisFolder.isRootFolder()) {
		let parent = cmisFolder.getFolderParent();
		if (parent !== null) {
			this.parentId = parent.getId();
		}
	}

	let children = cmisFolder.getChildren();
	for (let i in children) {
		let child = new ChildSerializer(children[i]);
		this.children.push(child);
	}
	this.children = this.children.sort((x, y) => x.path > y.path ? 1 : -1);
}

exports.readFolder = function (folder) {
	return new FolderSerializer(folder);
};

exports.createFolder = function (parentFolder, name) {
	let properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_FOLDER;
	properties[cmis.NAME] = name;
	let newFolder = parentFolder.createFolder(properties);

	return new FolderSerializer(newFolder);
};

exports.getFolderOrRoot = function (folderPath) {
	if (folderPath === null) {
		let rootFolder = cmisSession.getRootFolder();
		return rootFolder;
	}
	let folder = null;
	try {
		folder = exports.getFolder(folderPath);
	} catch (e) {
		folder = cmisSession.getRootFolder();
	}
	return folder;
};

exports.getFolder = function (path) {
	return objectUtils.getObject(path);
};

exports.deleteTree = function (folder) {
	folder.deleteTree();
};

function getReadAccessDefinitions(path) {
	return cmis.getAccessDefinitions(path, cmis.METHOD_READ);
}

function getWriteAccessDefinitions(path) {
	return cmis.getAccessDefinitions(path, cmis.METHOD_WRITE);
}

function hasAccess(accessDefinitions) {
	for (let i = 0; i < accessDefinitions.length; i++) {
		if (!user.isInRole(accessDefinitions[i].role)) {
			return false;
		}
	}
	return true;
}