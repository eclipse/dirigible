/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.s3.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.engine.cms.CmisDocument;

/**
 * The Class CmisDocument.
 */
public class CmisS3Document extends CmisS3Object implements CmisDocument {

    /**
     * The session.
     */
    private CmisS3Session session;

    private String id;

    private String name;

    /**
     * Instantiates a new document.
     *
     * @param session the session
     * @param id the idx
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Document(CmisS3Session session, String id, String name) throws IOException {
        super(session, id, name);
        id = sanitize(id);
        this.session = session;
        this.id = id;
        this.name = name;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    @Override
    protected boolean isCollection() {
        return false;
    }

    /**
     * Returns the CmisS3ContentStream representing the contents of this CmisDocument.
     *
     * @return Content Stream
     * @throws IOException IO Exception
     */
    public CmisS3ContentStream getContentStream() throws IOException {
        byte[] content = S3Facade.get(this.id.substring(1));
        String contentType = getContentType(this.id.substring(1));
        return new CmisS3ContentStream(session, this.name, content.length, contentType, new ByteArrayInputStream(content));
    }

    /**
     * Returns the Path of this CmisDocument.
     *
     * @return the path
     */
    public String getPath() {
        return this.id;
    }

    private String getContentType(String resource) {
        return S3Facade.getObjectContentType(resource);
    }

    private String getResourceName(String resource) {
        return this.name;
    }
}
