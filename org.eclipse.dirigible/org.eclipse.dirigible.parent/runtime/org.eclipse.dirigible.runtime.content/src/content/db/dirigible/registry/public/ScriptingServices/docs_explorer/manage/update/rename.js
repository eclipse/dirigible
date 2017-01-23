/* globals $ */
/* eslint-env node, dirigible */

var cmisObjectLib = require("docs_explorer/lib/object_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");

var request = require("net/http/request");
var response = require("net/http/response");

requestHandler.handleRequest({
	handlers : {
		PUT: handlePut
	},
});

function handlePut(){
	var body = getJsonRequestBody();
	if (!(body.path && body.name)){
		printError(response.BAD_REQUEST, 4, "Request body must contain 'path' and 'name'");
		return;
	}
	var object = cmisObjectLib.getObject(body.path);
	var result = cmisObjectLib.renameObject(object, body.name);
	response.setStatus(response.OK);
	response.print(JSON.stringify(result));
}

function getJsonRequestBody(){
	var input = request.readInputText();
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
