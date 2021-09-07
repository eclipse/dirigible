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
var cmis = require("cms/v4/cmis");
var streams = require("io/v4/streams");
var objectUtils = require("ide-documents/utils/cmis/object");

var cmisSession = cmis.getSession();

function DocumentSerializer(cmisDocument){
	this.id = cmisDocument.getId();
	this.name = cmisDocument.getName();
}

exports.uploadDocument = function(folder, document){
	var fileName = document.getName();
	var mimetype = document.getContentType();
	var size = document.getSize();
	var inputStream = document.getInputStream();

	var newDocument = craeteDocument(folder, fileName, size, mimetype, inputStream);
	
	return new DocumentSerializer(newDocument);
};

exports.uploadDocumentOverwrite = function(folder, document){
	var timestamp = new Date().getTime();
	if (document.name === null || document.name === undefined) {
		document.name = document.getName();
	}
	var newName = document.name + "-" + timestamp;
	var oldName = document.name;
	
	document.name = newName;
	exports.uploadDocument(folder, document);
	
	try {
		var oldDoc = objectUtils.getObject(folder.getPath() + "/" + oldName);
		objectUtils.deleteObject(oldDoc);	
 	} catch(e){
 		//do nothing
 	}
	
	var newDoc = objectUtils.getObject(folder.getPath() + "/" + newName);
	objectUtils.renameObject(newDoc, oldName);
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
	return objectUtils.getObject(path);
}

exports.createFromBytes = function(folder, fileName, bytes){
	var inputStream = streams.createByteArrayInputStream(bytes);
	var mimeType = "application/octet-stream";
	
	craeteDocument(folder, fileName, bytes.length, mimeType, inputStream);
};
