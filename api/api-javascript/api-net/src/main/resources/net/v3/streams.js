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
var rhino_and_nashorn_only = "Net Streams module is supported only with Mozilla Rhino and Nashorn";

/**
 * Read the stream content as a byte array
 */
exports.read = function(inputStream) {
	var internalBytes;
	if (__engine === "nashorn") {
		internalBytes = $.getIOUtils().class.static.toByteArray(inputStream.getInternalObject());
	} else if (__engine === "rhino") {
		internalBytes = $.getIOUtils().toByteArray(inputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
	return exports.toJavaScriptBytes(internalBytes);
};

/**
 * Read the stream to a byte array
 */
exports.write = function(outputStream, bytes) {
	var internalBytes = exports.toJavaBytes(bytes);
	if (__engine === "nashorn") {
		$.getIOUtils().class.static.write(internalBytes, outputStream.getInternalObject());
	} else if (__engine === "rhino") {
		$.getIOUtils().write(internalBytes, outputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
};

/**
 * Read the stream content as a String
 */
exports.readText = function(inputStream) {
	var internalBytes;
	if (__engine === "nashorn") {
		internalBytes = $.IOUtils.class.static.toByteArray(inputStream.getInternalObject());
	} else if (__engine === "rhino") {
		internalBytes = $.IOUtils.toByteArray(inputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
	return String.fromCharCode.apply(String, exports.toJavaScriptBytes(internalBytes));
};

/**
 * Read the stream to a byte array
 */
exports.writeText = function(outputStream, text) {
	if (__engine === "nashorn") {
		$.getIOUtils().class.static.write(text, outputStream.getInternalObject());
	} else if (__engine === "rhino") {
		$.getIOUtils().write(text, outputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
};

/**
 * Copy the input stream content to an output stream
 */
exports.copy = function(inputStream, outputStream) {
	if (__engine === "nashorn") {
		$.getIOUtils().class.static.copy(inputStream.getInternalObject(), outputStream.getInternalObject());
	} else if (__engine === "rhino") {
		$.getIOUtils().copy(inputStream.getInternalObject(), outputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
};

/**
 * Copy the input stream large (>2GB) content to an output stream
 */
exports.copyLarge = function(inputStream, outputStream) {
	if (__engine === "nashorn") {
		$.getIOUtils().class.static.copyLarge(inputStream.getInternalObject(), outputStream.getInternalObject());
	} else if (__engine === "rhino") {
		$.getIOUtils().copyLarge(inputStream.getInternalObject(), outputStream.getInternalObject());
	} else {
		console.error(rhino_and_nashorn_only);
		throw new Error(rhino_and_nashorn_only);
	}
};

/**
 * InputStream object. To be used internally by the API layer
 */
exports.InputStream = function(internalInputStream) {
	this.internalInputStream = internalInputStream;
	this.getInternalObject = inputStreamGetInternalObject;
};

function inputStreamGetInternalObject() {
	return this.internalInputStream;
}

/**
 * OutputStream object. To be used internally by the API layer
 */
exports.OutputStream = function(internalOutputStream) {
	this.internalOutputStream = internalOutputStream;

	this.getInternalObject = function() {
		return this.internalOutputStream;
	};
};

/**
 * Create an ByteArrayInputStream for byte array provided
 */
exports.createByteArrayInputStream = function(bytes) {
	return new exports.ByteArrayInputStream(bytes);
};

/**
 * ByteArrayInputStream object.
 */
exports.ByteArrayInputStream = function(bytes) {
	var internalBytes = exports.toJavaBytes(bytes);

	this.internalInputStream = new java.io.ByteArrayInputStream(internalBytes);

	this.getInternalObject = function() {
		return this.internalInputStream;
	};
};

/**
 * Create a ByteArrayOutputStream
 */
exports.createByteArrayOutputStream = function() {
	return new exports.ByteArrayOutputStream();
};

/**
 * ByteArrayOutputStream object.
 */
exports.ByteArrayOutputStream = function() {
	this.internalOutputStream = new java.io.ByteArrayOutputStream();

	this.getInternalObject = function() {
		return this.internalOutputStream;
	};

	this.getBytes = function() {
		var internalBytes = this.internalOutputStream.toByteArray();
		return exports.toJavaScriptBytes(internalBytes);
	};

	this.getText = function() {
		var bytes = this.getBytes();
		var text = String.fromCharCode.apply(String, bytes);
		return text;
	};
};

/**
 * Convert the native JavaScript byte array to Java one. To be used internally by the API layer
 */
exports.toJavaBytes = function(bytes) {
	var internalBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, bytes.length);
	for (var i=0; i<bytes.length; i++) {
		internalBytes[i] = bytes[i];
	}
	return internalBytes;
};

/**
 * Convert the Java byte array to a native JavaScript one. To be used internally by the API layer
 */
exports.toJavaScriptBytes = function(internalBytes) {
	var bytes = [];
	for (var i=0; i<internalBytes.length; i++) {
		bytes.push(internalBytes[i]);
	}
	return bytes;
};

/**
 * Converts a text to a byte array
 */
exports.textToByteArray = function(text) {
	var internalString = new java.lang.String(text);
	var internalBytes = internalString.getBytes();
	return exports.toJavaScriptBytes(internalBytes);
};

/**
 * Converts a text to a byte array
 */
exports.byteArrayToText = function(bytes) {
	var internalBytes = exports.toJavaBytes(bytes);
	return String.fromCharCode.apply(String, exports.toJavaScriptBytes(internalBytes));
};