/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.eclipse.dirigible.components.api.http.HttpRequestFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

/**
 * The file a request uploads for an import: the first file part of a {@code multipart/form-data}
 * body, or the whole body of any other request.
 *
 * <p>
 * A multipart body is read through the servlet {@link Part} API, because the controller is served
 * by Spring, which has parsed the parts before the controller runs - re-parsing the raw stream (as
 * {@code org.eclipse.dirigible.sdk.http.Upload} does) finds it already consumed.
 */
public final class UploadedFile {

    private final String name;
    private final byte[] content;

    private UploadedFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    /**
     * Read the file of the current request.
     *
     * @param rawBodyName the file's name when the body is the raw file - a multipart part carries its
     *        own
     * @return the uploaded file, or {@code null} when a multipart body holds no file part
     * @throws IOException when the body cannot be read
     * @throws IllegalStateException when there is no request in progress
     */
    public static UploadedFile fromRequest(String rawBodyName) throws IOException {
        HttpServletRequest request = HttpRequestFacade.getRequest();
        if (request == null) {
            throw new IllegalStateException("No active request to read the uploaded file from");
        }
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ROOT)
                                              .startsWith("multipart/")) {
            try {
                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {
                        try (InputStream in = part.getInputStream()) {
                            return new UploadedFile(baseName(part.getSubmittedFileName()), in.readAllBytes());
                        }
                    }
                }
            } catch (ServletException e) {
                throw new IOException("the multipart upload cannot be read", e);
            }
            return null;
        }
        try (InputStream in = request.getInputStream()) {
            return new UploadedFile(rawBodyName, in.readAllBytes());
        }
    }

    /**
     * Returns the file's name, without any client-side path.
     *
     * @return the file's name, or {@code null} for a raw body uploaded without one
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the file's bytes.
     *
     * @return the file's bytes
     */
    public byte[] getContent() {
        return content;
    }

    /** A browser may send the client-side path; only the name is the file's. */
    private static String baseName(String submitted) {
        int separator = Math.max(submitted.lastIndexOf('/'), submitted.lastIndexOf('\\'));
        return separator < 0 ? submitted : submitted.substring(separator + 1);
    }
}
