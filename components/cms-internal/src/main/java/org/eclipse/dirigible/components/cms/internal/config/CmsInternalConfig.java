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
package org.eclipse.dirigible.components.cms.internal.config;

import org.eclipse.dirigible.components.base.cms.CmsProvider;
import org.eclipse.dirigible.components.cms.internal.provider.CmsProviderInternal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class CmsInternalConfig.
 */
@Configuration
public class CmsInternalConfig {
	
	/**
	 * Gets the cms provider.
	 *
	 * @return the cms provider
	 */
	@Bean("CMS_PROVIDER")
	public CmsProvider getCmsProvider() {
		return new CmsProviderInternal();
	}

}
