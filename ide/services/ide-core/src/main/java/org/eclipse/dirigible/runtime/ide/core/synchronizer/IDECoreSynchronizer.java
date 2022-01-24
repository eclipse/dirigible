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
package org.eclipse.dirigible.runtime.ide.core.synchronizer;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.commons.health.HealthStatus;
import org.eclipse.dirigible.commons.health.HealthStatus.Jobs.JobStatus;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDECoreSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(IDECoreSynchronizer.class);

	private IJavascriptEngineExecutor engine  = (IJavascriptEngineExecutor) StaticObjects.get(StaticObjects.JAVASCRIPT_ENGINE);

	@Override
	public void synchronize() {
		try {
			if (isSynchronizationEnabled()) {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(() -> {
					HealthStatus.getInstance().getJobs().setStatus(IDECoreSynchronizerJobDefinitionProvider.IDE_CORE_SYNCHRONIZER_JOB, JobStatus.Succeeded);
					String code = new StringBuilder()
							.append("var moduleInfoCache = require(\"ide-monaco-extensions/api/utils/moduleInfoCache\");\n")
							.append("moduleInfoCache.refresh();").toString();
					try {
						engine.executeServiceCode(code, new HashMap<Object, Object>());
					} catch (ScriptingException e) {
						logger.error(e.getMessage(), e);
					}
				});
			} else {
				logger.debug("Synchronization has been disabled");
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		// Do nothing
	}

}
