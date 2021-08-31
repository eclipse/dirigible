/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let rs = require("http/v4/rs");
let user = require("security/v4/user");
let registry = require("platform/v4/registry");
let streams = require("io/v4/streams");
let upload = require('http/v4/upload');

let zipLib = require("ide-documents/api/lib/zip");
let folderLib = require("ide-documents/api/lib/folder");
let documentLib = require("ide-documents/api/lib/document");
let cmisObjectLib = require("ide-documents/api/lib/object");
let imageLib = require("ide-documents/api/lib/image");

let contentTypeHandler = require("ide-documents/utils/content-type-handler");
let {replaceAll, unescapePath, getNameFromPath} = require("ide-documents/utils/string");

rs.service()
    .resource("")
        .get(function(ctx, request, response) {
            let path = ctx.queryParameters.path || "/";
            path = unescapePath(path);
            let folder = folderLib.getFolderOrRoot(path);
            let result = folderLib.readFolder(folder);
            filterByAccessDefinitions(result);
            response.println(JSON.stringify(result));
        })
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
		.post(function(ctx, request, response) {
			if (!upload.isMultipartContent()) {
				throw new Error("The request's content must be 'multipart'");
			}
			let path = ctx.queryParameters.path || "/";
            path = unescapePath(path);
			let documents = upload.parseRequest();
			let result = [];
			let overwrite = ctx.queryParameters.overwrite || false;
			for (let i = 0 ; i < documents.size(); i ++) {
				let folder = folderLib.getFolder(path);
				if (overwrite){
					result.push(documentLib.uploadDocumentOverwrite(folder, documents.get(i)));
				} else {
					result.push(documentLib.uploadDocument(folder, documents.get(i)));		
				}
			}
			response.println(JSON.stringify(result));
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
		.put(function(ctx, request, response) {
			let body = request.getJSON();
			if (!(body.path && body.name)){
				throw new Error("Request body must contain 'path' and 'name'");
			}
			let object = cmisObjectLib.getObject(body.path);
			cmisObjectLib.renameObject(object, body.name);
			response.setStatus(response.OK);
			response.print(JSON.stringify(body.name));
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
		.delete(function(ctx, request, response) {
			let forceDelete = ctx.queryParameters.force;
			let objects = request.getJSON();
			for (let i in objects) {
				let object = cmisObjectLib.getObject(objects[i]);
				let isFolder = object.getType().getId() === 'cmis:folder';
				if (isFolder && forceDelete === 'true') {
					folderLib.deleteTree(object);
				} else {
					cmisObjectLib.deleteObject(object);
				}
			}
			response.setStatus(response.NO_CONTENT);
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
	.resource("folder")
		.post(function(ctx, request, response) {
			let body = request.getJSON();
			if (!(body.parentFolder && body.name)){
				throw new Error("Request body must contain 'parentFolder' and 'name'");
			}
			let folder = folderLib.getFolderOrRoot(body.parentFolder);
			let result = folderLib.createFolder(folder, body.name);
			response.setStatus(response.CREATED);
			response.print(JSON.stringify(result));
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
	.resource("zip")
		.get(function(ctx, request, response) {
			let path = ctx.queryParameters.path;
			if (!path){
				throw new Error("Query parameter 'path' must be provided.");
			}
            path = unescapePath(path);
			let name = getNameFromPath(path);
			let outputStream = response.getOutputStream();
			response.setContentType("application/zip");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + name +".zip\"");
			zipLib.makeZip(path, outputStream);
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
		.post(function(ctx, request, response) {
			if (!upload.isMultipartContent()) {
				throw new Error("The request's content must be 'multipart'");
			}
			let path = ctx.queryParameters.path || "/";
            path = unescapePath(path);
			let documents = upload.parseRequest();
			let result = [];
			for (let i = 0; i < documents.size(); i ++){
				result.push(zipLib.unpackZip(path, documents.get(i)));
			}
			response.println(JSON.stringify(result));
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
	.resource("image")
		.post(function(ctx, request, response) {
			if (!upload.isMultipartContent()) {
				throw new Error("The request's content must be 'multipart'");
			}
			let path = ctx.queryParameters.path || "/";
            path = unescapePath(path);
			let documents = upload.parseRequest();
			let result = [];
			let width = ctx.queryParameters.width;
			let height = ctx.queryParameters.height;

			for (let i = 0; i < documents.size(); i ++) {
				let folder = folderLib.getFolder(path);
				let name = documents.get(i).getName();
				if (width && height && name){
					result.push(imageLib.uploadImageWithResize(folder, name, documents.get(i), parseInt(width), parseInt(height)));
				} else {
					result.push(documentLib.uploadDocument(folder, documents.get(i)));
				}
			}

			response.println(JSON.stringify(result));
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
	.resource("preview")
		.get(function(ctx, request, response) {
			let path = request.getParameter('path');
			if (!path) {
				throw new Error("Query parameter 'path' must be provided.");
			}
			path = unescapePath(path);
			let document = documentLib.getDocument(path);
			let contentStream = documentLib.getDocumentStream(document);
			let contentType = contentStream.getMimeType();

			response.setContentType(contentType);
			response.write(contentStream.getStream().readBytes());
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
	.resource("download")
		.get(function(ctx, request, response) {
			let path = request.getParameter('path');
			if (!path) {
				throw new Error("Query parameter 'path' must be provided.");
			}
			path = unescapePath(path);
			let document = documentLib.getDocument(path);
			let nameAndStream = documentLib.getDocNameAndStream(document);
			let name = nameAndStream[0];
			let contentStream = nameAndStream[1];
			let contentType = contentStream.getMimeType();

			contentType = contentTypeHandler.getContentTypeBeforeDownload(name, contentType);

			response.setContentType(contentType);
			response.addHeader("Content-Disposition", "attachment;filename=\"" + name + "\"");
			streams.copy(contentStream.getStream(), response.getOutputStream());
		})
		.catch(function(ctx, error, request, response) {
			printError(response, response.BAD_REQUEST, 4, error.message);
		})
.execute();

function printError(response, httpCode, errCode, errMessage) {
	var body = {
		err: {
			code: errCode,
			message: errMessage
		}
	};
    console.error(JSON.stringify(body));
    response.setStatus(httpCode);
    response.println(JSON.stringify(body));
}

function filterByAccessDefinitions(folder) {
	let accessDefinitions = JSON.parse(registry.getText("ide-documents/security/roles.access"));
	folder.children = folder.children.filter(e => {
		let path = replaceAll((folder.path + "/" + e.name), "//", "/");
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
		constraintPath = replaceAll(constraintPath, "//", "/");
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
					if (!user.isInRole(roles[j])) {
						return false;
					}
				}
			}
		}
	}
	return true;
}