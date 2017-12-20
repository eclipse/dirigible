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

function createFile(rootPath, zipEntry, zipInputStream){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var path = pathAndName[0];
	var name = pathAndName[1];
	console.log('Creating file with path: ' + path + ' and name: ' + name + ' ...');
	
	var parent = null;
	try {
		parent = folderLib.getFolder(path);
	} catch(e) {
		console.error(e.message);
		var upper = folderLib.getFolder(rootPath);
		var upperPath = path.substring(path.lastIndexOf(SEPARATOR) + 1);
		console.warn('Creating the parent folder first with path: ' + path + ' ...');
		folderLib.createFolder(upper, upperPath);
	}
	parent = folderLib.getFolder(path);

	var bytes = zipInputStream.read();
	documentLib.createFromBytes(parent, name, bytes);
}

function getFullPathAndName(rootPath, fileFullName){
	var splittedFullName = fileFullName.split(SEPARATOR);
	var innerPath = SEPARATOR + splittedFullName.slice(0, -1).join(SEPARATOR);
	var fullPath = rootPath + innerPath;
	var name = splittedFullName[splittedFullName.length - 1];
	
	return [fullPath, name];
}

exports.makeZip = function(folderPath) {
	var folder = folderLib.getFolder(folderPath);
	console.info("Creating zip for folder: " + folderPath);

	var baos = streams.createByteArrayOutputStream();
	var zipOutputStream = zipAPI.createZipOutputStream(baos);
	try {
		traverseFolder(folder, folder.getName(), zipOutputStream);
	} finally {
		zipOutputStream.close();
	}
	return baos.getBytes();
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
