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
const bytes = require('io/bytes');

/**
 * InputStream object. To be used internally by the API layer
 */
exports.InputStream = function() {

	this.read = function() {
		return org.eclipse.dirigible.components.api.io.StreamsFacade.read(this.native);
	};

	this.readBytes = function() {
		const native = org.eclipse.dirigible.components.api.io.StreamsFacade.readBytes(this.native);
		return bytes.toJavaScriptBytes(native);
	};

	this.readBytesNative = function() {
		return org.eclipse.dirigible.components.api.io.StreamsFacade.readBytes(this.native);
	};

	this.readText = function() {
		return org.eclipse.dirigible.components.api.io.StreamsFacade.readText(this.native);
	};

	this.close = function() {
		org.eclipse.dirigible.components.api.io.StreamsFacade.close(this.native);
	};

	this.isValid = function() {
		return this.native !== null;
	};

};

/**
 * OutputStream object. To be used internally by the API layer
 */
exports.OutputStream = function() {

	this.write = function(byte) {
		org.eclipse.dirigible.components.api.io.StreamsFacade.write(this.native, byte);
	};

	this.writeBytes = function(data) {
		const native = bytes.toJavaBytes(data);
		org.eclipse.dirigible.components.api.io.StreamsFacade.writeBytes(this.native, native);
	};

	this.writeBytesNative = function(data) {
		org.eclipse.dirigible.components.api.io.StreamsFacade.writeBytes(this.native, data);
	};

	this.writeText = function(text) {
		org.eclipse.dirigible.components.api.io.StreamsFacade.writeText(this.native, text);
	};

	this.close = function() {
		org.eclipse.dirigible.components.api.io.StreamsFacade.close(this.native);
	};

	this.getBytes = function() {
		const native = org.eclipse.dirigible.components.api.io.StreamsFacade.getBytes(this.native);
		const data = bytes.toJavaScriptBytes(native);
		return data;
	};

	this.getBytesNative = function() {
		const native = org.eclipse.dirigible.components.api.io.StreamsFacade.getBytes(this.native);
		return native;
	};

	this.getText = function() {
		const value = org.eclipse.dirigible.components.api.io.StreamsFacade.getText(this.native);
		return value;
	};

	this.isValid = function() {
		return this.native !== null;
	};

};

exports.copy = function(input, output) {
	org.eclipse.dirigible.components.api.io.StreamsFacade.copy(input.native, output.native);
};

exports.copyLarge = function(input, output) {
	org.eclipse.dirigible.components.api.io.StreamsFacade.copyLarge(input.native, output.native);
};

/**
 * Get an ByteArrayInputStream for the provided resource
 */
exports.getResourceAsByteArrayInputStream = function(path) {
	const inputStream = new exports.InputStream();
	inputStream.native = org.eclipse.dirigible.components.api.io.StreamsFacade.getResourceAsByteArrayInputStream(path);
	return inputStream;
};

/**
 * Create an ByteArrayInputStream for byte array provided
 */
exports.createByteArrayInputStream = function(data) {
	const inputStream = new exports.InputStream();
	const array = bytes.toJavaBytes(data);
	inputStream.native = org.eclipse.dirigible.components.api.io.StreamsFacade.createByteArrayInputStream(array);
	return inputStream;
};


/**
 * Create a ByteArrayOutputStream
 */
exports.createByteArrayOutputStream = function() {
	const outputStream = new exports.OutputStream();
	outputStream.native = org.eclipse.dirigible.components.api.io.StreamsFacade.createByteArrayOutputStream();
	return outputStream;
};

/**
 * Create an InputStream object by a native InputStream
 */
exports.createInputStream = function(native) {
	const inputStream = new exports.InputStream();
	inputStream.native = native;
	return inputStream;
};

/**
 * Create an OutputStream object by a native OutputStream
 */
exports.createOutputStream = function(native) {
	const outputStream = new exports.OutputStream();
	outputStream.native = native;
	return outputStream;
};
