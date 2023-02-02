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
/**
 * HTTP API Upload
 *
 */
const streams = require("io/streams");
const bytes = require("io/bytes");

exports.isMultipartContent = function() {
	return org.eclipse.dirigible.components.api.http.HttpUploadFacade.isMultipartContent();
};

exports.parseRequest = function() {
	const fileItems = new FileItems();
	//fileItems.native = org.eclipse.dirigible.components.api.http.HttpUploadFacade.parseRequest();
	fileItems.native = __context.get("files");
	return fileItems;
};

/**
 * FileItems object
 */
function FileItems() {

	this.get = function(index) {
		const fileItem = new FileItem();
		fileItem.native = this.native.get(index);
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
		const inputStream = new streams.InputStream();
		inputStream.native = this.native.getInputStream();
		return inputStream;
	};

	this.getContentType = function() {
		return this.native.getContentType();
	};

	this.getName = function() {
		return this.native.getOriginalFilename();
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

	this.getHeaders = function() {
		const headers = new Headers();
		headers.native = this.native.getHeaders();
		return headers;
	};

}

/**
 * Headers object
 */
function Headers() {

	this.getHeaderNames = function() {
		return org.eclipse.dirigible.components.api.http.HttpUploadFacade.headerNamesToList(this.native.getHeaderNames());
	};

	this.getHeader = function(headerName) {
		return this.native.getHeader(headerName);
	}
}

