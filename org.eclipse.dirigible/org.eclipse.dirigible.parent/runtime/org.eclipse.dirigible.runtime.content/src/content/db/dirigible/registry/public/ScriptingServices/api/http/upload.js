/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
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
    var file = {
        "name": fileItem.getName(),
        "data": convertByteAray($.getIOUtils().toByteArray(fileItem.getInputStream())),
        "contentType": getContentType(fileItem.getContentType()),
        "size": fileItem.getSize()
    };
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
