/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.initializers.synchronizer;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.jobs.SystemJob;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleScheduleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * The Class SynchronizationJob.
 */
@Component
class SynchronizationJob extends SystemJob {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationJob.class);

    /** The executor. */
    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    /** The job service. */
    @Autowired
    private SynchronizationJobService jobService;

    /**
     * Gets the trigger key.
     *
     * @return the trigger key
     */
    @Override
    protected String getTriggerKey() {
        return "SynchronizationJobTrigger";
    }

    /**
     * Gets the trigger description.
     *
     * @return the trigger description
     */
    @Override
    protected String getTriggerDescription() {
        return "Synchronization trigger";
    }

    /**
     * Gets the schedule.
     *
     * @return the schedule
     */
    @Override
    protected SimpleScheduleBuilder getSchedule() {
        int frequencyInSec = DirigibleConfig.SYNCHRONIZER_FREQUENCY.getIntValue();
        logger.info("Configuring trigger to fire every [{}] seconds", frequencyInSec);

        return simpleSchedule().withIntervalInSeconds(frequencyInSec)
                               .repeatForever()
                               .withMisfireHandlingInstructionNextWithExistingCount();
    }

    /**
     * Gets the job key.
     *
     * @return the job key
     */
    @Override
    protected String getJobKey() {
        return "SynchronizationJobDetail";
    }

    /**
     * Gets the job description.
     *
     * @return the job description
     */
    @Override
    protected String getJobDescription() {
        return "Invoke Synchronization Job service...";
    }

    /**
     * Execute.
     *
     * @param context the context
     */
    @Override
    public void execute(JobExecutionContext context) {
        logger.debug("Job {} fired @ {}", context.getJobDetail()
                                                 .getKey()
                                                 .getName(),
                context.getFireTime());

        executor.submit(() -> {
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

            logger.debug(String.format("Used memory at the start: %-+,15d", usedMemoryBefore));

            jobService.executeSynchronizationJob();

            runtime.gc();
            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            logger.debug(String.format("Used memory at the end:   %-+,15d / delta: %-+,15d", usedMemoryAfter,
                    (usedMemoryAfter - usedMemoryBefore)));

        });

        logger.debug("Next job scheduled @ {}", context.getNextFireTime());
    }

}
