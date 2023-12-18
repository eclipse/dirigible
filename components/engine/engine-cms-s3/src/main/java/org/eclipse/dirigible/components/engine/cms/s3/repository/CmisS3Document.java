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

    private String internalResource;

    /**
     * The internal resource.
     */
    private S3Facade s3Facade;

//        /**
//     * Instantiates a new document.
//     *
//     * @param session the session
//     * @param internalResource the internal resource
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public CmisS3Document(CmisS3Session session, IResource internalResource) throws IOException {
//        super(session, internalResource.getPath());
//        this.session = session;
//        this.repository = (IRepository) session.getCmisRepository().getInternalObject();
//        this.internalResource = internalResource;
//    }

    /**
     * Instantiates a new document.
     *
     * @param session the session
     * @param id      the idx
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Document(CmisS3Session session, String id) throws IOException {
        super(session, id);
        id = sanitize(id);
        this.session = session;
        this.internalResource = id;
    }

//    /**
//     * Gets the internal folder.
//     *
//     * @return the internal folder
//     */
//    public IResource getInternalFolder() {
//        return internalResource;
//    }

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
        byte[] content = s3Facade.get(this.internalResource);
        String contentType = getContentType(this.internalResource);
        String name = getResourceName(this.internalResource);
        return new CmisS3ContentStream(session, name, content.length, contentType,
                new ByteArrayInputStream(content));
    }

    /**
     * Returns the Path of this CmisDocument.
     *
     * @return the path
     */
    public String getPath() {
        return this.internalResource;
    }

    private String getContentType(String resource) {
        return FilenameUtils.getExtension(resource);
    }

    private String getResourceName(String resource) {
        return FilenameUtils.getName(resource);
    }
}
