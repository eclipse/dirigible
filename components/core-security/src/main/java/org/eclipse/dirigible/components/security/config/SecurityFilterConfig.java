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
package org.eclipse.dirigible.components.security.config;

import org.eclipse.dirigible.components.security.filter.SecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterRegistrationBean(SecurityFilter securityFilter) {
        FilterRegistrationBean<SecurityFilter> filterRegistrationBean = new FilterRegistrationBean<>(securityFilter);

        filterRegistrationBean.setFilter(securityFilter);
        filterRegistrationBean.addUrlPatterns(
                "/services/v3/js/*",
                "/services/v3/rhino/*",
                "/services/v3/nashorn/*",
                "/services/v3/v8/*",
                "/services/v3/public/*",
                "/services/v3/web/*",
                "/services/v3/wiki/*",
                "/services/v3/command/*",

                "/public/v3/js/*",
                "/public/v3/rhino/*",
                "/public/v3/nashorn/*",
                "/public/v3/v8/*",
                "/public/v3/public/*",
                "/public/v3/web/*",
                "/public/v3/wiki/*",
                "/public/v3/command/*",

                "/services/v4/js/*",
                "/services/v4/rhino/*",
                "/services/v4/nashorn/*",
                "/services/v4/v8/*",
                "/services/v4/public/*",
                "/services/v4/web/*",
                "/services/v4/wiki/*",
                "/services/v4/command/*",

                "/public/v4/js/*",
                "/public/v4/rhino/*",
                "/public/v4/nashorn/*",
                "/public/v4/v8/*",
                "/public/v4/public/*",
                "/public/v4/web/*",
                "/public/v4/wiki/*",
                "/public/v4/command/*",

                "/odata/v2/*"
        );

        return filterRegistrationBean;
    }
}
