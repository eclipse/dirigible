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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.engine.cms.CmisConstants;
import org.eclipse.dirigible.components.engine.cms.CmisFolder;
import org.eclipse.dirigible.repository.api.IRepository;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * The Class CmisS3Folder.
 */
public class CmisS3Folder extends CmisS3Object implements CmisFolder {

    /**
     * The session.
     */
    private CmisS3Session session;

    /**
     * The internal folder.
     */

    private String internalFolder;

    /**
     * The root folder.
     */
    private boolean rootFolder = false;

//    /**
//     * Instantiates a new folder.
//     *
//     * @param session the session
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public CmisS3Folder(CmisS3Session session) throws IOException {
//        super(session, IRepository.SEPARATOR);
//        this.session = session;
//        // this.repository = (IRepository) session.getCmisRepository()
//        // .getInternalObject();
//        this.internalFolder = repository.getRoot();
//        this.rootFolder = true;
//    }

//    /**
//     * Instantiates a new folder.
//     *
//     * @param session the session
//     * @param internalCollection the internal collection
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public CmisS3Folder(CmisS3Session session, ICollection internalCollection) throws IOException {
//        super(session, internalCollection.getPath());
//        if (IRepository.SEPARATOR.equals(internalCollection.getPath())) {
//            this.rootFolder = true;
//        }
//        this.session = session;
//        // this.repository = (IRepository) session.getCmisRepository()
//        // .getInternalObject();
//        this.internalFolder = internalCollection;
//    }

    /**
     * Instantiates a new folder.
     *
     * @param session the session
     * @param id      the id
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Folder(CmisS3Session session, String id) throws IOException {
        super(session, id);
        id = sanitize(id);
        if (IRepository.SEPARATOR.equals(id)) {
            this.rootFolder = true;
        }
        this.session = session;
        this.internalFolder = id;
    }

    /**
     * Gets the internal folder.
     *
     * @return the internal folder
     */
    public String getInternalFolder() {
        return internalFolder;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    @Override
    protected boolean isCollection() {
        return true;
    }

    /**
     * Returns the Path of this CmisS3Folder.
     *
     * @return the path
     */
    @Override
    public String getPath() {
        //TODO check again what path we might get, probably just name/id
        return this.getInternalEntity();
    }

    /**
     * Creates a new folder under this CmisS3Folder.
     *
     * @param properties the properties
     * @return CmisS3Folder
     * @throws IOException IO Exception
     */
    public CmisS3Folder createFolder(Map<String, String> properties) throws IOException {
        String name = properties.get(CmisConstants.NAME);
        //TODO check properties
        S3Facade.put(name, properties.get("contentStream").getBytes());
        return new CmisS3Folder(this.session, this.internalFolder);
    }

    /**
     * Creates a new document under this CmisS3Folder.
     *
     * @param properties      the properties
     * @param contentStream   the content stream
     * @param versioningState the version state
     * @return CmisDocument
     * @throws IOException IO Exception
     */
    public CmisS3Document createDocument(Map<String, String> properties, CmisS3ContentStream contentStream, VersioningState versioningState)
            throws IOException {
        String name = properties.get(CmisConstants.NAME);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(contentStream.getStream(), out);
        S3Facade.put(name, out.toByteArray());
        return new CmisS3Document(this.session, name);
    }

    /**
     * Gets the children.
     *
     * @return the children
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<CmisS3Object> getChildren() throws IOException {
        List<CmisS3Object> children = new ArrayList<CmisS3Object>();
        List<S3Object> s3CurrentFolderObjects = S3Facade.listObjects(this.internalFolder);
        for (S3Object s3Object : s3CurrentFolderObjects) {
            if (s3Object.key().endsWith("/")) {
                children.add(new CmisS3Folder(this.session, s3Object.key()));
            } else {
                children.add(new CmisS3Document(this.session, s3Object.key()));
            }
        }
        return children;
    }

    /**
     * Returns true if this CmisS3Folder is a root folder and false otherwise.
     *
     * @return whether it is a root folder
     */
    @Override
    public boolean isRootFolder() {
        return rootFolder;
    }

//    /**
//     * Returns the parent CmisS3Folder of this CmisS3Folder.
//     *
//     * @return CmisS3Folder
//     * @throws IOException IO Exception
//     */
//    public CmisS3Folder getFolderParent() throws IOException {
//        if (this.internalFolder.getParent() != null) {
//            return new CmisS3Folder(this.session, this.internalFolder.getParent());
//        }
//        return new CmisS3Folder(this.session);
//    }

}
