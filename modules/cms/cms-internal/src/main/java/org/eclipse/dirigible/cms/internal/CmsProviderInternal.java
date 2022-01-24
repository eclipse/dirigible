/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cms.internal;

import java.io.File;

import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;

public class CmsProviderInternal implements ICmsProvider {

	private static final String CMS = "cms"; //$NON-NLS-1$

	/** The Constant NAME. */
	public static final String NAME = "repository"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "internal"; //$NON-NLS-1$

	private CmisRepository cmisRepository;

	public CmsProviderInternal() {
		Configuration.loadModuleConfig("/dirigible-cms-internal.properties");

		String rootFolder = Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE));

		String repositoryFolder = rootFolder + File.separator + CMS;

		IRepository repository = new LocalRepository(repositoryFolder, absolute);
		this.cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Object getSession() {
		CmisSession cmisSession = this.cmisRepository.getSession();
		return cmisSession;
	}

}
