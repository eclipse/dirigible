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
package org.eclipse.dirigible.components.cms.internal;

import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.cms.internal.CmsProviderInternal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmsInternalConfig {
	
	@Bean("CMS_PROVIDER")
	public ICmsProvider getCmsProvider() {
		return new CmsProviderInternal();
	}

}
