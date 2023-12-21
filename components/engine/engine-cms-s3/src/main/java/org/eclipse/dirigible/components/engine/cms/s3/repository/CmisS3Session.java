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

import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.engine.cms.CmisSession;
import org.eclipse.dirigible.repository.api.IRepository;

import java.io.IOException;

/**
 * The Class CmisS3Session.
 */
public class CmisS3Session implements CmisSession {

    /**
     * The cmis repository.
     */
    private CmisRepository cmisRepository;

    /**
     * Instantiates a new cmis session.
     *
     * @param cmisRepository the cmis repository
     */
    public CmisS3Session(CmisRepository cmisRepository) {
        super();
        this.cmisRepository = cmisRepository;
    }

    /**
     * Gets the cmis repository.
     *
     * @return the cmis repository
     */
    public CmisRepository getCmisRepository() {
        return cmisRepository;
    }

    /**
     * Returns the information about the CMIS repository.
     *
     * @return Repository Info
     */
    public CmisS3RepositoryInfo getRepositoryInfo() {
        return new CmisS3RepositoryInfo(this);
    }

    /**
     * Returns the CmisS3ObjectFactory utility.
     *
     * @return Object Factory
     */
    public CmisS3ObjectFactory getObjectFactory() {
        return new CmisS3ObjectFactory(this);
    }

    /**
     * Returns the root folder of this repository.
     *
     * @return CmisS3Folder
     * @throws IOException IO Exception
     */

    public CmisS3Folder getRootFolder() throws IOException {
        return new CmisS3Folder(this);
    }

    /**
     * Returns a CMIS Object by name.
     *
     * @param id the Id
     * @return CMIS Object
     * @throws IOException IO Exception
     */
    @Override
    public CmisS3Object getObject(String id) throws IOException {
        String path = id;
        if (path.equals("/")) {
            return new CmisS3Folder(this, id, path);
        }
        if (!path.startsWith("/")) {
            path = IRepository.SEPARATOR + id;
        }
        if (S3Facade.exists(path)) {
            if (path.endsWith("/")) {
                return new CmisS3Folder(this, path, CmisS3Utils.findCurrentFolder(path));
            } else {
                return new CmisS3Document(this, path, CmisS3Utils.findCurrentFile(path));
            }
        } else if (S3Facade.exists(path + "/")) {
            return new CmisS3Folder(this, path, CmisS3Utils.findCurrentFolder(path + "/"));
        }
        return new CmisS3Document(this, id, id);
    }

    /**
     * Returns a CMIS Object by path.
     *
     * @param path the path
     * @return CMIS Object
     * @throws IOException IO Exception
     */
    @Override
    public CmisS3Object getObjectByPath(String path) throws IOException {
        // Just for debugging
        // if(!path.equals("__internal/roles-access.json")) {
        // return getObject(path);
        // }
        // return new CmisS3Object(this, "/", "/");
        return getObject(path);
    }
}
