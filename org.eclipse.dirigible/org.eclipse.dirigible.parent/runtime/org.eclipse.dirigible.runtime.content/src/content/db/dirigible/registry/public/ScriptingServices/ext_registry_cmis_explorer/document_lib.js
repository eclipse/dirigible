/* globals $ */
/* eslint-env node, dirigible */

var cmis = require('doc/cmis');
var streams = require('io/streams');
var response = require("net/http/response");

var cmisSession = cmis.getSession();

function DocumentSerializer(cmisDocument){
	this.id = cmisDocument.getId();
	this.name = cmisDocument.getName();
}

exports.downloadDocument = function(documentId){
	var doc = cmisSession.getObject(documentId);
	
	var contentType = doc.getContentStream().getInternalObject().getMimeType();
	response.setContentType(contentType);
	response.addHeader("Content-Disposition", "attachment;filename=\"" + doc.getName() + "\"");
	
	var contentStream = doc.getContentStream();
	response.writeStream(contentStream.getStream());
};

exports.previewDocument = function(documentId){
	var doc = cmisSession.getObject(documentId);
	var contentType = doc.getContentStream().getInternalObject().getMimeType();
	response.setContentType(contentType);
	var contentStream = doc.getContentStream();
	response.writeStream(contentStream.getStream());
};

exports.uploadDocument = function(folderId, document){
	var folder;
	if (folderId !== null){
		folder = cmisSession.getObject(folderId);
	} else {
		folder = cmisSession.getRootFolder();
	}
	
	var fileName = document.name;
	var mimetype = document.contentType;
	var size = document.size;
	var inputStream = document.getInputStream();
	var contentStream = cmisSession.getObjectFactory().createContentStream(fileName, size, mimetype, inputStream);
	var properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
	properties[cmis.NAME] = fileName;
	
	var newDocument = folder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);
	
	return new DocumentSerializer(newDocument);
};
