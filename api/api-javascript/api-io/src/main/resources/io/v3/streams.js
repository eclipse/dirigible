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
		var value = java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'read', [this.uuid]);
		return value;
	};
	
	this.close = function() {
		java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'close', [this.uuid]);
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

	this.write = function(value) {
		var value = java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'write', [this.uuid, value]);
	};

	this.close = function() {
		java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'close', [this.uuid]);
	};
	
	this.getBytes = function() {
		return java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'getBytes', [this.uuid]);
	};
	
};

exports.copy = function(input, output) {
	java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'copy', [input.uuid, output.uuid]);
};


/**
 * Create an ByteArrayInputStream for byte array provided
 */
exports.createByteArrayInputStream = function(bytes) {
	var inputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'createByteArrayInputStream', [JSON.stringify(bytes)], true);
	var inputStream = new InputStream();
	inputStream.uuid = inputStreamInstance.uuid;
	return inputStream;
};


/**
 * Create a ByteArrayOutputStream
 */
exports.createByteArrayOutputStream = function() {
	var outputStreamInstance = java.call('org.eclipse.dirigible.api.v3.io.IoFacade', 'createByteArrayOutputStream', [], true);
	var outputStream = new OutputStream();
	outputStream.uuid = outputStreamInstance.uuid;
	return outputStream;
};


