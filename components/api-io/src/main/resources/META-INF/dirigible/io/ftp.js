/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 FTP
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */
const bytes = require("io/bytes");
const streams = require("io/streams");

/**
 * Returns a FTP Client
 *
 * @param {host} the ftp host
 * @param {port} the ftp port
 * @param {userName} the ftp user
 * @param {password} the ftp user's password
 * @return {FTPClient} the FTP Client
 */
exports.getClient = function(host, port, userName, password) {
	const manager = new FTPClientManager(host, port, userName, password);
	return new FTPClient(manager);
};

/**
 * Internal FTP Client Manager
 *
 * @private
 */
function FTPClientManager(host, port, userName, password) {
	this.host = host;
	this.port = port;
	this.userName = userName;
	this.password = password;

	this.getCurrentFolder = function() {
		checkConnection(this);
		return this.instance.printWorkingDirectory();
	};

	this.setCurrentFolder = function(path, folderName) {
		checkConnection(this);
		return this.instance.changeWorkingDirectory(this.getFullPath(path, folderName));
	};

	this.list = function() {
		checkConnection(this);
		return this.instance.listFiles();
	};

	this.getFileStream = function(path, fileName) {
		try {
			checkConnection(this);
			const inputStream = new streams.InputStream();
			inputStream.native = this.instance.retrieveFileStream(this.getFullPath(path, fileName));
			return inputStream;
		} finally {
			disconnect(this);
		}
	};

	this.createFile = function(path, fileName, inputStream) {
		try {
			checkConnection(this);
			return this.instance.storeFile(this.getFullPath(path, fileName), inputStream.native);
		} finally {
			disconnect(this);
		}
	};

	this.appendFile = function(path, fileName, inputStream) {
		try {
			checkConnection(this);
			return this.instance.appendFile(this.getFullPath(path, fileName), inputStream.native);
		} finally {
			disconnect(this);
		}
	};

	this.deleteFile = function(path, fileName) {
		try {
			connect(this);
			return this.instance.deleteFile(this.getFullPath(path, fileName));
		} finally {
			disconnect(this);
		}
	};

	this.createFolder = function(path, folderName) {
		checkConnection(this);
		return this.instance.makeDirectory(this.getFullPath(path, folderName));
	};

	this.deleteFolder = function(path, folderName) {
		checkConnection(this);
		return this.instance.removeDirectory(this.getFullPath(path, folderName));
	};

	this.getFullPath = function(path, fileName) {
		if (path && path.length > 0 && fileName && fileName.length > 0) {
			return path.endsWith("/") ? path + fileName : path + "/" + fileName;
		} else if (path && !fileName) {
			return path;
		}
		return fileName;
	};

	this.close = function() {
		disconnect(this);
	};

	function connect(context) {
		disconnect(context);
		context.instance = org.eclipse.dirigible.components.api.io.FTPFacade.connect(context.host, context.port, context.userName, context.password);
	}

	function disconnect(context) {
		if (isConnected(context)) {
			org.eclipse.dirigible.components.api.io.FTPFacade.disconnect(context.instance);
		}
	}

	function isConnected(context) {
		return context.instance && context.instance.isConnected();
	}

	function checkConnection(context) {
		if (!isConnected(context)) {
			connect(context);
		}
	}
}

/**
 * FTP Client
 */
function FTPClient(manager) {
	this.manager = manager;

	/**
	 * Returns the root folder
	 *
	 * @return {FTPFolder} the root folder
	 */
	this.getRootFolder = function() {
		return new FTPFolder(this.manager, "/", "/");
	};

	/**
	 * Returns the content of the file as an Input Stream
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @return {InputStream} the file content as an input stream
	 */
	this.getFile = function(path, fileName) {
		return this.manager.getFileStream(path, fileName);
	};

	/**
	 * Returns the content of the file as Byte Array
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @return {Array} the file content as byte array
	 */
	this.getFileBinary = function(path, fileName) {
		const inputStream = this.getFile(path, fileName);
		return inputStream.isValid() ? inputStream.readBytes() : null;
	};

	/**
	 * Returns the content of the file as String
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @return {String} the file content as string
	 */
	this.getFileText = function(path, fileName) {
		const inputStream = this.getFile(path, fileName);
		return inputStream.isValid() ? inputStream.readText() : null;
	};

	/**
	 * Returns the folder
	 *
	 * @param {path} the path to the folder
	 * @param {folderName} the name of the folder
	 * @return {FTPFolder} the folder
	 */
	this.getFolder = function(path, folderName) {
		const exists = this.manager.setCurrentFolder(path, folderName);
		return exists ? new FTPFolder(this.manager, path, folderName) : null;
	};

	/**
	 * Create file from input stream
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {inputStream} the input stream
	 * @return {Boolean} true if the file was created successfully
	 */
	this.createFile = function(path, fileName, inputStream) {
		return this.manager.createFile(path, fileName, inputStream);
	};

	/**
	 * Create file from byte array
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {bytes} the bytes
	 * @return {Boolean} true if the file was created successfully
	 */
	this.createFileBinary = function(path, fileName, bytes) {
		const inputStream = streams.createByteArrayInputStream(bytes);
		return this.createFile(path, fileName, inputStream);
	};

	/**
	 * Create file from text
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {text} the text
	 * @return {Boolean} true if the file was created successfully
	 */
	this.createFileText = function(path, fileName, text) {
		const inputStream = streams.createByteArrayInputStream(bytes.textToByteArray(text));
		return this.createFile(path, fileName, inputStream);
	};

	/**
	 * Append input stream to file
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {inputStream} the input stream
	 * @return {Boolean} true if the file was created successfully
	 */
	this.appendFile = function(path, fileName, inputStream) {
		return this.manager.appendFile(path, fileName, inputStream);
	};

	/**
	 * Append byte array to file
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {bytes} the bytes
	 * @return {Boolean} true if the file was created successfully
	 */
	this.appendFileBinary = function(path, fileName, bytes) {
		const inputStream = streams.createByteArrayInputStream(bytes);
		return this.appendFile(path, fileName, inputStream);
	};

	/**
	 * Append text to file
	 *
	 * @param {path} the path to the file
	 * @param {fileName} the name of the file
	 * @param {text} the text
	 * @return {Boolean} true if the file was created successfully
	 */
	this.appendFileText = function(path, fileName, text) {
		const inputStream = streams.createByteArrayInputStream(bytes.textToByteArray(text));
		return this.appendFile(path, fileName, inputStream);
	};

	this.createFolder = function(path, folderName) {
		return this.manager.createFolder(path, folderName);
	};

	this.deleteFile = function(path, fileName) {
		return this.manager.deleteFile(path, fileName);
	};

	this.deleteFolder = function(path, folderName) {
		return this.manager.deleteFolder(path, folderName);
	};

	/**
	 * Close the FTP Client
	 */
	this.close = function() {
		this.manager.close();
	};
}

