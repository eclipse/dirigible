/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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
	var javaString = new java.lang.String(text);
	var native = org.eclipse.dirigible.api.v3.io.BytesFacade.textToByteArray(text);
	return exports.toJavaScriptBytes(native);
};

/**
 * Converts a text to a byte array
 */
exports.byteArrayToText = function(data) {
	var native = exports.toJavaBytes(data);
	return String.fromCharCode.apply(String, exports.toJavaScriptBytes(native));
};
