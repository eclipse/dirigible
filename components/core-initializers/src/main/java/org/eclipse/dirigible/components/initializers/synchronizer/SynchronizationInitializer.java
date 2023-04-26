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

import org.eclipse.dirigible.components.initializers.classpath.ClasspathExpander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * The Class SynchronizersInitializer.
 */
@Component
@Scope("singleton")
public class SynchronizationInitializer {

	/** The synchronization processor. */
	private final SynchronizationProcessor synchronizationProcessor;
	
	/** The classpath expander. */
	private ClasspathExpander classpathExpander;

	/**
	 * Instantiates a new synchronizers initializer.
	 *
	 * @param synchronizationProcessor the synchronization processor
	 */
	@Autowired
	public SynchronizationInitializer(SynchronizationProcessor synchronizationProcessor, ClasspathExpander classpathExpander) {
		this.synchronizationProcessor = synchronizationProcessor;
		this.classpathExpander = classpathExpander;
	}

	/**
	 * Handle context start.
	 *
	 * @param are the are
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void handleContextStart(final ApplicationReadyEvent are) {
		synchronizationProcessor.prepareSynchronizers();
		classpathExpander.expandContent();
		synchronizationProcessor.processSynchronizers();
	}

}
