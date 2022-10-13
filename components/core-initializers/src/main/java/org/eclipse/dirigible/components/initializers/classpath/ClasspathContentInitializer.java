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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * The Class ClasspathContentInitializer.
 */
@Component
public class ClasspathContentInitializer {
	
	/** The classpath expander. */
	@Autowired
	private ClasspathExpander classpathExpander;
	
	/**
	 * Handle context start.
	 *
	 * @param are the are
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void handleContextStart(final ApplicationReadyEvent are) {
        classpathExpander.expandContent();
	}
	
}
