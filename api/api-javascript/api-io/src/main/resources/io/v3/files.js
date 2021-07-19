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
var java = require('core/v3/java');
var streams = require("io/v3/streams");

   
exports.exists = function(path){
  	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "exists", [path]);
}
    
exports.isExecutable = function(path) {
   	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isExecutable", [path]);
};

exports.isReadable = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isReadable", [path]);
};

exports.isWritable = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isWritable", [path]);
};

exports.isHidden = function(path) {
   	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isHidden", [path]);
};

exports.isDirectory = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isDirectory", [path]);
};

exports.isFile = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isFile", [path]);
};

exports.isSameFile = function(path1, path2) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "isSameFile", [path1, path2]);
};


exports.getCanonicalPath = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getCanonicalPath", [path]);
};

exports.getName = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getName", [path]);
};

exports.getParentPath = function(path) {
	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getParentPath", [path]);
};

exports.readBytes = function(path){
    var result = java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "readBytes", [path]);
    var bytes = JSON.parse(result);
    return bytes;
}

exports.readText = function(path){
    return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "readText", [path]);
}

exports.writeBytes = function(path, bytes){
    java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "writeBytes", [path, JSON.stringify(bytes)]);
}

exports.writeText = function(path, text){
    java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "writeText", [path, text]);
}

exports.getLastModified = function(path) {
   	return new Date(java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getLastModified", [path]));
};

exports.setLastModified = function(path, time) {
   	java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "setLastModified", [path, time.getMilliseconds()]);
};

exports.getOwner = function(path) {
   	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getOwner", [path]);
};

exports.setOwner = function(path, owner) {
   	java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "setOwner", [path, owner]);
};

exports.getPermissions = function(path) {
   	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "getPermissions", [path]);
};

exports.setPermissions = function(path, permissions) {
   	java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "setPermissions", [path, permissions]);
};

exports.size = function(path) {
   	return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "size", [path]);
};

exports.createFile = function(path) {
   	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createFile", [path]);
};

exports.createDirectory = function(path) {
  	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createDirectory", [path]);
};

exports.copy = function(source, target) {
 	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "copy", [path, source, target]);
};

exports.move = function(source, target) {
	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "move", [path, source, target]);
};

exports.deleteFile = function(path) {
  	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "deleteFile", [path]);
};

exports.deleteDirectory = function(path, forced) {
 	 java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "deleteDirectory", [path, forced]);
};

exports.createTempFile = function(prefix, suffix) {
  	 return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createTempFile", [prefix, suffix]);
};

exports.createTempDirectory = function(prefix) {
 	 return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createTempDirectory", [prefix]);
};

exports.createInputStream = function(path) {
	 var inputStreamInstance = java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createInputStream", [path], true);
	 var inputStream = new streams.InputStream();
	 inputStream.uuid = inputStreamInstance.uuid;
	 return inputStream;
};

exports.createOutputStream = function(path) {
	 var outputStreamInstance = java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "createOutputStream", [path], true);
	 var outputStream = new streams.OutputStream();
	 outputStream.uuid = outputStreamInstance.uuid;
	 return outputStream;
};

exports.traverse = function(path) {
 	 return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "traverse", [path]);
};

exports.list = function(path) {
 	 return java.call("org.eclipse.dirigible.api.v3.io.FilesFacade", "list", [path]);
};
