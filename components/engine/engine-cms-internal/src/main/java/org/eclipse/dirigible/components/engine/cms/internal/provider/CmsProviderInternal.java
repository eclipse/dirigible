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
package org.eclipse.dirigible.components.engine.cms.internal.provider;

import java.io.File;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.internal.repository.CmisInternalSession;
import org.eclipse.dirigible.components.engine.cms.internal.repository.CmisRepository;
import org.eclipse.dirigible.components.engine.cms.internal.repository.CmisRepositoryFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;

/**
 * The Class CmsProviderInternal.
 */
public class CmsProviderInternal implements CmsProvider {

    /** The Constant CMS. */
    private static final String CMS = "cms"; //$NON-NLS-1$

    /** The Constant NAME. */
    public static final String NAME = "repository"; //$NON-NLS-1$

    /** The Constant TYPE. */
    public static final String TYPE = "internal"; //$NON-NLS-1$

    /** The cmis repository. */
    private CmisRepository cmisRepository;

    /**
     * Instantiates a new cms provider internal.
     */
    public CmsProviderInternal() {

        String rootFolder = Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER, "target/dirigible");
        boolean absolute = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE, "false"));

        String repositoryFolder = rootFolder + File.separator + CMS;

        boolean versioningEnabled = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_INTERNAL_VERSIONING_ENABLED, "false"));
        IRepository repository = new LocalRepository(repositoryFolder, absolute, versioningEnabled);
        this.cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    @Override
    public Object getSession() {
        CmisInternalSession cmisSession = this.cmisRepository.getSession();
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
