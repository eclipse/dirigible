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

import org.eclipse.dirigible.components.engine.cms.CmisContentStream;
import org.eclipse.dirigible.components.engine.cms.CmisSession;

import java.io.InputStream;

/**
 * The Class CmisInternalContentStream.
 */
public class CmisInternalContentStream implements CmisContentStream {

    /** The cmis session. */
    private CmisInternalSession cmisSession;

    /** The filename. */
    private String filename;

    /** The length. */
    private long length;

    /** The mimetype. */
    private String mimetype;

    /** The input stream. */
    private InputStream inputStream;

    /**
     * Instantiates a new content stream.
     *
     * @param cmisSession the cmis session
     * @param filename the filename
     * @param length the length
     * @param mimetype the mimetype
     * @param inputStream the input stream
     */
    public CmisInternalContentStream(CmisInternalSession cmisSession, String filename, long length, String mimetype,
            InputStream inputStream) {
        super();
        this.cmisSession = cmisSession;
        this.filename = filename;
        this.length = length;
        this.mimetype = mimetype;
        this.inputStream = inputStream;
    }

    /**
     * Returns the InputStream of this CmisInternalContentStream object.
     *
     * @return Input Stream
     */
    @Override
    public InputStream getStream() {
        return this.inputStream;
    }

    /**
     * Gets the cmis session.
     *
     * @return the cmis session
     */
    @Override
    public CmisInternalSession getCmisSession() {
        return cmisSession;
    }

    /**
     * Sets the cmis session.
     *
     * @param cmisSession the new cmis session
     */
    @Override
    public void setCmisSession(CmisSession cmisSession) {
        this.cmisSession = (CmisInternalSession) cmisSession;
    }

    /**
     * Gets the filename.
     *
     * @return the filename
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    @Override
    public long getLength() {
        return length;
    }

    /**
     * Gets the mime type.
     *
     * @return the mime type
     */
    @Override
    public String getMimeType() {
        return mimetype;
    }

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

}
