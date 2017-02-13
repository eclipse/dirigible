/* globals $ */
/* eslint-env node, dirigible */

var documentLib = require("docs_explorer/lib/document_lib");
var folderLib = require("docs_explorer/lib/folder_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");
var upload = require('net/http/upload');
var user = require('net/http/user');
var xss = require('utils/xss');
var repository = require('platform/repository');

const separator = "/";

requestHandler.handleRequest({
	handlers : {
		POST: handlePost,
		GET: handleGet
	},
});

function handlePost(){
	if (upload.isMultipartContent()) {
		checkFolder("/iam");
		checkFolder("/iam/users");
		checkFolder("/iam/users/" + user.getName());
		var path = "/iam/users/" + user.getName();
		var documents = upload.parseRequest();
		var result = [];
		documents.forEach(function(document) {
			var folder = folderLib.getFolder(path);
			document.name = "avatar.png";
			result.push(documentLib.uploadDocument(folder, document));
		});
	} else {
		printError(response.BAD_REQUEST, 4, "The request's content must be 'multipart'");
	}
	
	response.println(JSON.stringify(result));
}

function handleGet(){
	var pathPrefix = "/profile/avatar.js/";
	var userName = xss.escapeSql(request.getInfo().pathInfo);
	userName = userName.substring(pathPrefix.length);
	if (userName === "") {
		userName = user.getName();
	}
	var documentPath = "/iam/users/" + userName + "/avatar.png";
	try {
		var document = documentLib.getDocument(documentPath);
		var contentStream = documentLib.getDocumentStream(document);
		var contentType = contentStream.getInternalObject().getMimeType();
		response.setContentType(contentType);
		response.writeStream(contentStream.getStream());
	} catch(e) {
		// asuming the avatar does not exist
		var defaultAvatar = repository.getResource("/db/dirigible/registry/public/WebContent/profile/default.png");
		response.setContentType("image/png");
		response.writeOutput(defaultAvatar.getContent());
	}
	response.flush();
	response.close();
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

function checkFolder(path) {
	var upper = null;
	var parent = path.substring(0, path.lastIndexOf(separator));
	if (parent.length === 0) {
		parent = separator;
	}
	var name = path.substring(path.lastIndexOf(separator) + 1);
	try {
		folderLib.getFolder(path);
	} catch(e) {
		upper = folderLib.getFolder(parent);
		folderLib.createFolder(upper, name);
	}
}


