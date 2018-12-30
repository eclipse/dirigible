/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');
var streams = require("io/v3/streams");

exports.isMultipartContent = function() {
	var result = java.call('org.eclipse.dirigible.api.v3.http.HttpUploadFacade', 'isMultipartContent', []);
	return result;
};

exports.parseRequest = function() {
	var fileItemsInstance = java.call('org.eclipse.dirigible.api.v3.http.HttpUploadFacade', 'parseRequest', [], true);
	var fileItems = new FileItems();
	fileItems.uuid = fileItemsInstance.uuid;
	return fileItems;
};

/**
 * FileItems object
 */
function FileItems() {
	
	this.get = function(index) {
		var fileItemInstance = java.invoke(this.uuid, 'get', [index], true);
		var fileItem = new FileItem();
		fileItem.uuid = fileItemInstance.uuid;
		return fileItem;
	};

	this.size = function() {
		return java.invoke(this.uuid, 'size', []);
	};
};

/**
 * FileItem object
 */
function FileItem() {
	
	this.getInputStream = function() {
		var fileItemInstance = java.invoke(this.uuid, 'getInputStream', [], true);
		var inputStream = new streams.InputStream();
		 inputStream.uuid = fileItemInstance.uuid;
		 return inputStream;
	};

	this.getContentType = function() {
		return java.invoke(this.uuid, 'getContentType', []);
	};
	
	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};
	
	this.getSize = function() {
		return java.invoke(this.uuid, 'getSize', []);
	};
	
	this.getBytes = function() {
		var bytes = java.invoke(this.uuid, 'get', []);
		return JSON.parse(bytes);
	};
	
	this.getText = function() {
		return java.invoke(this.uuid, 'getString', []);
	};
	
	this.isFormField = function() {
		return java.invoke(this.uuid, 'isFormField', []);
	};
	
	this.getFieldName = function() {
		return java.invoke(this.uuid, 'getFieldName', []);
	};
	
};


