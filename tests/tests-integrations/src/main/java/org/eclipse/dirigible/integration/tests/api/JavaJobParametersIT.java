/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.components.jobs.service.JobService;
import org.eclipse.dirigible.components.jobs.tenant.JobNameCreator;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * A {@code .job} artefact running a client-Java bean method declares parameters, and the method
 * receives them (#8017): the declared defaults on a scheduled fire, the values a manual trigger
 * passes on a trigger. The parameters used to stop at the jobs engine - and, before that, at the
 * parser, which dropped every declaration, so a trigger refused every value.
 *
 * <p>
 * The method publishes what it received to a queue the test draws from. The schedule never fires on
 * its own: the scheduled path is driven by triggering the job's Quartz key, which runs exactly what
 * a cron tick runs.
 */
// One Dirigible boot for the class: both cases read the same published job.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class JavaJobParametersIT extends IntegrationTest {

    private static final String PROJECT = "java-job-parameters-it";

    /** The job's name - the base name of its artefact. */
    private static final String JOB_NAME = "java-job-parameters-it";

    /** The user-defined job group - the one routed through the handler/engine dispatch. */
    private static final String JOB_GROUP = "defined";

    private static final String QUEUE = "java-job-parameters-it-out";

    /** A cron that never fires: the test runs the job itself, and nothing else should. */
    private static final String NEVER = "0 0 0 1 1 ? 2099";

    /** Covers the compile + registration lag after a forced synchronization. */
    private static final int REGISTRATION_TIMEOUT_SECONDS = 90;

    /** Per attempt - short, because a failed attempt is retried. */
    private static final long RECEIVE_TIMEOUT_MILLIS = 2000;

    private static final String JOB_SOURCE = """
            {
                "expression": "%s",
                "group": "%s",
                "handler": "demo.Reports#daily",
                "engine": "java",
                "description": "A Java job with parameters",
                "parameters": [
                    { "name": "region", "type": "string", "defaultValue": "EU" },
                    { "name": "limit", "type": "number", "defaultValue": "10" }
                ]
            }
            """.formatted(NEVER, JOB_GROUP);

    private static final String HANDLER_SOURCE = """
            package demo;

            import java.util.Map;

            import org.eclipse.dirigible.sdk.component.Component;
            import org.eclipse.dirigible.sdk.messaging.Producer;

            @Component
            public class Reports {

                public void daily(Map<String, String> parameters) {
                    Producer.sendToQueue("%s", parameters.get("region") + "/" + parameters.get("limit"));
                }
            }
            """.formatted(QUEUE);

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private TenantContext tenantContext;

    @Autowired
    @DefaultTenant
    private Tenant defaultTenant;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobNameCreator jobNameCreator;

    @Autowired
    private JobService jobService;

    /** Publishes the job and its handler - idempotent, so each case can rely on it. */
    @BeforeEach
    void publishTheJob() {
        write("/demo/Reports.java", HANDLER_SOURCE, "text/x-java");
        write("/" + JOB_NAME + ".job", JOB_SOURCE, "application/json");
        synchronizationProcessor.forceProcessSynchronizers();
    }

    @Test
    void aScheduledFireHandsTheMethodTheDeclaredDefaults() {
        Awaitility.await()
                  .pollInterval(1, TimeUnit.SECONDS)
                  .atMost(REGISTRATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                  .until(() -> tenantContext.execute(defaultTenant, () -> {
                      JobKey jobKey = JobKey.jobKey(jobNameCreator.toTenantName(JOB_NAME), JOB_GROUP);
                      if (!scheduler.checkExists(jobKey)) {
                          return false;
                      }
                      // a fire before the class is compiled fails its run - and is simply fired again
                      scheduler.triggerJob(jobKey);
                      return received("EU/10");
                  }));
    }

    @Test
    void aTriggerHandsTheMethodTheValuesItPasses() {
        Awaitility.await()
                  .pollInterval(1, TimeUnit.SECONDS)
                  .atMost(REGISTRATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                  .ignoreExceptions()
                  .until(() -> tenantContext.execute(defaultTenant, () -> {
                      // runs the job on this thread - it throws while the class is not compiled yet
                      jobService.trigger(JOB_NAME, Map.of("region", "US"));
                      return received("US/10");
                  }));
    }

    /**
     * Draws the queue until the expected message arrives or it stays empty. A scheduled fire that ran
     * late may have left an earlier run's message in front of it.
     */
    private boolean received(String expected) {
        while (true) {
            String message;
            try {
                message = MessagingFacade.receiveFromQueue(QUEUE, RECEIVE_TIMEOUT_MILLIS);
            } catch (RuntimeException empty) {
                return false;
            }
            if (expected.equals(message)) {
                return true;
            }
        }
    }

    private void write(String path, String content, String contentType) {
        repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + path,
                content.getBytes(StandardCharsets.UTF_8), false, contentType, true);
    }
}
