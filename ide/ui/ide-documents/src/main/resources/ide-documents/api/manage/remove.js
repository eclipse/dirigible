/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var request = require("http/v3/request");
var response = require("http/v3/response");
var cmisObjectLib = require("ide-documents/api/lib/object");
var folderLib = require("ide-documents/api/lib/folder");
var requestHandler = require("ide-documents/api/lib/request-handler");

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
		var isFolder = object.getType().getId() === 'cmis:folder';
		if(isFolder && forceDelete === 'true'){
			folderLib.deleteTree(object);
		} else {
			cmisObjectLib.deleteObject(object);
		}
	}
	response.setStatus(response.NO_CONTENT);
}

function getJsonRequestBody(){
	var input = request.getText();
    var requestBody = JSON.parse(input);
    return requestBody;
}
