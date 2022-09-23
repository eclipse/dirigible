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

/**
 * The Class IDECoreSynchronizer.
 */
public class IDECoreSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IDECoreSynchronizer.class);

	/** The engine. */
	private IJavascriptEngineExecutor engine  = null;
	
	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	protected synchronized IJavascriptEngineExecutor getEngine() {
		if (engine == null) {
			engine = (IJavascriptEngineExecutor) StaticObjects.get(StaticObjects.JAVASCRIPT_ENGINE);
		}
		return engine;
	}

	/**
	 * Synchronize.
	 */
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
						getEngine().executeServiceCode(code, new HashMap<Object, Object>());
					} catch (ScriptingException e) {
						if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
					}
				});
			} else {
				if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}

	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		// Do nothing
	}

}
