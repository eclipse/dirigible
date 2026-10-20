/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.jobs.manager;

import java.util.Set;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.eclipse.dirigible.components.jobs.repository.JobLogRepository;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the tenant's scheduled jobs - every Quartz job named
 * {@code <tenantId>###<name>} in the user-defined group, the {@code .job} artefacts and the
 * client-Java {@code @Scheduled} ones alike - and the tenant's job logs. Internal platform jobs are
 * never touched.
 */
@Component
class JobsTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsTenantTeardown.class);

    /** The order of this participant: what runs for the tenant goes first. */
    static final int ORDER = 100;

    /** The group the tenant-scoped user jobs are scheduled in. */
    private static final String JOB_GROUP_DEFINED = "defined";

    /** The scheduler. */
    private final Scheduler scheduler;

    /** The job logs. */
    private final JobLogRepository jobLogs;

    /**
     * Instantiates a new jobs teardown.
     *
     * @param scheduler the scheduler
     * @param jobLogs the job logs
     */
    JobsTenantTeardown(Scheduler scheduler, JobLogRepository jobLogs) {
        this.scheduler = scheduler;
        this.jobLogs = jobLogs;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        String prefix = tenantId + "###";
        int deleted = 0;
        try {
            Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JOB_GROUP_DEFINED));
            for (JobKey key : keys) {
                if (key.getName()
                       .startsWith(prefix) && scheduler.deleteJob(key)) {
                    deleted++;
                }
            }
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Could not delete the scheduled jobs of tenant [" + tenantId + "]: " + ex.getMessage(), ex);
        }
        long logs = jobLogs.deleteByTenantId(tenantId);
        LOGGER.info("Deleted [{}] scheduled job(s) and [{}] job log(s) of tenant [{}].", deleted, logs, LogSanitizer.sanitize(tenantId));
    }
}
