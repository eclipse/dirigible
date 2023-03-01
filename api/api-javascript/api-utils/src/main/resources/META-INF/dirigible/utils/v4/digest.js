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
 * API v4 Files
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */
const streams = require('io/v4/streams');
const bytes = require("io/v4/bytes");

/**
 * Calculate MD5 digest from input (text or byte array) and return result as byte array
 */
exports.md5 = function(input) {
	return bytes.toJavaScriptBytes(exports.md5AsNativeBytes(input));
};

/**
 * Calculate MD5 digest from input (text or byte array) and return result as 16 elements java native byte array
 */
exports.md5AsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		var baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.md5(native);
	return output;
};

/**
 * Calculate MD5 digest from input (text or byte array) and return result as 32 character hex string
 */
exports.md5Hex = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.md5Hex(native);
	return output;
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 20 elements byte array
 */
exports.sha1 = function(input) {
	return bytes.toJavaScriptBytes(exports.sha1AsNativeBytes(input));
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 20 elements java native byte array
 */
exports.sha1AsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.sha1(native);
	return output;
};

/**
 * Calculate SHA256 digest from input (text or byte array) and return result as 32 elements byte array
 */
exports.sha256 = function(input) {
	return bytes.toJavaScriptBytes(exports.sha256AsNativeBytes(input));
};

/**
 * Calculate SHA256 digest from input (text or byte array) and return result as 32 elements java native byte array
 */
exports.sha256AsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.sha256(native);
	return output;
};

/**
 * Calculate SHA384 digest from input (text or byte array) and return result as 48 elements byte array
 */
exports.sha384 = function(input) {
	return bytes.toJavaScriptBytes(exports.sha384AsNativeBytes(input));
};

/**
 * Calculate SHA384 digest from input (text or byte array) and return result as 48 elements java native byte array
 */
exports.sha384AsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.sha384(native);
	return output;
};

/**
 * Calculate SHA512 digest from input (text or byte array) and return result as 64 elements byte array
 */
exports.sha512 = function(input) {
	return bytes.toJavaScriptBytes(exports.sha512AsNativeBytes(input));
};

/**
 * Calculate SHA512 digest from input (text or byte array) and return result as 64 elements java native byte array
 */
exports.sha512AsNativeBytes = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.sha512(native);
	return output;
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 40 character hex string
 */
exports.sha1Hex = function(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = org.eclipse.dirigible.api.v3.utils.DigestFacade.sha1Hex(native);
	return output;
};
