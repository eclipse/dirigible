/* globals $ */
/* eslint-env node, dirigible */

var folderLib = require("ext_registry_cmis_explorer/folder_lib");
var cmisObjectLib = require("ext_registry_cmis_explorer/cmis_object_lib");

var request = require("net/http/request");
var response = require("net/http/response");

handleRequest();

function handleRequest() {
	
	response.setContentType("application/json");
	response.setCharacterEncoding("UTF-8");
	
	var method = request.getMethod();
	method = method.toUpperCase();
	try{
		executeMethod(method);
	} catch(e){
		console.error(e);
		console.trace(e);
		var index = e.message.indexOf(':') + 1;
		var shortMessage = e.message.substring(index, e.message.length);
		printError(response.INTERNAL_SERVER_ERROR, 5, shortMessage);
	}
	// flush and close the response
	response.flush();
	response.close();
}

function executeMethod(method){
	if (method === 'POST') {
		handlePost();
	} else if (method === 'GET') {
		handleGet();
	} else if (method === 'PUT') {
		handlePut(); 
	} else if (method === 'DELETE') {
		handleDelete();
	} else {
		printError(response.BAD_REQUEST, 4, "Invalid HTTP Method", method);
	}
}

function handlePost(){
	var body = getJsonRequestBody();
	if (body.parentFolderId === null || body.parentFolderId == undefined || body.name === null || body.name == undefined){
		printError(response.BAD_REQUEST, 4, "Request body must contain 'parentFolderId' and 'name'");
		return;
	}
	var result = folderLib.createFolder(body.parentFolderId, body.name);
	response.setStatus(response.CREATED);
	response.print(JSON.stringify(result));
}

function handleGet(){
	var folderId = request.getParameter('id');
	var result = folderLib.getFolder(folderId);
	response.setStatus(response.OK);
	response.print(JSON.stringify(result));
}

function handlePut(){
	var body = getJsonRequestBody();
	if (body.id === null || body.id == undefined || body.name === null || body.name == undefined){
		printError(response.BAD_REQUEST, 4, "Request body must contain 'id' and 'name'");
		return;
	}
	
	var result = cmisObjectLib.renameObject(body.id, body.name);
	response.setStatus(response.OK);
	response.print(JSON.stringify(result));
}

function handleDelete(){
	var folderIds = getJsonRequestBody();
	for (var i in folderIds){
		cmisObjectLib.deleteObject(folderIds[i]);
	}
	response.setStatus(response.NO_CONTENT);
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
