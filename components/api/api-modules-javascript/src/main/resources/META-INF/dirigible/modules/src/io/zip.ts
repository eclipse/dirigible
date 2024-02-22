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
 * API Zip
 */
import * as bytes from "sdk/io/bytes";
const ZipFacade = Java.type("org.eclipse.dirigible.components.api.io.ZipFacade");

export function zip(sourcePath, zipTargetPath) {
    ZipFacade.exportZip(sourcePath, zipTargetPath);
};
export function unzip(zipPath, targetPath) {
    ZipFacade.importZip(zipPath, targetPath);
};

export function createZipInputStream(inputStream) {
    const native = ZipFacade.createZipInputStream(inputStream.native);
    return new ZipInputStream(native);
};

class ZipInputStream {

    constructor(private native) {}

    getNextEntry() {
        const native = this.native.getNextEntry();
        const zipEntry = new ZipEntry(native);
        return zipEntry;
    };

    read() {
        const native = ZipFacade.readNative(this.native);
        return bytes.toJavaScriptBytes(native);
    };

    readNative() {
        return ZipFacade.readNative(this.native);
    };

    readText() {
        return ZipFacade.readText(this.native);
    };

    close() {
        this.native.close();
    };

};

export function createZipOutputStream(outputStream) {
    const native = ZipFacade.createZipOutputStream(outputStream.native);
    return new ZipOutputStream(native);
};

class ZipOutputStream {

    constructor(private native) {}

    createZipEntry(name) {
        const nativeNext = ZipFacade.createZipEntry(name);
        const zipEntry = new ZipEntry(nativeNext);
        this.native.putNextEntry(nativeNext);
        return zipEntry;
    };

    write(data) {
        const native = bytes.toJavaBytes(data);
        ZipFacade.writeNative(this.native, native);
    };

    writeNative(data) {
        ZipFacade.writeNative(this.native, data);
    };

    writeText(text) {
        ZipFacade.writeText(this.native, text);
    };

    closeEntry() {
        this.native.closeEntry();
    };

    close() {
        this.native.finish();
        this.native.flush();
        this.native.close();
    };

};

/**
 * ZipEntry object
 */
class ZipEntry {

    constructor(private native) {}

    getName() {
        return this.native.getName();
    };

    getSize() {
        return this.native.getSize();
    };

    getCompressedSize() {
        return this.native.getCompressedSize();
    };

    getTime() {
        return this.native.getTime();
    };

    getCrc() {
        return this.native.getCrc();
    };

    getComment() {
        return this.native.getComment();
    };

    isDirectory() {
        return this.native.isDirectory();
    };

    isValid() {
        return this.native !== null;
    };

}
