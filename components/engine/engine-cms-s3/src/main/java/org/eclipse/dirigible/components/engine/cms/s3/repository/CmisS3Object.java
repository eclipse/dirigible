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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.engine.cms.CmisObject;
import org.eclipse.dirigible.components.engine.cms.ObjectType;
import org.eclipse.dirigible.repository.api.IEntity;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * The Class CmisS3Object.
 */
public class CmisS3Object implements CmisObject {

    /**
     * The session.
     */
    private CmisS3Session session;

    /**
     * The internal entity.
     */
    private String internalEntity;

    /**
     * The type collection.
     */
    private boolean typeCollection = false;

    /**
     * Instantiates a new cmis object.
     *
     * @param session the session
     * @param path    the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    // Example:
    // a/b/c/x.doc
    // a/b/y.doc
    // a/z.doc
    // search for a/ -> b; z.doc
    //TODO check if trailing / and isEmpty() is folder else document
    //id = path
    //name = last string after /
    public CmisS3Object(CmisS3Session session, String path) throws IOException {
        super();
        this.session = session;
        path = sanitize(path);
        this.internalEntity = path;
        if (path.endsWith("/")) {
            this.typeCollection = true;
        } else {
            this.typeCollection = false;
        }
    }

    /**
     * Gets the internal entity.
     *
     * @return the internal entity
     */
    public String getInternalEntity() {
        return internalEntity;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    protected boolean isCollection() {
        return typeCollection;
    }

    /**
     * Sanitize.
     *
     * @param path the path
     * @return the string
     */
    @Override
    public String sanitize(String path) {
        return path.replace("\\", "");
    }

    /**
     * Returns the ID of this CmisS3Object.
     *
     * @return the Id
     */
    @Override
    public String getId() {
        return this.getInternalEntity();
    }

    /**
     * Returns the Name of this CmisS3Object.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.getInternalEntity();
    }

    /**
     * Returns the Type of this CmisS3Object.
     *
     * @return the object type
     */
    @Override
    public ObjectType getType() {
        return this.isCollection() ? ObjectType.FOLDER : ObjectType.DOCUMENT;
    }

    /**
     * Delete this CmisS3Object.
     *
     * @throws IOException IO Exception
     */
    @Override
    public void delete() throws IOException {
        S3Facade.delete(this.getInternalEntity());
    }

    /**
     * Delete this CmisS3Object.
     *
     * @param allVersions whether to delete all versions
     * @throws IOException IO Exception
     */
    @Override
    public void delete(boolean allVersions) throws IOException {
        delete();
    }

    /**
     * Rename this CmisS3Object.
     *
     * @param newName the new name
     * @throws IOException IO Exception
     */
    @Override
    public void rename(String newName) throws IOException {
        //TODO see how to rename from S3Facade
        //this.getInternalEntity().renameTo(newName);
    }

}
