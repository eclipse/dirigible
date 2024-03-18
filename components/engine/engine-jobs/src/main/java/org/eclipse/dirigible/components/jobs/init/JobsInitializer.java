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
package org.eclipse.dirigible.components.jobs.init;

import org.eclipse.dirigible.components.base.ApplicationListenersOrder.ApplicationReadyEventListeners;
import org.eclipse.dirigible.components.jobs.DirigibleJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Order(ApplicationReadyEventListeners.JOBS_INITIALIZER)
@Component
class JobsInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobsInitializer.class);

    private final Set<DirigibleJob> jobs;
    private final Scheduler scheduler;

    JobsInitializer(Set<DirigibleJob> jobs, Scheduler scheduler) {
        this.jobs = jobs;
        this.scheduler = scheduler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Initializing [{}] jobs: {}}", jobs.size(), jobs);

        jobs.forEach(job -> {
            JobDetail jobDetail = job.createJob();
            try {
                if (!scheduler.checkExists(jobDetail.getKey())) {
                    Trigger trigger = job.createTrigger();
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            } catch (SchedulerException ex) {
                throw new IllegalStateException("Failed to check the existence of job " + job, ex);
            }
        });

        LOGGER.info("Completed.");
    }

}
