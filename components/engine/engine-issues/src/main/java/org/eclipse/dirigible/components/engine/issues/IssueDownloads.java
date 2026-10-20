/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.components.engine.issues.IssueViews.Download;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Answers an attachment download. The file is uploaded by a user, so it is always served as a
 * download and never sniffed: an HTML or SVG attachment must not run in the platform's origin.
 */
final class IssueDownloads {

    private IssueDownloads() {}

    static ResponseEntity<byte[]> response(Download download) {
        return ResponseEntity.ok()
                             .contentType(mediaType(download.mimeType()))
                             .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                                                                                        .filename(download.fileName(),
                                                                                                StandardCharsets.UTF_8)
                                                                                        .build()
                                                                                        .toString())
                             .header("X-Content-Type-Options", "nosniff")
                             .body(download.content());
    }

    private static MediaType mediaType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(mimeType);
        } catch (InvalidMediaTypeException e) {
            // the uploader's browser named the type; a malformed one is served as plain bytes
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
