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
 * API v4 Bytes
 *
 * Bytes module is supported only with the Mozilla Rhino engine
 */

/**
 * Convert the native JavaScript byte array to Java one. To be used internally by the API layer
 */
exports.toJavaBytes = function(bytes) {
	const internalBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, bytes.length);
	for (let i=0; i<bytes.length; i++) {
		internalBytes[i] = bytes[i];
	}
	return internalBytes;
};

/**
 * Convert the Java byte array to a native JavaScript one. To be used internally by the API layer
 */
exports.toJavaScriptBytes = function(internalBytes) {
	const bytes = [];
	for (let i=0; i<internalBytes.length; i++) {
		bytes.push(internalBytes[i]);
	}
	return bytes;
};

/**
 * Converts a text to a byte array
 */
exports.textToByteArray = function(text) {
	const javaString = new java.lang.String(text);
	const native = org.eclipse.dirigible.components.api.io.BytesFacade.textToByteArray(text);
	return exports.toJavaScriptBytes(native);
};

/**
 * Converts a text to a byte array
 */
exports.byteArrayToText = function(data) {
	const native = exports.toJavaBytes(data);
	return String.fromCharCode.apply(String, exports.toJavaScriptBytes(native));
};

/**
 * Converts an integer to a byte array
 */
 exports.intToByteArray = function(value, byteOrder) {
    return org.eclipse.dirigible.components.api.io.BytesFacade.intToByteArray(value, byteOrder)
}

/**
 * Converts a byte array to integer
 */
exports.byteArrayToInt = function(data, byteOrder) {
    return org.eclipse.dirigible.components.api.io.BytesFacade.byteArrayToInt(data, byteOrder);
}
