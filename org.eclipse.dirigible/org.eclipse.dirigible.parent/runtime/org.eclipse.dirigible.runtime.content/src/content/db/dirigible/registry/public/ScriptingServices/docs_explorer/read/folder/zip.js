/* globals $ */
/* eslint-env node, dirigible */

var folderLib = require("docs_explorer/lib/folder_lib");
var zipLib = require("docs_explorer/lib/zip_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");

var streams = require('io/streams');

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
	var result = zipLib.makeZip(path);
	
	response.setContentType("application/zip");
	response.addHeader("Content-Disposition", "attachment;filename=\"" + name +".zip\"");
	response.writeStream(streams.createByteArrayInputStream(result));
	
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
