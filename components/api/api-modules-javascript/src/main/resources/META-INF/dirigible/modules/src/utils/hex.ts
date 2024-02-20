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
import * as bytes from "@dirigible/io/bytes";

const HexFacade = Java.type("org.eclipse.dirigible.components.api.utils.HexFacade");

export class Hex{

	/**
	 * Encode the input (text or byte array) as text
	 */
	public static encode(input) {
		return bytes.byteArrayToText(this.encodeAsNativeBytes(input));
	};

	/**
	 * Encode the input (text or byte array) as byte array
	 */
	public static encodeAsBytes(input) {
		return bytes.toJavaScriptBytes(this.encodeAsNativeBytes(input));
	};

	/**
	 * Encode the input (text or byte array) as java native byte array
	 */
	public static encodeAsNativeBytes(input) {
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
	public static decode(input) {
		const output = this.decodeAsNativeBytes(input);
		if (output) {
			return bytes.toJavaScriptBytes(output);
		}
		return output;
	};

	/**
	 * Decode the input (text or byte array) as java native byte array
	 */
	public static decodeAsNativeBytes(input) {
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
}
