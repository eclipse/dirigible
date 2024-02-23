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

	constructor(public readonly native: any){}

	public read(): any | number {
		return StreamsFacade.read(this.native);
	}

	public readBytes(): any {
		const native = StreamsFacade.readBytes(this.native);
		return bytes.toJavaScriptBytes(native);
	}

	public readBytesNative(): any{
		return StreamsFacade.readBytes(this.native);
	}

	public readText(): string {
		return StreamsFacade.readText(this.native);
	}

	public close(): void {
		StreamsFacade.close(this.native);
	}

	public isValid(): boolean {
		return this.native !== null;
	}

};

/**
 * OutputStream object. To be used internally by the API layer
 */
export class OutputStream {

	constructor(public readonly native: any) {}

	public write(byte: any): void {
		StreamsFacade.write(this.native, byte);
	}

	public writeBytes(data: any): void {
		const native = bytes.toJavaBytes(data);
		StreamsFacade.writeBytes(this.native, native);
	}

	public writeBytesNative(data: string): void {
		StreamsFacade.writeBytes(this.native, data);
	}

	public writeText(text: string): void {
		StreamsFacade.writeText(this.native, text);
	}

	public close(): void {
		StreamsFacade.close(this.native);
	}

	public getBytes(): any {
		const native = StreamsFacade.getBytes(this.native);
		const data = bytes.toJavaScriptBytes(native);
		return data;
	}

	public getBytesNative(): any {
		const native = StreamsFacade.getBytes(this.native);
		return native;
	}

	public getText(): string {
		const value = StreamsFacade.getText(this.native);
		return value;
	}

	public isValid(): boolean {
		return this.native !== null;
	}

};

export class Streams{

	public static copy(input: InputStream, output: OutputStream): void {
		StreamsFacade.copy(input.native, output.native);
	};

	public static copyLarge(input: InputStream, output: OutputStream): void {
		StreamsFacade.copyLarge(input.native, output.native);
	};

	/**
	 * Get an ByteArrayInputStream for the provided resource
	 */
	public static getResourceAsByteArrayInputStream(path: string): InputStream {
		const native = StreamsFacade.getResourceAsByteArrayInputStream(path);
		return new InputStream(native);
	};

	/**
	 * Create an ByteArrayInputStream for byte array provided
	 */
	public static createByteArrayInputStream(data: any): InputStream {
		const array = bytes.toJavaBytes(data);
		const native = StreamsFacade.createByteArrayInputStream(array);
		return new InputStream(native);
	};


	/**
	 * Create a ByteArrayOutputStream
	 */
	public static createByteArrayOutputStream(): OutputStream {
		const native = StreamsFacade.createByteArrayOutputStream();
		return new OutputStream(native);
	};

	/**
	 * Create an InputStream object by a native InputStream
	 */
	public static createInputStream(native: InputStream): InputStream {
		const inputStream = new InputStream(native);
		return inputStream;
	};

	/**
	 * Create an OutputStream object by a native OutputStream
	 */
	public static createOutputStream(native: OutputStream): OutputStream {
		const outputStream = new OutputStream(native);
		return outputStream;
	};
}
