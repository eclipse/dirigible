/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.api;

import static java.text.MessageFormat.format;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing CMS Providers instantiation and binding.
 */
public class CmsModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(CmsModule.class);

	private static final ServiceLoader<ICmsProvider> CMS_PROVIDERS = ServiceLoader.load(ICmsProvider.class);

	private static final String MODULE_NAME = "CMS Module";

	private static ICmsProvider provider;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		Configuration.loadModuleConfig("/dirigible-cms.properties");

		String cmsProvider = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_PROVIDER,
				ICmsProvider.DIRIGIBLE_CMS_PROVIDER_INTERNAL);
		for (ICmsProvider next : CMS_PROVIDERS) {
			logger.trace(format("Installing CMS Provider [{0}:{1}] ...", next.getType(), next.getName()));
			if (next.getType().equals(cmsProvider)) {
				bind(ICmsProvider.class).toInstance(next);
				provider = next;
			}
			logger.trace(format("Done installing CMS Provider [{0}:{1}].", next.getType(), next.getName()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName(
	 * )
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

	/**
	 * The bound CMIS session
	 *
	 * @return the session
	 */
	public static Object getSession() {
		if (provider != null) {
			return provider.getSession();
		}
		throw new IllegalStateException("CMIS Provider has not been initialized.");
	}

}
