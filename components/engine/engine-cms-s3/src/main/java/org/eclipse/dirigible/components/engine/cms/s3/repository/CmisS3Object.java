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
     * The type collection.
     */
    private boolean typeCollection = false;

    /**
     * The id/path of the object.
     */
    private String id;

    private String name;

    /**
     * Instantiates a new cmis object.
     *
     * @param session the session
     * @param id the path
     * @param name the name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Object(CmisS3Session session, String id, String name) throws IOException {
        super();
        this.session = session;
        id = sanitize(id);
        this.id = id;
        this.name = name;
        if (id.endsWith("/")) {
            this.typeCollection = true;
        } else {
            this.typeCollection = false;
        }
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
        return this.id;
    }

    /**
     * Returns the Name of this CmisS3Object.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.name;
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
        String cmisPath = this.id.substring(1);
        if (this.typeCollection) {
            S3Facade.deleteFolder(cmisPath);
        } else {
            S3Facade.delete(cmisPath);
        }
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
        // TODO see how to rename from S3Facade
        // S3Facade.update();
    }

}
