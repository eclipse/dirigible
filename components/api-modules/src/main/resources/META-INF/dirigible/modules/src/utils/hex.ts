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
const streams = dirigibleRequire('io/streams');
const bytes = dirigibleRequire("io/bytes");

const HexFacade = Java.type("org.eclipse.dirigible.components.api.utils.HexFacade");

/**
 * Encode the input (text or byte array) as text
 */
export function encode(input) {
	return bytes.byteArrayToText(encodeAsNativeBytes(input));
};

/**
 * Encode the input (text or byte array) as byte array
 */
export function encodeAsBytes(input) {
	return bytes.toJavaScriptBytes(encodeAsNativeBytes(input));
};

/**
 * Encode the input (text or byte array) as java native byte array
 */
export function encodeAsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}

	const output = HexFacade.encodeNative(native);
	return output;
};

/**
 * Decode the input (text or byte array) as text
 */
export function decode(input) {
	const output = decodeAsNativeBytes(input);
	if (output) {
		return bytes.toJavaScriptBytes(output);
	}
	return output;
};

/**
 * Decode the input (text or byte array) as java native byte array
 */
export function decodeAsNativeBytes(input) {
	const data = input;
	let native;
	if (typeof data === 'string') {
		const baos = streams.createByteArrayOutputStream();
		baos.writeText(data);
		native = baos.getBytesNative();
	} else if (Array.isArray(data)) {
		native = bytes.toJavaBytes(data);
	}
	return HexFacade.decodeNative(native);
};
