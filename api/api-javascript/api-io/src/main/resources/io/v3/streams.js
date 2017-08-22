/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

/**
 * Create an InputStream
 */
exports.createInputStream = function() {
	return new InputStream();
};

/**
 * InputStream object. To be used internally by the API layer
 */
InputStream = function() {
	
	this.read = function() {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'read', [this.uuid]);
		return value;
	};
	
	this.readText = function() {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'readText', [this.uuid]);
		return value;
	};
	
	this.close = function() {
		java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'close', [this.uuid]);
	};
	
};

/**
 * Create an OutputStream
 */
exports.createOutputStream = function() {
	return new OutputStream();
};

/**
 * OutputStream object. To be used internally by the API layer
 */
OutputStream = function() {

	this.write = function(byte) {
		var value = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'write', [this.uuid, byte]);
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
	
};

exports.copy = function(input, output) {
	java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'copy', [input.uuid, output.uuid]);
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
	var inputStream = new InputStream();
	inputStream.uuid = inputStreamInstance.uuid;
	return inputStream;
};


/**
 * Create a ByteArrayOutputStream
 */
exports.createByteArrayOutputStream = function() {
	var outputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.StreamsFacade', 'createByteArrayOutputStream', [], true);
	var outputStream = new OutputStream();
	outputStream.uuid = outputStreamInstance.uuid;
	return outputStream;
};


