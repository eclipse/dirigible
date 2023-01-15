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
package org.eclipse.dirigible.components.initializers.classpath;

import org.eclipse.dirigible.components.initializers.SynchronousSpringEventsConfig;
import org.eclipse.dirigible.components.repository.RepositoryConfig;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SynchronousSpringEventsConfig.class }, loader = AnnotationConfigContextLoader.class)
@EntityScan("org.eclipse.dirigible.components") 
public class ClasspathContentInitializerTest {
	
	@Configuration
    static class ContextConfiguration {

        @Bean("ClasspathContentInitializerTestRepository")
        public IRepository repository() {
            return new RepositoryConfig().repository();
        }
        
    }
	
	@Autowired
    private ClasspathContentInitializer listener;
	
	@Test
    public void testContextStartedHandler() {
		System.out.println("Test context started listener.");
		listener.handleContextStart(null);
    }
	
	@SpringBootApplication
	static class TestConfiguration {
	}

}
