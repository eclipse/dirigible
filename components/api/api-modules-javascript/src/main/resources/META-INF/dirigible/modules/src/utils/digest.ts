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
const DigestFacade = Java.type("org.eclipse.dirigible.components.api.utils.DigestFacade");

export class Digest{

	/**
	 * Calculate MD5 digest from input (text or byte array) and return result as byte array
	 */
	public static md5(input: any): any {
		return bytes.toJavaScriptBytes(this.md5AsNativeBytes(input));
	};

	/**
	 * Calculate MD5 digest from input (text or byte array) and return result as 16 elements java native byte array
	 */
	public static md5AsNativeBytes(input: any): any {
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
	public static md5Hex(input: any): any {
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
	public static sha1(input: any): any {
		return bytes.toJavaScriptBytes(this.sha1AsNativeBytes(input));
	};

	/**
	 * Calculate SHA1 digest from input (text or byte array) and return result as 20 elements java native byte array
	 */
	public static sha1AsNativeBytes(input: any): any {
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
	public static sha256(input: any): any {
		return bytes.toJavaScriptBytes(this.sha256AsNativeBytes(input));
	};

	/**
	 * Calculate SHA256 digest from input (text or byte array) and return result as 32 elements java native byte array
	 */
	public static sha256AsNativeBytes(input: any): any {
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
	public static sha384(input: any): any {
		return bytes.toJavaScriptBytes(this.sha384AsNativeBytes(input));
	};

	/**
	 * Calculate SHA384 digest from input (text or byte array) and return result as 48 elements java native byte array
	 */
	public static sha384AsNativeBytes(input: any): any {
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
	public static sha512(input: any): any {
		return bytes.toJavaScriptBytes(this.sha512AsNativeBytes(input));
	};

	/**
	 * Calculate SHA512 digest from input (text or byte array) and return result as 64 elements java native byte array
	 */
	public static sha512AsNativeBytes(input: any): any {
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
	public static sha1Hex(input: any): any {
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
}
