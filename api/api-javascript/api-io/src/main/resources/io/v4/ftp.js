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

/**
 * API v4 FTP
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

var streams = require("io/v4/streams");

exports.getClient = function(host, port, username, password) {
	return new FTPClient(host, port, username, password);
};

function FTPClient(host, port, username, password) {

	this.host = host;
	this.port = port;
	this.username = username;
	this.password = password;

	function checkConnection(client) {
		if (!client.isConnected()) {
			client.connect();
		}
	}

	this.isConnected = function() {
		return this.instance && this.instance.isConnected();
	};

	this.connect = function() {
		this.disconnect();
		this.instance = org.eclipse.dirigible.api.v3.io.FTPFacade.connect(this.host, this.port, this.username, this.password);
	};

	this.disconnect = function() {
		if (this.isConnected()) {
			org.eclipse.dirigible.api.v3.io.FTPFacade.disconnect(this.instance);
		}
	};

	this.listFiles = function() {
		checkConnection(this);
		var files = [];
		var internalFiles = this.instance.listFiles();
		for (var i = 0; i < internalFiles.length; i ++) {
			files.push(new FTPFile(internalFiles[i]));
		}
		return files;
	};

	this.printWorkingDirectory = function() {
		checkConnection(this);
		return this.instance.printWorkingDirectory();
	};

	this.changeWorkingDirectory = function(pathname) {
		checkConnection(this);
		return this.instance.changeWorkingDirectory(pathname);
	};

	this.changeToParentDirectory = function() {
		checkConnection(this);
		return this.instance.changeToParentDirectory();
	};

	this.reinitialize = function() {
		checkConnection(this);
		return this.instance.reinitialize();
	};

	this.retrieveFileStream = function(filename) {
		this.connect();
		try {
			var inputStream = new streams.InputStream();
			inputStream.native = this.instance.retrieveFileStream(filename);
			return inputStream;
		} finally {
			this.disconnect();
		}
	};

	this.retrieveFile = function(filename) {
		var inputStream = this.retrieveFileStream(filename);
		if (inputStream.isValid()) {
			return inputStream.readText();
		}
		return null;
	};

	this.retrieveFileBinary = function(filename) {
		var inputStream = this.retrieveFileStream(filename);
		if (inputStream.isValid()) {
			return inputStream.readBytes();
		}
		return null;
	};

	this.storeFile = function(filename, inputStream) {
		this.connect();
		try {
			return this.instance.storeFile(filename, inputStream.native);
		} finally {
			this.disconnect();
		}
	};

	this.deleteFile = function(filename) {
		checkConnection(this);
		return this.instance.deleteFile(filename);
	};
}

function FTPFile(instance) {
	this.instance = instance;

	this.getName = function() {
		return this.instance.getName();
	};

	this.isDirectory = function() {
		return this.instance.isDirectory();
	};

	this.isFile = function() {
		return this.instance.isFile();
	};

	this.isSymbolicLink = function() {
		return this.instance.isSymbolicLink();
	};

    this.isUnknown = function() {
    	return this.instance.isUnknown();
    };

    this.isValid = function() {
    	return this.instance.isValid();
    };

    this.setType = function(type) {
    	this.instance.setType(type);
    };

	this.getType = function() {
		return this.instance.getType();
	};

    this.setName = function(name) {
    	this.instance.setName(name);
    };

    this.getName = function() {
    	return this.instance.getName();
    };

    this.setSize = function(size) {
    	this.instance.setSize(size);
    };

    this.getSize = function() {
    	return this.instance.getSize();
    };

    this.setHardLinkCount = function(links) {
    	this.instance.setHardLinkCount(links);
    };

	this.getHardLinkCount = function() {
		return this.instance.getHardLinkCount();
	};

    this.setGroup = function(group) {
    	this.instance.setGroup(group);
    };

    this.getGroup = function() {
    	return this.instance.getGroup();
    };

    this.setUser = function(user) {
    	this.instance.setUser(user);
    };

    this.getUser = function() {
    	return this.instance.getUser();
    };

    this.setLink = function(link) {
    	this.instance.setLink(link);
    };

    this.getLink = function() {
    	return this.instance.getLink();
    };

    this.setTimestamp = function(date) {
    	this.instance.setTimestamp(date);
    };

    this.getTimestamp = function() {
    	return this.instance.getTimestamp();
    };

    this.setPermission = function(access, permission, value) {
    	this.instance.setPermission(access, permission, value);
    };

    this.hasPermission = function(access, permission) {
    	return this.instance.hasPermission(access, permission);
    };
}