function FTPObject(manager, instance, path, name) {
	this.manager = manager;
	this.instance = instance;
	this.path = path;
	this.name = name;

	this.getPath = function() {
		return this.path;
	};

	this.getName = function() {
		return this.name;
	};

	this.isFile = function() {
		return this.instance.isFile();
	};

	this.isFolder = function() {
		return this.instance.isDirectory();
	};

	this.getFile = function() {
		if (this.isFile()) {
			return new FTPFile(this.manager, this.instance, this.path, this.name);
		}
		return null;
	};

	this.getFolder = function() {
		if (this.isFolder()) {
			return new FTPFolder(this.manager, this.path, this.name);
		}
		return null;
	};
}

function FTPFolder(manager, path, name) {
	this.client = new FTPClient(manager);
	this.manager = manager;
	this.path = path;
	this.name = name;

	this.getPath = function() {
		return this.path;
	};

	this.getName = function() {
		return this.name;
	};

	this.getFile = function(fileName) {
		const files = this.listFiles();
		for (let i = 0; i < files.length; i ++) {
			if (files[i].getName() === fileName) {
				return files[i];
			}
		}
		return null;
	};

	this.getFolder = function(folderName) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.getFolder(folderPath, folderName);
	};

	this.list = function() {
		let objects = [];
		const internalObjects = this.manager.list();
		for (let i = 0; i < internalObjects.length; i ++) {
			objects.push(new FTPObject(this.manager, internalObjects[i], this.path, internalObjects[i].getName()));
		}
		return objects;
	};

	this.listFiles = function() {
		const files = [];
		this.manager.setCurrentFolder(this.path, this.name);
		const internalObjects = this.manager.list();
		for (let i = 0; i < internalObjects.length; i ++) {
			if (internalObjects[i].isFile()) {
				files.push(new FTPFile(this.manager, internalObjects[i], this.path, internalObjects[i].getName()));
			}
		}
		return files;
	};

	this.listFolders = function() {
		const folders = [];
		this.manager.setCurrentFolder(this.path, this.name);
		const internalObjects = this.manager.list();
		for (var i = 0; i < internalObjects.length; i ++) {
			if (internalObjects[i].isDirectory()) {
				folders.push(new FTPFolder(this.manager, this.path, internalObjects[i].getName()));
			}
		}
		return folders;
	};

	this.createFile = function(fileName, inputStream) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.createFile(folderPath, fileName, inputStream);
	};

	this.createFileBinary = function(fileName, bytes) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.createFileBinary(folderPath, fileName, bytes);
	};

	this.createFileText = function(fileName, text) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.createFileText(folderPath, fileName, text);
	};

	this.createFolder = function(folderName) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.createFolder(folderPath, folderName);
	};

	this.delete = function() {
		return this.client.deleteFolder(this.path, this.name);
	};

	this.deleteFile = function(fileName) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.deleteFile(folderPath, fileName);
	};

	this.deleteFolder = function(folderName) {
		const folderPath = this.manager.getFullPath(this.path, this.name);
		return this.client.deleteFolder(folderPath, folderName);
	};
}

function FTPFile(manager, instance, path, name) {
	this.client = new FTPClient(manager);
	this.instance = instance;
	this.path = path;
	this.name = name;

	this.getPath = function() {
		return this.path;
	};

	this.getName = function() {
		return this.name;
	};

	this.getContent = function() {
		return this.client.getFile(this.path, this.name);
	};

	this.getContentBinary = function() {
		return this.client.getFileBinary(this.path, this.name);
	};

	this.getContentText = function() {
		return this.client.getFileText(this.path, this.name);
	};

	this.setContent = function(inputStream) {
		return this.client.createFile(this.path, this.name, inputStream);
	};

	this.setContentBinary = function(bytes) {
		return this.client.createFileBinary(this.path, this.name, bytes);
	};

	this.setContentText = function(text) {
		return this.client.createFileText(this.path, this.name, text);
	};

	this.appendContent = function(inputStream) {
		return this.client.appendFile(this.path, this.name, inputStream);
	};

	this.appendContentBinary = function(bytes) {
		return this.client.appendFileBinary(this.path, this.name, bytes);
	};

	this.appendContentText = function(text) {
		return this.client.appendFileText(this.path, this.name, text);
	};

	this.delete = function() {
		return this.client.deleteFile(this.path, this.name);
	};
}
