/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var folderLib = require("ide-documents/api/lib/folder");
var requestHandler = require("ide-documents/api/lib/request-handler");
var request = require("http/v4/request");
var response = require("http/v4/response");

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
