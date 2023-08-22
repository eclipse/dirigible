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
const streams = dirigibleRequire("io/streams");
const bytes = dirigibleRequire("io/bytes");
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

    constructor(
        private readonly native: any
    ) { }

    get(index) {
        const native = this.native.get(index);
        return new FileItem(native);
    }

    size() {
        return this.native.size();
    }
};

/**
 * FileItem object
 */
class FileItem {

    constructor(
        private readonly native: any
    ) { }

    getInputStream() {
        const inputStream = new streams.InputStream();
        inputStream.native = this.native.getInputStream();
        return inputStream;
    }

    getContentType() {
        return this.native.getContentType();
    }

    getName() {
        return this.native.getOriginalFilename();
    }

    getSize() {
        return this.native.getSize();
    }

    getBytes() {
        var data = this.native.get();
        return bytes.toJavaScriptBytes(data);
    }

    getBytesNative() {
        var data = this.native.get();
        return data;
    }

    getText() {
        return this.native.getString();
    }

    isFormField() {
        return this.native.isFormField();
    }

    getFieldName() {
        return this.native.getFieldName();
    }

    getHeaders() {
        const native = this.native.getHeaders();
        return new Headers(native);
    }

}

/**
 * Headers object
 */
class Headers {

    constructor(
        private readonly native: any
    ) { }

    getHeaderNames() {
        return HttpUploadFacade.headerNamesToList(this.native.getHeaderNames());
    };

    getHeader(headerName) {
        return this.native.getHeader(headerName);
    }
}

