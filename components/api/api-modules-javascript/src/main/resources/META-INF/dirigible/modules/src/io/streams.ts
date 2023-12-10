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
import {bytes} from ".";
const StreamsFacade = Java.type("org.eclipse.dirigible.components.api.io.StreamsFacade");

/**
 * InputStream object. To be used internally by the API layer
 */
export class InputStream {

	constructor(private native) {}

	read() {
		return StreamsFacade.read(this.native);
	}

	readBytes() {
		const native = StreamsFacade.readBytes(this.native);
		return bytes.toJavaScriptBytes(native);
	}

	readBytesNative() {
		return StreamsFacade.readBytes(this.native);
	}

	readText() {
		return StreamsFacade.readText(this.native);
	}

	close() {
		StreamsFacade.close(this.native);
	}

	isValid() {
		return this.native !== null;
	}

};

/**
 * OutputStream object. To be used internally by the API layer
 */
export class OutputStream {

	constructor(public readonly native) {}

	write(byte) {
		StreamsFacade.write(this.native, byte);
	}

	writeBytes(data) {
		const native = bytes.toJavaBytes(data);
		StreamsFacade.writeBytes(this.native, native);
	}

	writeBytesNative(data) {
		StreamsFacade.writeBytes(this.native, data);
	}

	writeText(text) {
		StreamsFacade.writeText(this.native, text);
	}

	close() {
		StreamsFacade.close(this.native);
	}

	getBytes() {
		const native = StreamsFacade.getBytes(this.native);
		const data = bytes.toJavaScriptBytes(native);
		return data;
	}

	getBytesNative() {
		const native = StreamsFacade.getBytes(this.native);
		return native;
	}

	getText() {
		const value = StreamsFacade.getText(this.native);
		return value;
	}

	isValid() {
		return this.native !== null;
	}

};

export function copy(input, output) {
	StreamsFacade.copy(input.native, output.native);
};

export function copyLarge(input, output) {
	StreamsFacade.copyLarge(input.native, output.native);
};

/**
 * Get an ByteArrayInputStream for the provided resource
 */
export function getResourceAsByteArrayInputStream(path) {
	const native = StreamsFacade.getResourceAsByteArrayInputStream(path);
	return new InputStream(native);
};

/**
 * Create an ByteArrayInputStream for byte array provided
 */
export function createByteArrayInputStream(data) {
	const array = bytes.toJavaBytes(data);
	const native = StreamsFacade.createByteArrayInputStream(array);
	return new InputStream(native);
};


/**
 * Create a ByteArrayOutputStream
 */
export function createByteArrayOutputStream() {
	const native = StreamsFacade.createByteArrayOutputStream();
	return new OutputStream(native);
};

/**
 * Create an InputStream object by a native InputStream
 */
export function createInputStream(native) {
	const inputStream = new InputStream(native);
	return inputStream;
};

/**
 * Create an OutputStream object by a native OutputStream
 */
export function createOutputStream(native) {
	const outputStream = new OutputStream(native);
	return outputStream;
};
