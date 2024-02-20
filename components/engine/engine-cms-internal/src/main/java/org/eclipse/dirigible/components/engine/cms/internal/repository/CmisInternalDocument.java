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
package org.eclipse.dirigible.components.engine.cms.internal.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.dirigible.components.engine.cms.CmisDocument;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * The Class CmisDocument.
 */
public class CmisInternalDocument extends CmisInternalObject implements CmisDocument {

    /** The session. */
    private CmisInternalSession session;

    /** The internal resource. */
    private IResource internalResource;

    /** The repository. */
    private IRepository repository;

    /**
     * Instantiates a new document.
     *
     * @param session the session
     * @param internalResource the internal resource
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisInternalDocument(CmisInternalSession session, IResource internalResource) throws IOException {
        super(session, internalResource.getPath());
        this.session = session;
        this.repository = (IRepository) session.getCmisRepository()
                                               .getInternalObject();
        this.internalResource = internalResource;
    }

    /**
     * Instantiates a new document.
     *
     * @param session the session
     * @param id the id
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisInternalDocument(CmisInternalSession session, String id) throws IOException {
        super(session, id);
        id = sanitize(id);
        this.session = session;
        this.repository = (IRepository) session.getCmisRepository()
                                               .getInternalObject();
        this.internalResource = this.repository.getResource(id);
    }

    /**
     * Gets the internal folder.
     *
     * @return the internal folder
     */
    public IResource getInternalFolder() {
        return internalResource;
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
     * Returns the CmisInternalContentStream representing the contents of this CmisDocument.
     *
     * @return Content Stream
     * @throws IOException IO Exception
     */
    public CmisInternalContentStream getContentStream() throws IOException {
        byte[] content = this.internalResource.getContent();
        return new CmisInternalContentStream(session, this.internalResource.getName(), content.length,
                this.internalResource.getContentType(), new ByteArrayInputStream(content));
    }

    /**
     * Returns the Path of this CmisDocument.
     *
     * @return the path
     */
    public String getPath() {
        return this.internalResource.getPath();
    }

}
