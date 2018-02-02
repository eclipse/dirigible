/* globals $ */
/* eslint-env node, dirigible */

var request = require("http/v3/request");
var response = require("http/v3/response");
var folderLib = require("ide-documents/api/lib/folder");
var requestHandler = require("ide-documents/api/lib/request-handler");

requestHandler.handleRequest({
	handlers : {
		POST: handlePost
	}
});

function handlePost(){
	var body = getJsonRequestBody();
	if (!(body.parentFolder && body.name)){
		printError(response.BAD_REQUEST, 4, "Request body must contain 'parentFolder' and 'name'");
		return;
	}
	var folder = folderLib.getFolderOrRoot(body.parentFolder);
	var result = folderLib.createFolder(folder, body.name);
	response.setStatus(response.CREATED);
	response.print(JSON.stringify(result));
}

function getJsonRequestBody(){
	var input = request.getText();
    var requestBody = JSON.parse(input);
    return requestBody;
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
