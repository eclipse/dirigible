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
package org.eclipse.dirigible.components.engine.cms.s3.config;

import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.s3.provider.CmsProviderS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * The Class CmsS3Config.
 */
@Configuration
public class CmsS3Config {

    /**
     * Gets the cms provider.
     *
     * @return the cms provider
     */
    @Bean("cms-provider-s3")
    public CmsProvider getCmsProvider() throws IOException {
        return new CmsProviderS3();
    }

}
