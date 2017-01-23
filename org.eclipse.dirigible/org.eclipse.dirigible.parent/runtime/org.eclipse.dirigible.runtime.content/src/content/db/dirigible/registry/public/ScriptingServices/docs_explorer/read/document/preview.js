/* globals $ */
/* eslint-env node, dirigible */

var documentLib = require("docs_explorer/lib/document_lib");

var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");

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
	var contentType = contentStream.getInternalObject().getMimeType();
	
	response.setContentType(contentType);
	response.writeStream(contentStream.getStream());
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
