/* globals $ */
/* eslint-env node, dirigible */
var request = require("net/http/request");
var response = require("net/http/response");
var upload = require('net/http/upload');
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var zipLib = require("docs_explorer/lib/zip_lib");

requestHandler.handleRequest({
	handlers : {
		POST: handlePost
	}
});

function handlePost(){
	if (upload.isMultipartContent()) {
		var path = request.getParameter('path');
		if (path) { path = unescapePath(path); }
		var documents = upload.parseRequest();
		var result = [];
		documents.forEach(function(zip) {
			result.push(zipLib.unpackZip(path, zip));
		});
	} else {
		printError(response.BAD_REQUEST, 4, "The request's content must be 'multipart'");
	}
	
	response.println(JSON.stringify(result));
}

function unescapePath(path){
	return path.replace(/\\/g, '');
}


function printError(httpCode, errCode, errMessage, errContext) {
    var body = {'err': {'code': errCode, 'message': errMessage}};
    response.setStatus(httpCode);
    response.setHeader("Content-Type", "application/json");
    response.print(JSON.stringify(body));
    console.error(JSON.stringify(body));
    if (errContext) {
    	console.error(JSON.stringify(errContext));
    }
}
