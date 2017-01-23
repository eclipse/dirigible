/* globals $ */
/* eslint-env node, dirigible */

var folderLib = require("docs_explorer/lib/folder_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");

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
