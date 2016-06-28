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

exports.parseRequest = function() {
	var files = [];
	if($.getUploadUtils().isMultipartContent($.getRequest())) {
        var fileItems = $.getUploadUtils().parseRequest($.getRequest());
        for(var i = 0; i < fileItems.size(); i ++){
            var file = createFileEntity(fileItems.get(i));
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

function createFileEntity(fileItem) {
    var file = new HttpFileEntry();
    file.name = fileItem.getName();

	if (engine === "nashorn") {
		file.data = convertByteAray($.getIOUtils().class.static.toByteArray(fileItem.getInputStream()));
	} else {
		file.data = convertByteAray($.getIOUtils().toByteArray(fileItem.getInputStream()));
	}
    
    file.contentType = getContentType(fileItem.getContentType());
    file.size = fileItem.getSize();
    return file;
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
function HttpFileEntry() {
	this.name = "";
	this.data = [];
	this.contentType = "";
	this.size = 0;
}
