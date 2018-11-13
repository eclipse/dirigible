/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var java = require('core/v3/java');

/**
 * InputStream object. To be used internally by the API layer
 */
exports.InputStream = function() {
	
	this.read = function() {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'read', [this.uuid]);
		return value;
	};
	
	this.readBytes = function() {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'readBytes', [this.uuid]);
		if (value && value != null) {
			return JSON.parse(value);
		}
		return value;
	};
	
	this.readText = function() {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'readText', [this.uuid]);
		return value;
	};
	
	this.close = function() {
		java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'close', [this.uuid]);
	};
	
	this.isValid = function() {
		return this.uuid !== null;
	};
	
};

/**
 * OutputStream object. To be used internally by the API layer
 */
exports.OutputStream = function() {

	this.write = function(byte) {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'write', [this.uuid, byte]);
	};
	
	this.writeBytes = function(bytes) {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'writeBytes', [this.uuid, JSON.stringify(bytes)]);
	};
	
	this.writeText = function(text) {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'writeText', [this.uuid, text]);
	};

	this.close = function() {
		java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'close', [this.uuid]);
	};
	
	this.getBytes = function() {
		var result = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'getBytes', [this.uuid]);
		bytes = JSON.parse(result);
		return bytes;
	};
	
	this.getText = function() {
		var text = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'getText', [this.uuid]);
		return text;
	};
	
	this.isValid = function() {
		return this.uuid !== null;
	};
	
};

exports.copy = function(input, output) {
	java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'copy', [input.uuid, output.uuid]);
};

/**
 * Get an ByteArrayInputStream for the provided resource
 */
exports.getResourceAsByteArrayInputStream = function(path) {
	var inputStream = new exports.InputStream();
	var inputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'getResourceAsByteArrayInputStream', [path], true);
	inputStream.uuid = inputStreamInstance.uuid;
	return inputStream;
};

/**
 * Create an ByteArrayInputStream for byte array provided
 */
exports.createByteArrayInputStream = function(bytes) {
	var inputStreamInstance = {};
	if (bytes) {
		inputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'createByteArrayInputStream', [JSON.stringify(bytes)], true);
	} else {
		inputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'createByteArrayInputStream', [], true);
	}
	var inputStream = new exports.InputStream();
	inputStream.uuid = inputStreamInstance.uuid;
	return inputStream;
};


/**
 * Create a ByteArrayOutputStream
 */
exports.createByteArrayOutputStream = function() {
	var outputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'createByteArrayOutputStream', [], true);
	var outputStream = new exports.OutputStream();
	outputStream.uuid = outputStreamInstance.uuid;
	return outputStream;
};


