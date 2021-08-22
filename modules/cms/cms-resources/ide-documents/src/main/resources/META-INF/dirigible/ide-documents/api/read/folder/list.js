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
var repositoryContent = require("platform/v4/registry");

String.prototype.replaceAll = function(find, replace) {
  return this.replace(new RegExp(find, 'g'), replace);
};

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
	folder.children = folder.children.filter(e => {
		let path = (folder.path + "/" + e.name).replaceAll("//", "/");
		if (path.startsWith("/__internal")) {
			return false;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (path.endsWith("/")) {
			path = path.substr(0, path.length - 1);
		}
		return hasAccessPermissions(accessDefinitions.constraints, path);
	});
}

function hasAccessPermissions(constraints, path) {
	for (let i = 0; i < constraints.length; i ++) {
		let method = constraints[i].method;
		let constraintPath = constraints[i].path;
		constraintPath = constraintPath.replaceAll("//", "/");
		if (!constraintPath.startsWith("/")) {
			constraintPath = "/" + constraintPath;
		}
		if (constraintPath.endsWith("/")) {
			constraintPath = constraintPath.substr(0, constraintPath.length - 1);
		}
		if (constraintPath.length === 0 || (path.length >= constraintPath.length && constraintPath.startsWith(path))) {
			if (method !== null && method !== undefined && (method.toUpperCase() === "READ" || method === "*")) {				
				let roles = constraints[i].roles;
				for (let j = 0; j < roles.length; j ++) {
					if (!request.isUserInRole(roles[j])) {
						return false;
					}
				}
			}
		}
	}
	return true;
}