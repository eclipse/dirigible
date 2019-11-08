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
var request = require("http/v4/request");
var response = require("http/v4/response");
var streams = require('io/v4/streams');
var folderLib = require("ide-documents/api/lib/folder");
var zipLib = require("ide-documents/api/lib/zip");
var requestHandler = require("ide-documents/api/lib/request-handler");

requestHandler.handleRequest({
	handlers : {
		GET: handleGet
	},
});

function handleGet(){
	var path = request.getParameter('path');
	if (!path){
		printError(response.BAD_REQUEST, 4, "Query parameter 'path' must be provided.");
		return;
	}
	path = unescapePath(path);
	var name = getNameFromPath(path);
	var outputStream = response.getOutputStream();
	response.setContentType("application/zip");
	response.addHeader("Content-Disposition", "attachment;filename=\"" + name +".zip\"");
	zipLib.makeZip(path, outputStream);

//	console.warn('Result: ' + result);

//	response.write(result);
	// response.writeStream(streams.createByteArrayInputStream(result));
	
	response.setStatus(response.OK);
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

function getNameFromPath(path){
	var splittedFullName = path.split("/");
	var name = splittedFullName[splittedFullName.length - 1];
	if (!name || name.lenght === 0) {
		name = "root";
	}

	return name;
}
