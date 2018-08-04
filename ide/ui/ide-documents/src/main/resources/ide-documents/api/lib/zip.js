/* globals $ */
/* eslint-env node, dirigible */

var streams = require('io/v3/streams');
var zipAPI = require('io/v3/zip');
var folderLib = require("ide-documents/api/lib/folder");
var documentLib = require("ide-documents/api/lib/document");
var objectLib = require("ide-documents/api/lib/object");

const SEPARATOR = '/';

exports.unpackZip = function(folderPath, zip){
	var inputStream = zip.getInputStream();
	createEntries(inputStream, folderPath);
	
	return folderLib.readFolder(folderLib.getFolder(folderPath));
};

function createEntries(inputStream, rootPath){
	var zipInputStream = zipAPI.createZipInputStream(inputStream);
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
	var parent = folderLib.getFolder(path);
	folderLib.createFolder(parent, name);
}

function createParentFolder(path){
	var upperPath = path.substring(0, path.lastIndexOf(SEPARATOR));
	var upper = folderLib.getFolder(upperPath);
	var upperName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
	
	console.warn('Creating the parent folder first with path: ' + upperPath + ' and name: ' + upperName + ' ...');
	try {
		var parentFolder = folderLib.createFolder(upper, upperName);
		if (parentFolder === null) {
			createParentFolder(upperPath);
			folderLib.createFolder(upper, upperName);
		}
	} catch(e) {
		console.error(e.message);
		createParentFolder(upperPath);
		folderLib.createFolder(upper, upperName);
	}
	
}

function createFile(rootPath, zipEntry, zipInputStream){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var path = pathAndName[0];
	var name = pathAndName[1];
	console.log('Creating file with root: ' + rootPath + ' path: ' + path + ' and name: ' + name + ' ...');
	
	var parent = null;
	try {
		parent = folderLib.getFolder(path);
		if (parent === null) {
			createParentFolder(path);
		}
	} catch(e) {
		console.error(e.message);
		createParentFolder(path);
	}
	parent = folderLib.getFolder(path);

	var bytes = zipInputStream.read();
	documentLib.createFromBytes(parent, name, bytes);
}

function getFullPathAndName(rootPath, fileFullName){
	if (fileFullName.endsWith(SEPARATOR)) {
		fileFullName = fileFullName.substring(0, fileFullName.length - 1);
	}
	var splittedFullName = fileFullName.split(SEPARATOR);
	var innerPath = SEPARATOR + splittedFullName.slice(0, -1).join(SEPARATOR);
	var fullPath = rootPath + innerPath;
	var name = splittedFullName[splittedFullName.length - 1];
//	if (fullPath.startsWith('//')) {
//		fullPath = fullPath.substring(1, fullPath.length);
//	}
	return [fullPath, name];
}

exports.makeZip = function(folderPath, outputStream) {
	var folder = folderLib.getFolder(folderPath);
	console.info("Creating zip for folder: " + folderPath);

//	var baos = streams.createByteArrayOutputStream();
	var zipOutputStream = zipAPI.createZipOutputStream(outputStream);
	try {
		traverseFolder(folder, folder.getName(), zipOutputStream);
	} finally {
		zipOutputStream.close();
	}
//	return baos.getBytes();
};



function traverseFolder(folder, path, zipOutputStream) {
	folder.getChildren().forEach(function(child) {
		console.info("Folder: " + folder.getName() + " Path: " + path + " Child: " + child.getName());
		var entryPath = path.lenght === 0 ? '' : path + SEPARATOR;
		entryPath += child.getName();
		var childObject = objectLib.getById(child.getId());
		var zipEntry;
		if (isFolder(child)) {
			// zipEntry = zipOutputStream.createZipEntry(entryPath + SEPARATOR);
			// console.log("Zip directory entry: " + zipEntry.getName());
			// zipOutputStream.putNextEntry(zipEntry);
			traverseFolder(childObject, path + SEPARATOR + child.getName(), zipOutputStream);
		} else {
			zipOutputStream.createZipEntry(entryPath);
			var fileStream = childObject.getContentStream().getStream();
			console.warn('File Stream: ' + JSON.stringify(fileStream));
			zipOutputStream.write(fileStream.readBytes());
		}
	});
}

function isFolder(cmisObject) {
	return cmisObject.getType().getId() === "cmis:folder";
}
