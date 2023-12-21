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
import java.util.*;

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

    private String id;

    private String name;

    /**
     * Is root folder.
     */
    private boolean rootFolder = false;

    /**
     * The root folder.
     */
    private static String ROOT = "/";

    /**
     * Instantiates a new folder.
     *
     * @param session the session
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Folder(CmisS3Session session) throws IOException {
        super(session, IRepository.SEPARATOR, IRepository.SEPARATOR);
        this.session = session;
        this.id = ROOT;
        this.name = ROOT;
        this.rootFolder = true;
    }

    /**
     * Instantiates a new folder.
     *
     * @param session the session
     * @param id the id
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CmisS3Folder(CmisS3Session session, String id, String name) throws IOException {
        super(session, id, name);
        id = sanitize(id);
        if (IRepository.SEPARATOR.equals(id)) {
            this.rootFolder = true;
        }
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
        return true;
    }

    /**
     * Returns the Path of this CmisS3Folder.
     *
     * @return the path
     */
    @Override
    public String getPath() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Creates a new folder under this CmisS3Folder.
     *
     * @param properties the properties
     * @return CmisS3Folder
     * @throws IOException IO Exception
     */
    public CmisS3Folder createFolder(Map<String, String> properties) throws IOException {
        String folderName;
        String name = properties.get(CmisConstants.NAME);
        if (!Objects.equals(this.id, ROOT)) {
            String fromRootPath = this.id + name + IRepository.SEPARATOR;
            folderName = fromRootPath.startsWith(IRepository.SEPARATOR) ? fromRootPath.substring(1) : fromRootPath;
            S3Facade.put(folderName, new byte[0], "");
            return new CmisS3Folder(this.session, fromRootPath, folderName);
        } else {
            folderName = name + IRepository.SEPARATOR;
            S3Facade.put(folderName, new byte[0], "");
            return new CmisS3Folder(this.session, folderName, folderName);
        }
    }

    /**
     * Creates a new document under this CmisS3Folder.
     *
     * @param properties the properties
     * @param contentStream the content stream
     * @param versioningState the version state
     * @return CmisDocument
     * @throws IOException IO Exception
     */
    public CmisS3Document createDocument(Map<String, String> properties, CmisS3ContentStream contentStream, VersioningState versioningState)
            throws IOException {
        String name = properties.get(CmisConstants.NAME);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(contentStream.getStream(), out);
        String folderName;
        if (!Objects.equals(this.id, ROOT)) {
            String fromRootPath = this.id + name;
            folderName = fromRootPath.startsWith(IRepository.SEPARATOR) ? fromRootPath.substring(1) : fromRootPath;
            S3Facade.put(folderName, out.toByteArray(), contentStream.getMimeType());
        } else {
            folderName = this.id + name;
            S3Facade.put(folderName, out.toByteArray(), contentStream.getMimeType());
        }
        return new CmisS3Document(this.session, IRepository.SEPARATOR + folderName, name);
    }

    /**
     * Gets the children.
     *
     * @return the children
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<CmisS3Object> getChildren() throws IOException {
        List<CmisS3Object> children = new ArrayList<CmisS3Object>();

        String path;
        if (this.id.startsWith(IRepository.SEPARATOR) && this.id.length() > 1) {
            path = this.id.substring(1);
        } else {
            path = this.id;
        }

        List<S3Object> s3CurrentFolderObjects = S3Facade.listObjects(path);
        for (S3Object s3Object : s3CurrentFolderObjects) {
            int segmentsPath = CmisS3Utils.pathSegmentsLength(path);
            int segmentsObject = CmisS3Utils.pathSegmentsLength(s3Object.key());
            if (segmentsObject == segmentsPath + 1) {
                if (s3Object.key()
                            .endsWith(IRepository.SEPARATOR)) {
                    if (!s3Object.key()
                                 .equals(path)) {
                        children.add(new CmisS3Folder(this.session, this.id, CmisS3Utils.findCurrentFolder((s3Object.key()))));
                    }
                } else {
                    children.add(new CmisS3Document(this.session, this.id, CmisS3Utils.findCurrentFile(s3Object.key())));
                }
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

    /**
     * Returns the parent CmisS3Folder of this CmisS3Folder.
     *
     * @return CmisS3Folder
     * @throws IOException IO Exception
     */
    public CmisS3Folder getFolderParent() throws IOException {
        if (CmisS3Utils.findParentFolder(this.id) != null) {
            return new CmisS3Folder(this.session, this.id, CmisS3Utils.findCurrentFolder(this.id));
        }
        return new CmisS3Folder(this.session);
    }
}
