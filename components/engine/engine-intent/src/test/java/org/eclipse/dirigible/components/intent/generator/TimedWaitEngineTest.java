/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * The polling loop a timed {@code wait} exists for, generated from the intent and run on a REAL
 * Flowable engine (in-memory, async executor on): the check answers "not yet" twice and then
 * "done", and every "not yet" is a pause on the timer - parked at the wait step - rather than a
 * failed job, so the instance completes through {@code publish} with no job failure and no ERROR
 * logged.
 */
class TimedWaitEngineTest {

    private static final String YAML = """
            name: archive
            processes:
              - name: ExportArchive
                vars:
                  - { name: exportState }
                steps:
                  - { name: checkExport, kind: serviceTask, args: { delegate: %s, produces: [exportState], next: exportDone } }
                  - { name: exportDone,  kind: decision,    args: { if: "exportState == 'DONE'", then: publish, else: pause } }
                  - { name: pause,       kind: wait,        args: { after: PT1S, next: checkExport } }
                  - { name: publish,     kind: serviceTask, args: { delegate: %s, next: done } }
                  - { name: done, kind: end }
            """.formatted(CheckExportTask.class.getName(), PublishTask.class.getName());

    private static ProcessEngine engine;

    private ListAppender<ILoggingEvent> appender;

    /** Reads the remote state once: RUNNING on the first two checks, DONE on the third. */
    public static class CheckExportTask implements JavaDelegate {

        static final AtomicInteger CHECKS = new AtomicInteger();

        @Override
        public void execute(DelegateExecution execution) {
            execution.setVariable("exportState", CHECKS.incrementAndGet() < 3 ? "RUNNING" : "DONE");
        }
    }

    /** Records that the flow reached the step after the loop. */
    public static class PublishTask implements JavaDelegate {

        @Override
        public void execute(DelegateExecution execution) {
            execution.setVariable("published", CheckExportTask.CHECKS.get());
        }
    }

    @BeforeAll
    static void startEngine() {
        StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
        configuration.setJdbcUrl("jdbc:h2:mem:timed-wait-test;DB_CLOSE_DELAY=1000");
        configuration.setAsyncExecutorActivate(true);
        // Tight acquire cycles so the PT1S pauses dominate the test's wall clock.
        configuration.getAsyncExecutorConfiguration()
                     .setDefaultAsyncJobAcquireWaitTime(Duration.ofMillis(100));
        configuration.getAsyncExecutorConfiguration()
                     .setDefaultTimerJobAcquireWaitTime(Duration.ofMillis(100));
        engine = configuration.buildProcessEngine();
    }

    @AfterAll
    static void stopEngine() {
        if (engine != null) {
            engine.close();
        }
    }

    @BeforeEach
    void captureLog() {
        appender = new ListAppender<>();
        appender.start();
        root().addAppender(appender);
    }

    @AfterEach
    void releaseLog() {
        root().detachAppender(appender);
    }

    @Test
    void theLoopPollsOnTheTimerAndContinuesOnceTheCheckIsDone() {
        engine.getRepositoryService()
              .createDeployment()
              .addString("ExportArchive.bpmn20.xml", TimedWaitBpmnTest.bpmn(YAML))
              .deploy();
        RuntimeService runtime = engine.getRuntimeService();
        ProcessInstance instance = runtime.startProcessInstanceByKey("ExportArchive");

        await().atMost(Duration.ofSeconds(30))
               .pollInterval(Duration.ofMillis(50))
               .until(() -> runtime.getActiveActivityIds(instance.getId())
                                   .equals(List.of("pause")));
        assertEquals(1, engine.getManagementService()
                              .createTimerJobQuery()
                              .processInstanceId(instance.getId())
                              .count(),
                "a parked instance waits on the pause step's timer");

        await().atMost(Duration.ofSeconds(60))
               .pollInterval(Duration.ofMillis(250))
               .until(() -> engine.getHistoryService()
                                  .createHistoricProcessInstanceQuery()
                                  .processInstanceId(instance.getId())
                                  .finished()
                                  .count() == 1);

        assertEquals(3, engine.getHistoryService()
                              .createHistoricVariableInstanceQuery()
                              .processInstanceId(instance.getId())
                              .variableName("published")
                              .singleResult()
                              .getValue(),
                "the flow must continue at publish after the third check answered DONE");
        assertEquals(2, engine.getHistoryService()
                              .createHistoricActivityInstanceQuery()
                              .processInstanceId(instance.getId())
                              .activityId("pause")
                              .count(),
                "each RUNNING answer must pause once");
        assertEquals(0, engine.getManagementService()
                              .createDeadLetterJobQuery()
                              .count(),
                "an expected wait must never fail a job");
        List<String> errors = appender.list.stream()
                                           .filter(event -> event.getLevel() == Level.ERROR)
                                           .map(ILoggingEvent::getFormattedMessage)
                                           .toList();
        assertTrue(errors.isEmpty(), "an expected wait must log no ERROR, got: " + errors);
    }

    private static Logger root() {
        return (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }
}
