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

    /**
     * The internal folder.
     */

    private String internalFolder;

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
        super(session, IRepository.SEPARATOR);
        this.session = session;
        this.internalFolder = ROOT;
        this.rootFolder = true;
    }

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
        String folderName;
        String name = properties.get(CmisConstants.NAME);
        if (!Objects.equals(this.internalFolder, ROOT)) {
            String fromRootPath = this.internalFolder + name + IRepository.SEPARATOR;

            //remove root "/" as it interferes with the UI
            folderName = fromRootPath.startsWith(IRepository.SEPARATOR) ? fromRootPath.substring(1) : fromRootPath;
            S3Facade.put(folderName, new byte[0]);
            return new CmisS3Folder(this.session, fromRootPath);
        } else {
            folderName = name + IRepository.SEPARATOR;
            S3Facade.put(folderName, new byte[0]);
            return new CmisS3Folder(this.session, folderName);
        }
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
        String folderName;
        if (!Objects.equals(this.internalFolder, ROOT)) {
            String fromRootPath = this.internalFolder + name;
            folderName = fromRootPath.startsWith(IRepository.SEPARATOR) ? fromRootPath.substring(1) : fromRootPath;
            S3Facade.put(folderName, out.toByteArray());
        } else {
            folderName = this.internalFolder + name;
            S3Facade.put(folderName, out.toByteArray());
        }
        return new CmisS3Document(this.session, folderName);
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
        if (this.internalFolder.startsWith(IRepository.SEPARATOR) && this.internalFolder.length() > 1) {
            path = this.internalFolder.substring(1);
        } else {
            path = this.internalFolder;
        }

        List<S3Object> s3CurrentFolderObjects = S3Facade.listObjects(path);

        for (S3Object s3Object : s3CurrentFolderObjects) {
            if (s3Object.key().equals(path)) {
                return children;
            }
            if (s3Object.key().endsWith(IRepository.SEPARATOR)) {
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

    /**
     * Returns the parent CmisS3Folder of this CmisS3Folder.
     *
     * @return CmisS3Folder
     * @throws IOException IO Exception
     */
    public CmisS3Folder getFolderParent() throws IOException {
        if (findParentFolder(this.internalFolder) != null) {
            return new CmisS3Folder(this.session, findParentFolder(this.internalFolder));
        }
        return new CmisS3Folder(this.session);
    }

    private static String findParentFolder(String folderPath) {
        if (Objects.equals(folderPath, ROOT)) {
            return null;
        }

        String[] parts = folderPath.split(IRepository.SEPARATOR);
        if (parts.length >= 3) {
            int secondToLastIndex = parts.length - 2;
            return parts[secondToLastIndex] + IRepository.SEPARATOR;
        } else {
            return IRepository.SEPARATOR;
        }
    }

//    private String findParentFolder(String folderPath) {
//        String[] pathElements = folderPath.split("/");
//
//        if (folderPath.equals("/")) {
//            return "/";
//        }
//
//        // Find the last non-empty element without using a for loop
//        String secondToLastElement = Arrays.stream(pathElements)
//                .filter(s -> !s.isEmpty())
//                .skip(Math.max(0, pathElements.length - 2)) // Skip to the second-to-last element
//                .findFirst()
//                .orElse(null);
//
//        if (secondToLastElement == null) {
//            return null;
//        }
//
//        return secondToLastElement + "/";
//    }

}
