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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.eclipse.dirigible.components.jobs.repository.JobLogRepository;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Deprovisioning deletes the tenant's jobs - and only the tenant's: not another tenant's, and never
 * an internal one.
 */
class JobsTenantTeardownTest {

    private final Scheduler scheduler = mock(Scheduler.class);
    private final JobLogRepository jobLogs = mock(JobLogRepository.class);
    private final JobsTenantTeardown teardown = new JobsTenantTeardown(scheduler, jobLogs);

    @Test
    void theTenantsJobsAndLogsAreDeletedAndNothingElse() throws SchedulerException {
        JobKey acme = new JobKey("acme###nightly", "defined");
        JobKey acmeTwin = new JobKey("acme-twin###nightly", "defined");
        JobKey defaultTenant = new JobKey("nightly", "defined");
        when(scheduler.getJobKeys(any())).thenReturn(Set.of(acme, acmeTwin, defaultTenant));
        when(scheduler.deleteJob(acme)).thenReturn(true);

        teardown.teardown("acme");

        verify(scheduler).deleteJob(acme);
        verify(scheduler, never()).deleteJob(acmeTwin);
        verify(scheduler, never()).deleteJob(defaultTenant);
        verify(jobLogs).deleteByTenantId("acme");
    }
}
