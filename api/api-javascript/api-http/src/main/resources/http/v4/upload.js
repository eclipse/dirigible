/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Upload
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

var streams = require("io/v4/streams");
var bytes = require("io/v4/bytes");

exports.isMultipartContent = function() {
	var result = org.eclipse.dirigible.api.v3.http.HttpUploadFacade.isMultipartContent();
	return result;
};

exports.parseRequest = function() {
	var fileItems = new FileItems();
	var native = org.eclipse.dirigible.api.v3.http.HttpUploadFacade.parseRequest();
	fileItems.native = native;
	return fileItems;
};

/**
 * FileItems object
 */
function FileItems() {
	
	this.get = function(index) {
		var fileItem = new FileItem();
		var native = this.native.get(index);
		fileItem.native = native;
		return fileItem;
	};

	this.size = function() {
		return this.native.size();
	};
};

/**
 * FileItem object
 */
function FileItem() {
	
	this.getInputStream = function() {
		var inputStream = new streams.InputStream();
		var native = this.native.getInputStream();
		inputStream.native = native;
		return inputStream;
	};

	this.getContentType = function() {
		return this.native.getContentType();
	};
	
	this.getName = function() {
		return this.native.getName();
	};
	
	this.getSize = function() {
		return this.native.getSize();
	};
	
	this.getBytes = function() {
		var data = this.native.get();
		return bytes.toJavaScriptBytes(data);
	};
	
	this.getBytesNative = function() {
		var data = this.native.get();
		return data;
	};
	
	this.getText = function() {
		return this.native.getString();
	};
	
	this.isFormField = function() {
		return this.native.isFormField();
	};
	
	this.getFieldName = function() {
		return this.native.getFieldName();
	};
	
}
