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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.component.platform.http.spi.PlatformHttpEngine;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;
import org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ExtendWith(MockitoExtension.class)
class CamelDirigibleConfigurationTest {

    @Mock
    private CamelContext camelContext;

    @Mock
    private CamelRequestHandlerMapping camelRequestHandlerMapping;

    @Mock
    private PlatformHttpEngine httpEngine;

    @Mock
    private PlatformHttpComponent httpComponent;

    @Test
    void testCreateCamelRequestHandlerMapping() {
        when(camelContext.getComponent("platform-http", PlatformHttpComponent.class)).thenReturn(httpComponent);

        CamelRequestHandlerMapping res =
                new CamelDirigibleConfiguration().createCamelRequestHandlerMapping(camelContext, httpEngine, camelRequestHandlerMapping);

        verify(httpComponent, times(1)).removePlatformHttpListener(camelRequestHandlerMapping);
        assertNotNull(res, "CamelRequestHandlerMapping should not be null");
    }

    @Test
    void testCamelConfigurationHasBeanFactoryMethodWeDependOn() {
        try {
            var platformHttpEngineRequestMappingMethod =
                    SpringBootPlatformHttpAutoConfiguration.class.getMethod("platformHttpEngineRequestMapping", PlatformHttpEngine.class);
            assertEquals(CamelRequestHandlerMapping.class, platformHttpEngineRequestMappingMethod.getReturnType(),
                    "SpringBootPlatformHttpAutoConfiguration::platformHttpEngineRequestMapping does not return expected type");
        } catch (NoSuchMethodException e) {
            fail("SpringBootPlatformHttpAutoConfiguration::platformHttpEngineRequestMapping does not exist", e);
        }
    }

    @Test
    void testSpringBootPlatformHttpAutoConfigurationFullName() {
        assertEquals("org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpAutoConfiguration",
                SpringBootPlatformHttpAutoConfiguration.class.getName(),
                "Unexpected SpringBootPlatformHttpAutoConfiguration full class name");
    }

    @Test
    void testCamelDirigibleConfigurationCorrectSpringAnnotations() {
        var classAnnotations = CamelDirigibleConfiguration.class.getAnnotations();
        assertEquals(1, classAnnotations.length, "Unexpected number of annotations");

        var configuration = firstAnnotationWithClass(classAnnotations, Configuration.class);
        assertTrue(configuration.isPresent(), "No @Configuration found on CamelDirigibleConfiguration");
    }

    @Test
    void testCamelDirigibleConfigurationCorrectSpringBeanAnnotations() {
        Method beanFactoryMethod = getBeanFactoryMethodOrFail();

        var beanFactoryMethodAnnotations = beanFactoryMethod.getAnnotations();
        assertEquals(2, beanFactoryMethodAnnotations.length, "Unexpected number of annotations");

        var bean = firstAnnotationWithClass(beanFactoryMethodAnnotations, Bean.class);
        assertTrue(bean.isPresent(), "No @Bean found on CamelDirigibleConfiguration");

        var primary = firstAnnotationWithClass(beanFactoryMethodAnnotations, Primary.class);
        assertTrue(primary.isPresent(), "No @Primary found on CamelDirigibleConfiguration");
    }

    @Test
    void testCamelDirigibleConfigurationBeanFactoryMethodReturnType() {
        Method beanFactoryMethod = getBeanFactoryMethodOrFail();
        assertEquals(CamelRequestHandlerMapping.class, beanFactoryMethod.getReturnType(),
                "Unexpected CamelDirigibleConfiguration::createCamelRequestHandlerMapping return type");
    }

    private static Optional<Annotation> firstAnnotationWithClass(Annotation[] annotations, Class<?> annotationClass) {
        return Arrays.stream(annotations)
                     .filter(a -> a.annotationType()
                                   .equals(annotationClass))
                     .findFirst();
    }

    private static Method getBeanFactoryMethodOrFail() {
        try {
            return CamelDirigibleConfiguration.class.getMethod("createCamelRequestHandlerMapping", CamelContext.class,
                    PlatformHttpEngine.class, CamelRequestHandlerMapping.class);
        } catch (Exception e) {
            fail("CamelDirigibleConfiguration::createCamelRequestHandlerMapping does not exist");
            throw new RuntimeException(e);
        }
    }
}
