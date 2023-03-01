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
const streams = require("io/streams");
const bytes = require("io/bytes");
exports.zip = function (sourcePath, zipTargetPath) {
    org.eclipse.dirigible.components.api.io.ZipFacade.exportZip(sourcePath, zipTargetPath);
};
exports.unzip = function (zipPath, targetPath) {
    org.eclipse.dirigible.components.api.io.ZipFacade.importZip(zipPath, targetPath);
};

exports.createZipInputStream = function (inputStream) {

    /**
     * ZipInputStream object
     */
    const ZipInputStream = function () {

        this.getNextEntry = function () {
            const zipEntry = new ZipEntry();
            const native = this.native.getNextEntry();
            zipEntry.native = native;
            zipEntry.input = this;
            return zipEntry;
        };

        this.read = function () {
            const native = org.eclipse.dirigible.components.api.io.ZipFacade.readNative(this.native);
            return bytes.toJavaScriptBytes(native);
        };

        this.readNative = function () {
            return org.eclipse.dirigible.components.api.io.ZipFacade.readNative(this.native);
        };

        this.readText = function () {
            return org.eclipse.dirigible.components.api.io.ZipFacade.readText(this.native);
        };

        this.close = function () {
            this.native.close();
        };

    };

    const zipInputStream = new ZipInputStream();
    zipInputStream.native = org.eclipse.dirigible.components.api.io.ZipFacade.createZipInputStream(inputStream.native);
    return zipInputStream;
};

exports.createZipOutputStream = function (outputStream) {

    /**
     * ZipOutputStream object
     */
    const ZipOutputStream = function () {

        this.createZipEntry = function (name) {
            const zipEntry = new ZipEntry();
            zipEntry.native = org.eclipse.dirigible.components.api.io.ZipFacade.createZipEntry(name);
            this.native.putNextEntry(zipEntry.native);
            return zipEntry;
        };

        this.write = function (data) {
            const native = bytes.toJavaBytes(data);
            org.eclipse.dirigible.api.v3.io.ZipFacade.writeNative(this.native, native);
        };

        this.writeNative = function (data) {
            org.eclipse.dirigible.api.v3.io.ZipFacade.writeNative(this.native, data);
        };

        this.writeText = function (text) {
            org.eclipse.dirigible.api.v3.io.ZipFacade.writeText(this.native, text);
        };

        this.closeEntry = function () {
            this.native.closeEntry();
        };

        this.close = function () {
            this.native.finish();
            this.native.flush();
            this.native.close();
        };

    };

    const zipOutputStream = new ZipOutputStream();
    zipOutputStream.native = org.eclipse.dirigible.components.api.io.ZipFacade.createZipOutputStream(outputStream.native);
    return zipOutputStream;
};

/**
 * ZipEntry object
 */
function ZipEntry() {

    this.getName = function () {
        return this.native.getName();
    };

    this.getSize = function () {
        return this.native.getSize();
    };

    this.getCompressedSize = function () {
        return this.native.getCompressedSize();
    };

    this.getTime = function () {
        return this.native.getTime();
    };

    this.getCrc = function () {
        return this.native.getCrc();
    };

    this.getComment = function () {
        return this.native.getComment();
    };

    this.isDirectory = function () {
        return this.native.isDirectory();
    };

    this.isValid = function () {
        return this.native !== null;
    };

}
