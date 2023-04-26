/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.initializers.synchronizer;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.components.initializers.SynchronousSpringEventsConfig;
import org.eclipse.dirigible.components.repository.RepositoryConfig;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * The Class SynchronizationInitializerTest.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SynchronousSpringEventsConfig.class }, loader = AnnotationConfigContextLoader.class)
@EntityScan("org.eclipse.dirigible.components")
public class SynchronizationInitializerTest {
	
	/**
	 * The Class ContextConfiguration.
	 */
	@Configuration
	@ComponentScan("org.eclipse.dirigible.components")
    static class ContextConfiguration {

        /**
         * Repository.
         *
         * @return the i repository
         */
        @Bean("SynchronizationInitializerTestReposiotry")
        public IRepository repository() {
            return new RepositoryConfig().repository();
        }
        
    }
	
	/** The listener. */
	@Autowired
    private SynchronizationInitializer listener;
	
	/** The repository. */
	@Autowired
    private IRepository repository;
	
	/**
	 * Test context started handler.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
    public void testContextStartedHandler() throws RepositoryWriteException, IOException {
		System.out.println("Test context started listener.");
		repository.createResource("/registry/public/test/test.extensionpoint", 
				IOUtils.toByteArray(SynchronizationInitializerTest.class.getResourceAsStream(
						"/META-INF/dirigible/test/test.extensionpoint")));
		listener.handleContextStart(null);
    }
	
	/**
	 * The Class TestConfiguration.
	 */
	@SpringBootApplication
	static class TestConfiguration {
	}

}
