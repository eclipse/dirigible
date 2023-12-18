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
package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.s3.repository.CmisRepository;
import org.eclipse.dirigible.components.engine.cms.s3.repository.CmisRepositoryFactory;
import org.eclipse.dirigible.components.engine.cms.s3.repository.CmisS3Session;

import java.io.IOException;

/**
 * The Class CmsProviderS3.
 */
public class CmsProviderS3 implements CmsProvider {

    /**
     * The Constant CMS.
     */
    private static final String CMS = "cms"; //$NON-NLS-1$

    /**
     * The Constant NAME.
     */
    public static final String NAME = "repository"; //$NON-NLS-1$

    /**
     * The Constant TYPE.
     */
    public static final String TYPE = "s3"; //$NON-NLS-1$

    /**
     * The cmis repository.
     */
    private CmisRepository cmisRepository;

    /**
     * The S3Facade
     */
    private String S3_ROOT = "/";

    /**
     * Instantiates a new cms provider s3.
     */
    public CmsProviderS3() throws IOException {
        this.cmisRepository = CmisRepositoryFactory.createCmisRepository(S3_ROOT);
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    @Override
    public Object getSession() {
        CmisS3Session cmisSession = this.cmisRepository.getSession();
        return cmisSession;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return TYPE;
    }

}
