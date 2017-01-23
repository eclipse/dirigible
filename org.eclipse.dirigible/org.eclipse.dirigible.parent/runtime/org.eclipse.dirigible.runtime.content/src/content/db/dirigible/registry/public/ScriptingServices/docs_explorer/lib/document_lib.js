/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var streams = require('io/streams');
var cmisObjectLib = require("docs_explorer/lib/object_lib");

var cmisSession = cmis.getSession();

function DocumentSerializer(cmisDocument){
	this.id = cmisDocument.getId();
	this.name = cmisDocument.getName();
}

exports.uploadDocument = function(folder, document){
	var fileName = document.name;
	var mimetype = document.contentType;
	var size = document.size;
	var inputStream = document.getInputStream();
	
	var newDocument = craeteDocument(folder, fileName, size, mimetype, inputStream);
	
	return new DocumentSerializer(newDocument);
};

function craeteDocument(folder, fileName, size, mimetype, inputStream){
	var contentStream = cmisSession.getObjectFactory().createContentStream(fileName, size, mimetype, inputStream);
	var properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
	properties[cmis.NAME] = fileName;
	
	var newDocument = folder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);
	return newDocument;
}

exports.getDocumentStream = function(document){
	var contentStream = document.getContentStream();
	return contentStream;
};

exports.getDocNameAndStream = function(document){
	var stream = exports.getDocumentStream(document);
	var name = document.getName();
	return [name, stream];
};

exports.getDocument = function(path){
	return cmisObjectLib.getObject(path);
}

exports.createFromBytes = function(folder, fileName, bytes){
	var inputStream = streams.createByteArrayInputStream(bytes);
	var mimeType = "application/octet-stream";
	
	craeteDocument(folder, fileName, bytes.length, mimeType, inputStream);
};
