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
const streams = require('io/streams');
const bytes = require("io/bytes");

/**
 * Encode the input (text or byte array) as text
 */
exports.encode = function(input) {
	return bytes.byteArrayToText(exports.encodeAsNativeBytes(input));
};

/**
 * Encode the input (text or byte array) as byte array
 */
exports.encodeAsBytes = function(input) {
	return bytes.toJavaScriptBytes(exports.encodeAsNativeBytes(input));
};

/**
 * Encode the input (text or byte array) as java native byte array
 */
exports.encodeAsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.components.api.utils.HexFacade.encodeNative(native);
	return output;
};

/**
 * Decode the input (text or byte array) as text
 */
exports.decode = function(input) {
	const output = exports.decodeAsNativeBytes(input);
	if (output) {
		return bytes.toJavaScriptBytes(output);
	}
	return output;
};

/**
 * Decode the input (text or byte array) as java native byte array
 */
exports.decodeAsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}
	return org.eclipse.dirigible.components.api.utils.HexFacade.decodeNative(native);
};
