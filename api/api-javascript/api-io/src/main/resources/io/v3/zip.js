/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var java = require('core/v3/java');
var streams = require("io/v3/streams");

exports.createZipInputStream = function(inputStream) {
	
	/**
	 * ZipInputStream object
	 */
	var ZipInputStream = function () {
	
		this.getNextEntry = function() {
			var zipEntryInstance = java.invoke(this.uuid, 'getNextEntry', [], true);
			var zipEntry = new ZipEntry();
			zipEntry.uuid = zipEntryInstance.uuid;
			zipEntry.input = this;
			return zipEntry;
		};
		
		this.read = function(zipEntry) {
			var bytes = java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "read", [this.uuid], false);
			return JSON.parse(bytes);
		};
		
		this.readText = function(zipEntry) {
			var text = java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "readText", [this.uuid], false);
			return text;
		};
		
		this.close = function() {
			java.invoke(this.uuid, 'close', [], true);
		};
	
	}
	
	var zipInputStreamInstance = java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "createZipInputStream", [inputStream.uuid], true);
	var zipInputStream = new ZipInputStream();
	zipInputStream.uuid = zipInputStreamInstance.uuid;
	return zipInputStream;
};

exports.createZipOutputStream = function(outputStream) {

	/**
	 * ZipOutputStream object
	 */
	var ZipOutputStream = function() {

		this.createZipEntry = function(name) {
			var zipEntryInstance = java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "createZipEntry", [name], true);
			var zipEntry = new ZipEntry();
			zipEntry.uuid = zipEntryInstance.uuid;
			java.invoke(this.uuid, 'putNextEntry', [zipEntry.uuid], true);
			return zipEntry;
		};

		this.write = function(bytes) {
			java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "write", [this.uuid, JSON.stringify(bytes)], false);
		};
		
		this.writeText = function(text) {
			java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "writeText", [this.uuid, text], false);
		};
		
		this.closeEntry = function() {
			java.invoke(this.uuid, 'closeEntry', [], true);
		};

		this.close = function() {
			java.invoke(this.uuid, 'finish', [], true);
			java.invoke(this.uuid, 'flush', [], true);
			java.invoke(this.uuid, 'close', [], true);
		};

	}

	var zipOutputStreamInstance = java.call("org.eclipse.dirigible.api.v3.io.ZipFacade", "createZipOutputStream", [outputStream.uuid], true);
	var zipOutputStream = new ZipOutputStream();
	zipOutputStream.uuid = zipOutputStreamInstance.uuid;
	return zipOutputStream;
};




/**
 * ZipEntry object
 */
function ZipEntry() {

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', [], false);
	};
	
	this.getSize = function() {
		return java.invoke(this.uuid, 'getSize', [], false);
	};
	
	this.getCompressedSize = function() {
		return java.invoke(this.uuid, 'getCompressedSize', [], false);
	};
	
	this.getTime = function() {
		return java.invoke(this.uuid, 'getTime', [], false);
	};
	
	this.getCrc = function() {
		return java.invoke(this.uuid, 'getCrc', [], false);
	};
	
	this.getComment = function() {
		return java.invoke(this.uuid, 'getComment', [], false);
	};
	
	this.isDirectory = function() {
		return java.invoke(this.uuid, 'isDirectory', [], false);
	};
	
	this.isValid = function() {
		return this.uuid !== null;
	};

}
