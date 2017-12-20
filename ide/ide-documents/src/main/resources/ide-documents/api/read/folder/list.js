/* globals $ */
/* eslint-env node, dirigible */

var folderLib = require("ide-documents/api/lib/folder");
var requestHandler = require("ide-documents/api/lib/request-handler");
var request = require("http/v3/request");
var response = require("http/v3/response");

requestHandler.handleRequest({
	handlers : {
		GET: handleGet
	},
});

function handleGet(){
	var path = request.getParameter('path');
	if (path){
		path = unescapePath(path);
	}
	var folder = folderLib.getFolderOrRoot(path);
	var result = folderLib.readFolder(folder);
	response.setStatus(response.OK);
	response.print(JSON.stringify(result));
}

function unescapePath(path){
	return path.replace(/\\/g, '');
}
