/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

var streams = require("io/streams");

/**
 * Getter for File reference
 */
exports.get = function(path) {
	var internalFile = new java.io.File(path);
	return new File(internalFile);
};

/**
 * File object
 */
function File(internalFile) {
	this.internalFile = internalFile;
	this.getInternalObject = fileGetInternalObject;
	this.exists = fileExists;
	this.isExecutable = fileIsExecutable;
	this.isReadable = fileIsReadable;
	this.isWritable = fileIsWritable;
	this.getCanonicalPath = fileGetCanonicalPath;
	this.getPath = fileGetPath;
	this.getName = fileGetName;
	this.getParent = fileGetParent;
	this.isDirectory = fileIsDirectory;
	this.isFile = fileIsFile;
	this.isHidden = fileIsHidden;
	this.lastModified = fileLastModified;
	this.length = fileLength;
	this.list = fileList;
	this.listRoots = fileListRoots;
	this.filter = fileFilter;
	this.setExecutable = fileSetExecutable;
	this.setReadable = fileSetReadable;
	this.setWritable = fileSetWritable;
}

function fileGetInternalObject() {
	return this.internalFile;
}

function fileExists() {
	return this.internalFile.exists();
}

function fileIsExecutable() {
	return this.internalFile.canExecute();
}

function fileIsReadable() {
	return this.internalFile.canRead();
}

function fileIsWritable() {
	return this.internalFile.canWrite();
}

function fileGetCanonicalPath() {
	return this.internalFile.getCanonicalPath();
}

function fileGetPath() {
	return this.internalFile.getPath();
}

function fileGetName() {
	return this.internalFile.getName();
}

function fileGetParent() {
	return this.internalFile.getParent();
}

function fileGetParentFile() {
    if (this.internalFile.getParentFile()) {
		return new File(this.internalFile.getParentFile());
	}
	return null;
}

function fileIsDirectory() {
	return this.internalFile.isDirectory();
}

function fileIsFile() {
	return this.internalFile.isFile();
}

function fileIsHidden() {
	return this.internalFile.isHidden();
}

function fileLastModified() {
	return new Date(this.internalFile.lastModified());
}

function fileLength() {
	return this.internalFile.length();
}

function fileList() {
	var list = [];
	var internalList = this.internalFile.list();
	for (i = 0; i < internalList.length; i++) {
		list.push(internalList[i]);
	}
	return list;
}

function fileListRoots() {
	var list = [];
	var internalList;
	if (engine === "nashorn") {
		internalList = this.internalFile.class.static.listRoots();
	} else {
		internalList = this.internalFile.listRoots();
	}

	for (i = 0; i < internalList.length; i++) {
		list.push(internalList[i]);
	}
	return list;
}

function fileFilter(pattern) {
	var list = [];
	var filter = new java.io.FilenameFilter() {
		    accept: function(dir, name) {
		        return name.search(pattern) >= 0;
		    }
		};
	var internalList = this.internalFile.list(filter);
	for (i = 0; i < internalList.length; i++) {
		list.push(internalList[i]);
	}
	return list;
}

function fileSetExecutable(executable) {
	return this.internalFile.setExecutable(executable, false);
}

function fileSetReadable(readable) {
	return this.internalFile.setReadable(readable, false);
}

function fileSetWritable(writable) {
	return this.internalFile.setWritable(writable, false);
}

/**
 * Create a new directory by a given path
 */
exports.createDirectory = function(path) {
	var internalPath = java.nio.file.Paths.get(path);
	java.nio.file.Files.createDirectories(internalPath);
};

/**
 * Create a new file by a given path
 */
exports.createFile = function(path) {
	var internalPath = java.nio.file.Paths.get(path);
	if (!exports.get(path).exists()) {
		java.nio.file.Files.createFile(internalPath);
	}
};

/**
 * Copy a source file to a target
 */
exports.copy = function(source, target) {
	var internalSource = java.nio.file.Paths.get(source);
	var internalTarget = java.nio.file.Paths.get(target);
	java.nio.file.Files.copy(internalSource, internalTarget, 
		java.nio.file.StandardCopyOption.REPLACE_EXISTING,
      	java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
};

/**
 * Move a source file to a target
 */
exports.move = function(source, target) {
	var internalSource = java.nio.file.Paths.get(source);
	var internalTarget = java.nio.file.Paths.get(target);
	java.nio.file.Files.move(internalSource, internalTarget, 
		java.nio.file.StandardCopyOption.REPLACE_EXISTING,
      	java.nio.file.StandardCopyOption.ATOMIC_MOVE);
};

/**
 * Delete a file or an empty directory
 */
exports.delete = function(path) {
	var internalPath = java.nio.file.Paths.get(path);
	java.nio.file.Files.deleteIfExists(internalPath);
};

/**
 * Read a text content from a file
 */
exports.readText = function(path) {
	var internalPath = java.nio.file.Paths.get(path);
	var bytes = java.nio.file.Files.readAllBytes(internalPath);
	var result = new java.lang.String(bytes);
	return result;
};

/**
 * Write a text content to a file
 */
exports.writeText = function(path, text) {
	var internalPath = java.nio.file.Paths.get(path);
	var asString = new java.lang.String(text);
	java.nio.file.Files.write(internalPath, asString.getBytes());
};

/**
 * Read a binary content from a file
 */
exports.read = function(path) {
	var internalPath = java.nio.file.Paths.get(path);
	var internalBytes = java.nio.file.Files.readAllBytes(internalPath);
	var bytes = streams.toJavaScriptBytes(internalBytes);
	return bytes;
};

/**
 * Write a binary content to a file
 */
exports.write = function(path, bytes) {
	var internalPath = java.nio.file.Paths.get(path);
	var internalBytes = streams.toJavaBytes(bytes);
	java.nio.file.Files.write(internalPath, internalBytes);
};

