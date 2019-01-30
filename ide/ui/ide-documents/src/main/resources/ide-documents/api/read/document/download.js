/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var request = require("http/v3/request");
var response = require("http/v3/response");
var documentLib = require("ide-documents/api/lib/document");
var requestHandler = require("ide-documents/api/lib/request-handler");
var streams = require("io/v3/streams");
var contentTypeHandler = require("ide-documents/services/content-type-handler");

requestHandler.handleRequest({
	handlers : {
		GET: handleGet
	},
});

function handleGet(){
	var documentPath = request.getParameter('path');
	if (!documentPath){
		printError(response.BAD_REQUEST, 4, "Query parameter 'path' must be provided.");
		return;
	}
	
	documentPath = unescapePath(documentPath);
	var document = documentLib.getDocument(documentPath);
	var nameAndStream = documentLib.getDocNameAndStream(document);
	var name = nameAndStream[0];
	var contentStream = nameAndStream[1];
	var contentType = contentStream.getMimeType();

	contentType = contentTypeHandler.getContentTypeBeforeDownload(name, contentType);

	response.setContentType(contentType);
	response.addHeader("Content-Disposition", "attachment;filename=\"" + name + "\"");
	//response.write(contentStream.getStream().readBytes());
	streams.copy(contentStream.getStream(), response.getOutputStream());
}

function unescapePath(path){
	return path.replace(/\\/g, '');
}

function printError(httpCode, errCode, errMessage, errContext) {
    var body = {'err': {'code': errCode, 'message': errMessage}};
    response.setStatus(httpCode);
    response.print(JSON.stringify(body));
    console.error(JSON.stringify(body));
    if (errContext !== null) {
    	console.error(JSON.stringify(errContext));
    }
}
