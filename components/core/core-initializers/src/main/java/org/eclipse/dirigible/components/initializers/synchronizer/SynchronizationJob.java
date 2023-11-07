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
package org.eclipse.dirigible.components.initializers.synchronizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The Class SynchronizationJob.
 */
@Component
@Scope("singleton")
public class SynchronizationJob implements Job {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SynchronizationJob.class);

	/** The executor. */
	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	/** The job service. */
	@Autowired
	private SynchronizationJobService jobService;

	/**
	 * Execute.
	 *
	 * @param context the context
	 * @throws JobExecutionException the job execution exception
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("Job {} fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

		executor.submit(() -> {
			Runtime runtime = Runtime.getRuntime();
			runtime.gc();
			long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Used memory at the start: %-+,15d", usedMemoryBefore));
			}

			jobService.executeSynchronizationJob();

			runtime.gc();
			long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Used memory at the end:   %-+,15d / delta: %-+,15d", usedMemoryAfter,
						(usedMemoryAfter - usedMemoryBefore)));
			}

		});

		logger.debug("Next job scheduled @ {}", context.getNextFireTime());
	}

}
