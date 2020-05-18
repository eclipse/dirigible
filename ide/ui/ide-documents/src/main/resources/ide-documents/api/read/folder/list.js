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
var repositoryContent = require("repository/v4/content");

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
	filterByAccessDefinitions(result);
	response.setStatus(response.OK);
	response.print(JSON.stringify(result));
}

function unescapePath(path){
	return path.replace(/\\/g, '');
}

function filterByAccessDefinitions(folder) {
	let accessDefinitions = JSON.parse(repositoryContent.getText("ide-documents/security/roles.access"));
	folder.children = folder.children.filter(e => hasAccessPermissions(accessDefinitions.constraints, e.id))
}

function hasAccessPermissions(constraints, path) {
	for (let i = 0; i < constraints.length; i ++) {
		let method = constraints[i].method;
		if (constraints[i].path.startsWith(path) && (method.toUpperCase() === "READ" || method === "*")) {
			let roles = constraints[i].roles;
			for (let j = 0; j < roles.length; j ++) {
				if (!request.isUserInRole(roles[i])) {
					return false;
				}
			}
		}
	}
	return true;
}