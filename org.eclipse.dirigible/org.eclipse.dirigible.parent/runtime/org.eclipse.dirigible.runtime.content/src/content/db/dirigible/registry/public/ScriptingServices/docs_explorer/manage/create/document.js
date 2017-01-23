/* globals $ */
/* eslint-env node, dirigible */

var documentLib = require("docs_explorer/lib/document_lib");
var folderLib = require("docs_explorer/lib/folder_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");
var upload = require('net/http/upload');

requestHandler.handleRequest({
	handlers : {
		POST: handlePost
	},
});

function handlePost(){
	if (upload.isMultipartContent()) {
		var path = request.getParameter('path');
		if (path) { path = unescapePath(path); }
		var documents = upload.parseRequest();
		var result = [];
		documents.forEach(function(document) {
			var folder = folderLib.getFolder(path);
			result.push(documentLib.uploadDocument(folder, document));
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
    response.print(JSON.stringify(body));
    console.error(JSON.stringify(body));
    if (errContext !== null) {
    	console.error(JSON.stringify(errContext));
    }
}
