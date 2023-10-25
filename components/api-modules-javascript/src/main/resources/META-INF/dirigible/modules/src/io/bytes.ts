/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Bytes
 */

/**
 * Convert the native JavaScript byte array to Java one. To be used internally by the API layer
 */

const JString = Java.type("java.lang.String");
const JByte = Java.type("java.lang.Byte");
const JArray = Java.type("java.lang.reflect.Array");
const BytesFacade = Java.type("org.eclipse.dirigible.components.api.io.BytesFacade");

export function toJavaBytes(bytes) {
	const internalBytes = JArray.newInstance(JByte.TYPE, bytes.length);
	for (let i=0; i<bytes.length; i++) {
		internalBytes[i] = bytes[i];
	}
	return internalBytes;
};

/**
 * Convert the Java byte array to a native JavaScript one. To be used internally by the API layer
 */
export function toJavaScriptBytes(internalBytes) {
	const bytes = [];
	for (let i=0; i<internalBytes.length; i++) {
		bytes.push(internalBytes[i]);
	}
	return bytes;
};

/**
 * Converts a text to a byte array
 */
export function textToByteArray(text) {
	const javaString = new JString(text);
	const native = BytesFacade.textToByteArray(text);
	return toJavaScriptBytes(native);
};

/**
 * Converts a text to a byte array
 */
export function byteArrayToText(data) {
	const native = toJavaBytes(data);
	return String.fromCharCode.apply(String, toJavaScriptBytes(native));
};

/**
 * Converts an integer to a byte array
 */
 export function intToByteArray(value, byteOrder) {
    return BytesFacade.intToByteArray(value, byteOrder)
}

/**
 * Converts a byte array to integer
 */
export function byteArrayToInt(data, byteOrder) {
    return BytesFacade.byteArrayToInt(data, byteOrder);
}
