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

import java.io.IOException;

/**
 * The Class CmisS3Repository.
 */
public class CmisS3Repository implements CmisRepository {

    /**
     * The S3Facade representation of repository.
     */
    private String S3_ROOT = "/";

    /**
     * Instantiates a new cmis s3 repository.
     */
    public CmisS3Repository(String root) throws IOException {
        super();
        this.S3_ROOT = root;
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    @Override
    public CmisS3Session getSession() {
        return new CmisS3Session(this);
    }

}
