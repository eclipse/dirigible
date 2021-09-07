/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var streams = require("io/v4/streams");
var zip = require("io/v4/zip");
var folderUtils = require("ide-documents/utils/cmis/folder");
var documentUtils = require("ide-documents/utils/cmis/document");
var objectUtils = require("ide-documents/utils/cmis/object");

const SEPARATOR = '/';

exports.unpackZip = function(folderPath, zip){
	var inputStream = zip.getInputStream();
	createEntries(inputStream, folderPath);
	
	return folderUtils.readFolder(folderUtils.getFolder(folderPath));
};

function createEntries(inputStream, rootPath){
	var zipInputStream = zip.createZipInputStream(inputStream);
	try {
		var zipEntry = zipInputStream.getNextEntry();
		while (zipEntry.isValid()) {
			console.log(zipEntry.getName());
			if (zipEntry.isDirectory()){
				createFolder(rootPath, zipEntry, zipInputStream);
			} else {
				createFile(rootPath, zipEntry, zipInputStream);		
			}
			zipEntry = zipInputStream.getNextEntry();
		}
	} finally {
	    zipInputStream.close();
	}
}

function createFolder(rootPath, zipEntry, zipInputStream){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var path = pathAndName[0];
	var name = pathAndName[1];
	console.log('Creating folder with path:' + path + ' and name: ' + name + ' ...');
	var parent = folderUtils.getFolder(path);
	if (parent === null) {
		createParentFolder(path);
	}
	var parent = folderUtils.getFolder(path);
	folderUtils.createFolder(parent, name);
}

function createParentFolder(path){
	var upperPath = path.substring(0, path.lastIndexOf(SEPARATOR));
	var upper = folderUtils.getFolder(upperPath);
	var upperName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
	
	console.warn('Creating the parent folder first with path: ' + upperPath + ' and name: ' + upperName + ' ...');
	try {
		var parentFolder = folderUtils.createFolder(upper, upperName);
		if (parentFolder === null) {
			createParentFolder(upperPath);
			folderUtils.createFolder(upper, upperName);
		}
	} catch(e) {
		console.error(e.message);
		createParentFolder(upperPath);
		folderUtils.createFolder(upper, upperName);
	}
	
}

function createFile(rootPath, zipEntry, zipInputStream){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var path = pathAndName[0];
	var name = pathAndName[1];
	console.log('Creating file with root: ' + rootPath + ' path: ' + path + ' and name: ' + name + ' ...');
	
	var parent = null;
	try {
		parent = folderUtils.getFolder(path);
		if (parent === null) {
			createParentFolder(path);
		}
	} catch(e) {
		console.error(e.message);
		createParentFolder(path);
	}
	parent = folderUtils.getFolder(path);

	var bytes = zipInputStream.read();
	documentUtils.createFromBytes(parent, name, bytes);
}

function getFullPathAndName(rootPath, fileFullName){
	if (fileFullName.endsWith(SEPARATOR)) {
		fileFullName = fileFullName.substring(0, fileFullName.length - 1);
	}
	var splittedFullName = fileFullName.split(SEPARATOR);
	var innerPath = SEPARATOR + splittedFullName.slice(0, -1).join(SEPARATOR);
	var fullPath = rootPath + innerPath;
	var name = splittedFullName[splittedFullName.length - 1];
	return [fullPath, name];
}

exports.makeZip = function(folderPath, outputStream) {
	var folder = folderUtils.getFolder(folderPath);
	console.info("Creating zip for folder: " + folderPath);

	var zipOutputStream = zip.createZipOutputStream(outputStream);
	try {
		traverseFolder(folder, folder.getName(), zipOutputStream);
	} finally {
		zipOutputStream.close();
	}
};

function traverseFolder(folder, path, zipOutputStream) {
	if (path === 'root') {
		path = '';
	}
	folder.getChildren().forEach(function(child) {
		console.info("Folder: " + folder.getName() + " Path: " + path + " Child: " + child.getName());
		var entryPath = path.lenght === 0 ? '' : path + SEPARATOR;
		entryPath += child.getName();
		var childObject = objectUtils.getById(child.getId());
		var zipEntry;
		if (isFolder(child)) {
			zipEntry = zipOutputStream.createZipEntry(entryPath + SEPARATOR);
			console.log("Zip directory entry: " + zipEntry.getName());
			traverseFolder(childObject, path + SEPARATOR + child.getName(), zipOutputStream);
		} else {
			zipOutputStream.createZipEntry(entryPath);
			var fileStream = childObject.getContentStream().getStream();
			zipOutputStream.write(fileStream.readBytes());
		}
	});
}

function isFolder(cmisObject) {
	return cmisObject.getType().getId() === "cmis:folder";
}
