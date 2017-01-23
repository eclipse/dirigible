/* globals $ */
/* eslint-env node, dirigible */

var cmisObjectLib = require("docs_explorer/lib/object_lib");
var folderLib = require("docs_explorer/lib/folder_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");

var request = require("net/http/request");
var response = require("net/http/response");

requestHandler.handleRequest({
	handlers : {
		DELETE: handleDelete
	},
});

function handleDelete(){
	var forceDelete = request.getParameter('force');
	var objects = getJsonRequestBody();
	for (var i in objects){
		var object = cmisObjectLib.getObject(objects[i]);
		var isFolder = object.getInternalObject().getType().getId() === 'cmis:folder';
		if(isFolder && forceDelete === 'true'){
			folderLib.deleteTree(object);
		} else {
			cmisObjectLib.deleteObject(object);
		}
	}
	response.setStatus(response.NO_CONTENT);
}

function getJsonRequestBody(){
	var input = request.readInputText();
    var requestBody = JSON.parse(input);
    return requestBody;
}
