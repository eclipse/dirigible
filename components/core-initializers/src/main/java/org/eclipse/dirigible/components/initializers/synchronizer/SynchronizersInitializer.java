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
package org.eclipse.dirigible.components.initializers.synchronizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SynchronizersInitializer {

	private final SynchronizationProcessor synchronizationProcessor;

	@Autowired
	public SynchronizersInitializer(SynchronizationProcessor synchronizationProcessor) {
		this.synchronizationProcessor = synchronizationProcessor;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void handleContextStart(final ApplicationReadyEvent are) {
		synchronizationProcessor.processSynchronizers();
	}

}
