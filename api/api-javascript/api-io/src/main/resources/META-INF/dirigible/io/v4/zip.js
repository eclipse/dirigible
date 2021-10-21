/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Zip
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

var streams = require("io/v4/streams");
var bytes = require("io/v4/bytes");

exports.createZipInputStream = function (inputStream) {

    /**
     * ZipInputStream object
     */
    var ZipInputStream = function () {

        this.getNextEntry = function () {
            var zipEntry = new ZipEntry();
            var native = this.native.getNextEntry();
            zipEntry.native = native;
            zipEntry.input = this;
            return zipEntry;
        };

        this.read = function () {
            var native = org.eclipse.dirigible.api.v3.io.ZipFacade.readNative(this.native);
            var data = bytes.toJavaScriptBytes(native);
            return data;
        };

        this.readNative = function () {
            let data = org.eclipse.dirigible.api.v3.io.ZipFacade.readNative(this.native);
            return data;
        };

        this.readText = function () {
            var text = org.eclipse.dirigible.api.v3.io.ZipFacade.readText(this.native);
            return text;
        };

        this.close = function () {
            this.native.close();
        };

    };

    var zipInputStream = new ZipInputStream();
    var native = org.eclipse.dirigible.api.v3.io.ZipFacade.createZipInputStream(inputStream.native);
    zipInputStream.native = native;
    return zipInputStream;
};

exports.createZipOutputStream = function (outputStream) {

    /**
     * ZipOutputStream object
     */
    var ZipOutputStream = function () {

        this.createZipEntry = function (name) {
            var zipEntry = new ZipEntry();
            var native = org.eclipse.dirigible.api.v3.io.ZipFacade.createZipEntry(name);
            zipEntry.native = native;
            this.native.putNextEntry(zipEntry.native);
            return zipEntry;
        };

        this.write = function (data) {
            var native = bytes.toJavaBytes(data);
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

    var zipOutputStream = new ZipOutputStream();
    var native = org.eclipse.dirigible.api.v3.io.ZipFacade.createZipOutputStream(outputStream.native);
    zipOutputStream.native = native;
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
