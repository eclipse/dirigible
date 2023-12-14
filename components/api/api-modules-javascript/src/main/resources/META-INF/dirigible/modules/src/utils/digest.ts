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
import * as streams from "@dirigible/io/streams";
import {bytes} from "@dirigible/io";
const DigestFacade = Java.type("org.eclipse.dirigible.components.api.utils.DigestFacade");

/**
 * Calculate MD5 digest from input (text or byte array) and return result as byte array
 */
export function md5(input) {
	return bytes.toJavaScriptBytes(md5AsNativeBytes(input));
};

/**
 * Calculate MD5 digest from input (text or byte array) and return result as 16 elements java native byte array
 */
export function md5AsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		var baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.md5(native);
	return output;
};

/**
 * Calculate MD5 digest from input (text or byte array) and return result as 32 character hex string
 */
export function md5Hex(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.md5Hex(native);
	return output;
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 20 elements byte array
 */
export function sha1(input) {
	return bytes.toJavaScriptBytes(sha1AsNativeBytes(input));
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 20 elements java native byte array
 */
export function sha1AsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.sha1(native);
	return output;
};

/**
 * Calculate SHA256 digest from input (text or byte array) and return result as 32 elements byte array
 */
export function sha256(input) {
	return bytes.toJavaScriptBytes(sha256AsNativeBytes(input));
};

/**
 * Calculate SHA256 digest from input (text or byte array) and return result as 32 elements java native byte array
 */
export function sha256AsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.sha256(native);
	return output;
};

/**
 * Calculate SHA384 digest from input (text or byte array) and return result as 48 elements byte array
 */
export function sha384(input) {
	return bytes.toJavaScriptBytes(sha384AsNativeBytes(input));
};

/**
 * Calculate SHA384 digest from input (text or byte array) and return result as 48 elements java native byte array
 */
export function sha384AsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.sha384(native);
	return output;
};

/**
 * Calculate SHA512 digest from input (text or byte array) and return result as 64 elements byte array
 */
export function sha512(input) {
	return bytes.toJavaScriptBytes(sha512AsNativeBytes(input));
};

/**
 * Calculate SHA512 digest from input (text or byte array) and return result as 64 elements java native byte array
 */
export function sha512AsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.sha512(native);
	return output;
};

/**
 * Calculate SHA1 digest from input (text or byte array) and return result as 40 character hex string
 */
export function sha1Hex(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = DigestFacade.sha1Hex(native);
	return output;
};
