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
package org.eclipse.dirigible.components.engine.camel.config;

import org.apache.camel.CamelContext;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.component.platform.http.spi.PlatformHttpEngine;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.eclipse.dirigible.components.engine.camel.processor.CamelDirigibleRequestHandlerMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CamelDirigibleConfiguration {

    @Bean
    @Primary
    CamelRequestHandlerMapping createCamelRequestHandlerMapping(CamelContext camelContext, PlatformHttpEngine httpEngine,
            CamelRequestHandlerMapping camelRequestHandlerMapping) {
        var httpComponent = camelContext.getComponent("platform-http", PlatformHttpComponent.class);
        httpComponent.removePlatformHttpListener(camelRequestHandlerMapping);
        return new CamelDirigibleRequestHandlerMapping(httpComponent, httpEngine);
    }

    @Bean
    SpringBootCamelContext createSpringBootCamelContext(ApplicationContext applicationContext) {
        return new SpringBootCamelContext(applicationContext, true);
    }

}
