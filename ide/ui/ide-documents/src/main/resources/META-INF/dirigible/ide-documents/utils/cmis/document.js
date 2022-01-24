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
let streams = require("io/v4/streams");
let objectUtils = require("ide-documents/utils/cmis/object");

let cmisSession = cmis.getSession();

function DocumentSerializer(cmisDocument) {
	this.id = cmisDocument.getId();
	this.name = cmisDocument.getName();
}

exports.uploadDocument = function (folder, document) {
	let fileName = document.getName();
	let mimetype = document.getContentType();
	let size = document.getSize();
	let inputStream = document.getInputStream();

	let newDocument = craeteDocument(folder, fileName, size, mimetype, inputStream);

	return new DocumentSerializer(newDocument);
};

exports.uploadDocumentOverwrite = function (folder, document) {
	let timestamp = new Date().getTime();
	if (document.name === null || document.name === undefined) {
		document.name = document.getName();
	}
	let newName = document.name + "-" + timestamp;
	let oldName = document.name;

	document.name = newName;
	exports.uploadDocument(folder, document);

	try {
		let oldDoc = objectUtils.getObject(folder.getPath() + "/" + oldName);
		objectUtils.deleteObject(oldDoc);
	} catch (e) {
		//do nothing
	}

	let newDoc = objectUtils.getObject(folder.getPath() + "/" + newName);
	objectUtils.renameObject(newDoc, oldName);
};

function craeteDocument(folder, fileName, size, mimetype, inputStream) {
	let contentStream = cmisSession.getObjectFactory().createContentStream(fileName, size, mimetype, inputStream);
	let properties = {};
	properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
	properties[cmis.NAME] = fileName;

	let newDocument = folder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);
	return newDocument;
}

exports.getDocumentStream = function (document) {
	let contentStream = document.getContentStream();
	return contentStream;
};

exports.getDocNameAndStream = function (document) {
	let stream = exports.getDocumentStream(document);
	let name = document.getName();
	return [name, stream];
};

exports.getDocument = function (path) {
	return objectUtils.getObject(path);
}

exports.createFromBytes = function (folder, fileName, bytes) {
	let inputStream = streams.createByteArrayInputStream(bytes);
	let mimeType = "application/octet-stream";

	craeteDocument(folder, fileName, bytes.length, mimeType, inputStream);
};