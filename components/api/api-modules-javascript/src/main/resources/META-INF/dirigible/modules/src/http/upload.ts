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
 * HTTP API Upload
 *
 */
import * as streams from "@dirigible/io/streams"
import * as bytes from "@dirigible/io/bytes"
const HttpUploadFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpUploadFacade");

export function isMultipartContent() {
    return HttpUploadFacade.isMultipartContent();
}

export function parseRequest() {
    const native = __context.get("files");
    return new FileItems(native);
}

/**
 * FileItems object
 */
class FileItems {

    private readonly native: any

    constructor(native: any) {
        this.native = native;
    }

    public get(index) {
        const native = this.native.get(index);
        return new FileItem(native);
    }

    public size() {
        return this.native.size();
    }
}

/**
 * FileItem object
 */
class FileItem {

    private readonly native: any

    constructor(native: any) {
        this.native = native;
    }

    public getName() {
        return this.native.getOriginalFilename();
    }

    public getContentType() {
        return this.native.getContentType();
    }

    public isEmpty() {
        return this.native.isEmpty();
    }

    public getSize() {
        return this.native.getSize();
    }

    public getBytes() {
        const data = this.getBytesNative();
        return bytes.toJavaScriptBytes(data);
    }

    public getBytesNative() {
        return this.native.getBytes();
    }

    public getText() {
        return String.fromCharCode.apply(null, this.getBytesNative());
    }

    public getInputStream() {
        const native = this.native.getInputStream();
        return new streams.InputStream(native);
    }

}