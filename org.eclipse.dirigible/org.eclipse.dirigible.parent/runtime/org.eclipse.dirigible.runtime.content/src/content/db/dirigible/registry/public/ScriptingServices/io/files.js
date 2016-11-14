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

	this.getInternalObject = function() {
		return this.internalFile;
	};

	this.exists = function() {
		return this.internalFile.exists();
	};

	this.isExecutable = function() {
		return this.internalFile.canExecute();
	};

	this.isReadable = function() {
		return this.internalFile.canRead();
	};

	this.isWritable = function() {
		return this.internalFile.canWrite();
	};

	this.getCanonicalPath = function() {
		return this.internalFile.getCanonicalPath();
	};

	this.getPath = function() {
		return this.internalFile.getPath();
	};

	this.getName = function() {
		return this.internalFile.getName();
	};

	this.getParent = function() {
		return this.internalFile.getParent();
	};

	this.getParentFile = function() {
	    if (this.internalFile.getParentFile()) {
			return new File(this.internalFile.getParentFile());
		}
		return null;
	}

	this.isDirectory = function() {
		return this.internalFile.isDirectory();
	};

	this.isFile = function() {
		return this.internalFile.isFile();
	};

	this.isHidden = function() {
		return this.internalFile.isHidden();
	};

	this.lastModified = function() {
		return new Date(this.internalFile.lastModified());
	};

	this.length = function() {
		return this.internalFile.length();
	};

	this.list = function() {
		var list = [];
		var internalList = this.internalFile.list();
		for (i = 0; i < internalList.length; i++) {
			list.push(internalList[i]);
		}
		return list;
	};

	this.listRoots = function() {
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
	};

	this.filter = function(pattern) {
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
	};

	this.setExecutable = function(executable) {
		return this.internalFile.setExecutable(executable, false);
	};

	this.setReadable = function(readable) {
		return this.internalFile.setReadable(readable, false);
	};

	this.setWritable = function(writable) {
		return this.internalFile.setWritable(writable, false);
	};
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
	return String.fromCharCode.apply(String, streams.toJavaScriptBytes(bytes));
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

