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
 * API v4 Bytes
 * 
 * Bytes module is supported only with the Mozilla Rhino engine
 */

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
