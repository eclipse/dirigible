/* globals $ */
/* eslint-env node, dirigible */

var zipLib = require('io/zip');
var folderLib = require("docs_explorer/lib/folder_lib");
var documentLib = require("docs_explorer/lib/document_lib");
var objectLib = require("docs_explorer/lib/object_lib");
var streams = require('io/streams');
var zipAPI = require('io/zip');


exports.unpackZip = function(folderPath, zip){
	var inputStream = zip.getInputStream();
	createEntries(inputStream, folderPath);
	
	return folderLib.readFolder(folderLib.getFolder(folderPath));
};

function createEntries(inputStream, rootPath){
	var zipFolderInputStream = zipLib.createZipInputStream(inputStream);
	try {
		var zipEntry = null;
	    while ((zipEntry = zipFolderInputStream.getNextEntry()) !== null) {
	    	console.log(zipEntry.getName());
			if (zipEntry.isDirectory()){
				createFolder(rootPath, zipEntry);
			} else {
				createFile(rootPath, zipEntry);		
			}
		}
	} finally {
	    zipFolderInputStream.close();
	}
}

function createFolder(rootPath, zipEntry){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var parent = folderLib.getFolder(pathAndName[0]);
	folderLib.createFolder(parent, pathAndName[1]);
}

function createFile(rootPath, zipEntry){
	var pathAndName = getFullPathAndName(rootPath, zipEntry.getName());
	var parent = folderLib.getFolder(pathAndName[0]);
	var bytes = zipEntry.readData();
	documentLib.createFromBytes(parent, pathAndName[1], bytes);
}

function getFullPathAndName(rootPath, fileFullName){
	var splittedFullName = fileFullName.split("/");
	var innerPath = "/" + splittedFullName.slice(0, -1).join("/");
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
		traverseFolder(folder, "", zipOutputStream);
	} finally {
		zipOutputStream.close();
	}
	return baos.getBytes();
};

function traverseFolder(folder, path, zipOutputStream) {
	folder.getChildren().forEach(function(child) {
		console.info("Folder: " + folder.getName() + " Path: " + path + " Child: " + child.getName());
		
		var childObject = objectLib.getById(child.getId());
		var zipEntry;
		if (isFolder(child)) {
			zipEntry = zipOutputStream.createZipEntry(path + "/" + child.getName() + "/");
			console.log("Is directory: " + zipEntry.isDirectory());
			console.log("Zip entry: " + zipEntry.getName());
			zipOutputStream.putNextEntry(zipEntry);
			traverseFolder(childObject, path + "/" + child.getName(), zipOutputStream);
		} else {
			zipEntry = zipOutputStream.createZipEntry(path + "/" + child.getName());
			var fileStream = childObject.getContentStream().getStream();
			zipEntry.writeData(streams.read(fileStream));
			zipOutputStream.putNextEntry(zipEntry);
		}
	});
}

function isFolder(cmisObject) {
	return cmisObject.getType() === "cmis:folder";
}
