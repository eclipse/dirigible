/* globals $ */
/* eslint-env node, dirigible */

var zipLib = require('io/zip');
var folderLib = require("docs_explorer/lib/folder_lib");
var documentLib = require("docs_explorer/lib/document_lib");
var objectLib = require("docs_explorer/lib/object_lib");
var streams = require('io/streams');
var zipAPI = require('io/zip');

const SEPARATOR = '/';

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
	var path = pathAndName[0];
	var name = pathAndName[1];
	console.log('Creating folder with path:' + path + ' and name: ' + name + ' ...');
	var parent = folderLib.getFolder(path);
	folderLib.createFolder(parent, name);
}

function createFile(rootPath, zipEntry){
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

	var bytes = zipEntry.readData();
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
			zipEntry = zipOutputStream.createZipEntry(entryPath + SEPARATOR);
			console.log("Zip directory entry: " + zipEntry.getName());
			zipOutputStream.putNextEntry(zipEntry);
			traverseFolder(childObject, path + SEPARATOR + child.getName(), zipOutputStream);
		} else {
			zipEntry = zipOutputStream.createZipEntry(entryPath);
			console.log("Zip file entry: " + zipEntry.getName());
			var fileStream = childObject.getContentStream().getStream();
			zipEntry.writeData(streams.read(fileStream));
			zipOutputStream.putNextEntry(zipEntry);
		}
	});
}

function isFolder(cmisObject) {
	return cmisObject.getType() === "cmis:folder";
}
