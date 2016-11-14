/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax engine */
/* eslint-env node, dirigible */

var streams = require("io/streams");

exports.parseRequest = function(lazy) {
	if (lazy === undefined) {
		lazy = true;
	}
	var files = [];
	if($.getUploadUtils().isMultipartContent($.getRequest())) {
        var fileItems = $.getUploadUtils(). parseRequest($.getRequest());
        for(var i = 0; i < fileItems.size(); i ++){
            var file = createFileEntity(fileItems.get(i), lazy);
            if(file.name){
                files.push(file);
            }
        }
	}
	return files;
};

exports.isMultipartContent = function() {
	return $.getUploadUtils().isMultipartContent($.getRequest());
};

function createFileEntity(fileItem, lazy) {
    var file = new HttpFileEntry(fileItem, lazy);
    file.name = fileItem.getName();

	if (lazy === true) {
		file.internalStream = fileItem.getInputStream();
		file.data = null;
	} else {
		file.data = loadData(fileItem);
		file.internalStream = null;
	}
    
    file.contentType = getContentType(fileItem.getContentType());
    file.size = fileItem.getSize();
    return file;
}

function loadData(fileItem) {
	if (engine === "nashorn") {
		return convertByteAray($.getIOUtils().class.static.toByteArray(fileItem.getInputStream()));
	}
	return convertByteAray($.getIOUtils().toByteArray(fileItem.getInputStream()));
}

function getContentType(contentType){
    return contentType ? contentType : "";
}

function convertByteAray(internalBytes) {
	var bytes = [];
	for (var i=0; i<internalBytes.length; i++) {
		bytes.push(internalBytes[i]);
	}
	return bytes;
}


/**
 * HTTP File Entry object
 */
function HttpFileEntry(fileItem, lazy) {
	this.internalFileItem = fileItem;
	this.name = "";
	this.lazy = lazy;
	this.data = null;
	this.contentType = "";
	this.size = 0;
	this.internalStream = null;
	this.loadData = function() {
		if (this.internalStream && this.internalStream !== null) {
			if (this.data !== null) {
				return this.data; // already loaded
			}
			this.data = loadData(this.internalFileItem); // load once
			return this.data;
		}
		if (this.lazy) {
			throw new Error("The stream element is null in a lazy file item.");
		} else {
			return this.data;
		}
	};

	this.copyData = function(outputStream) {
		var inputStream = new streams.InputStream(this.internalStream);
		streams.copy(inputStream, outputStream);
	};

	this.getInputStream = function() {
		return new streams.InputStream(this.internalStream);
	};
}