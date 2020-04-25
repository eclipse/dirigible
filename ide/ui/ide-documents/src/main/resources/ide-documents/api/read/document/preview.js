/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var request = require("http/v4/request");
var response = require("http/v4/response");
var documentLib = require("ide-documents/api/lib/document");
var requestHandler = require("ide-documents/api/lib/request-handler");

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
	var contentStream = documentLib.getDocumentStream(document);
	var contentType = contentStream.getMimeType();

	response.setContentType(contentType);
	response.write(contentStream.getStream().readBytes());
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
