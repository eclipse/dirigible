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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class SynchronizationJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(SynchronizationJob.class);
	
	@Autowired
    private SynchronizationJobService jobService;

    public void execute(JobExecutionContext context) throws JobExecutionException {

        logger.debug("Job {} fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        jobService.executeSynchronizationJob();

        logger.debug("Next job scheduled @ {}", context.getNextFireTime());
    }

}
