/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.delegate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
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
 * The whole resilience conversion against a REAL Flowable engine (in-memory, async executor on):
 * the declared retry cycle re-runs a failing {@code flowable:class} delegate, the FINAL failed
 * attempt converts to the caught intent error (routing the token through the {@code onError}
 * boundary with the failure message published for {@code {error}}), and a delegate that recovers
 * within its cycle completes normally with no conversion at all. This is what proves the
 * final-attempt arithmetic against the engine's own {@code JobRetryCmd}, not a mirror of it.
 */
class ResilientClassDelegateEngineTest {

    private static final String PROCESS_XML_TEMPLATE =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:flowable="http://flowable.org/bpmn" targetNamespace="http://www.flowable.org/processdef">
                      <error id="intentStepError" name="Intent Step Error" errorCode="INTENT_STEP_FAILED"></error>
                      <process id="%s" name="Resilience" isExecutable="true">
                        <startEvent id="start"></startEvent>
                        <serviceTask id="call" name="Call" flowable:async="true" flowable:class="%s">
                          <extensionElements>
                            <flowable:failedJobRetryTimeCycle>%s</flowable:failedJobRetryTimeCycle>
                          </extensionElements>
                        </serviceTask>
                        <boundaryEvent id="callError" attachedToRef="call" cancelActivity="true">
                          <errorEventDefinition errorRef="intentStepError"></errorEventDefinition>
                        </boundaryEvent>
                        <serviceTask id="recordFailure" name="Record Failure" flowable:class="%s"></serviceTask>
                        <endEvent id="end"></endEvent>
                        <endEvent id="failedEnd"></endEvent>
                        <sequenceFlow id="flow_start_call" sourceRef="start" targetRef="call"></sequenceFlow>
                        <sequenceFlow id="flow_call_end" sourceRef="call" targetRef="end"></sequenceFlow>
                        <sequenceFlow id="flow_callError_then" sourceRef="callError" targetRef="recordFailure"></sequenceFlow>
                        <sequenceFlow id="flow_recordFailure_end" sourceRef="recordFailure" targetRef="failedEnd"></sequenceFlow>
                      </process>
                    </definitions>
                    """;

    private static ProcessEngine engine;

    private ListAppender<ILoggingEvent> appender;

    /** Always fails; carries the attempt number so the recorded message pins WHICH attempt routed. */
    public static class DoomedDelegate implements JavaDelegate {

        static final AtomicInteger ATTEMPTS = new AtomicInteger();

        @Override
        public void execute(DelegateExecution execution) {
            throw new IllegalStateException("refused (attempt " + ATTEMPTS.incrementAndGet() + ")");
        }
    }

    /** Fails twice, succeeds on the third attempt - within its declared R3 cycle. */
    public static class FlakyDelegate implements JavaDelegate {

        static final AtomicInteger ATTEMPTS = new AtomicInteger();

        @Override
        public void execute(DelegateExecution execution) {
            int attempt = ATTEMPTS.incrementAndGet();
            if (attempt < 3) {
                throw new IllegalStateException("flaky (attempt " + attempt + ")");
            }
            execution.setVariable("result", "OK-" + attempt);
        }
    }

    /**
     * Always fails with an {@link Error} - what a delegate whose library left the platform raises
     * (#7840). Carries the attempt number like {@link DoomedDelegate}.
     */
    public static class UnlinkableDelegate implements JavaDelegate {

        static final AtomicInteger ATTEMPTS = new AtomicInteger();

        @Override
        public void execute(DelegateExecution execution) {
            throw new NoClassDefFoundError("unlinkable (attempt " + ATTEMPTS.incrementAndGet() + ")");
        }
    }

    /** Always fails with a message-less {@link Error}, so the route can only record its class name. */
    public static class UninitializableDelegate implements JavaDelegate {

        @Override
        public void execute(DelegateExecution execution) {
            throw new ExceptionInInitializerError();
        }
    }

    /** The error route: records what {@code {error}} reads, like the generated setField delegate. */
    public static class RecordFailureDelegate implements JavaDelegate {

        @Override
        public void execute(DelegateExecution execution) {
            execution.setVariable("recorded", String.valueOf(execution.getVariable(IntentStepResilience.ERROR_MESSAGE_VARIABLE)));
        }
    }

    @BeforeAll
    static void startEngine() {
        StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
        configuration.setJdbcUrl("jdbc:h2:mem:resilience-test;DB_CLOSE_DELAY=1000");
        configuration.setActivityBehaviorFactory(new DefaultActivityBehaviorFactory(new ResilientClassDelegateFactory()));
        configuration.setAsyncExecutorActivate(true);
        // Tight acquire cycles so the PT1S retry waits dominate the test's wall clock, via the
        // executor configuration object (the flat setAsyncExecutorDefault* setters are deprecated).
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
        resilienceLogger().addAppender(appender);
    }

    @AfterEach
    void releaseLog() {
        resilienceLogger().detachAppender(appender);
    }

    @Test
    void anExhaustedRetryCycleRoutesTheFinalAttemptsMessageThroughTheErrorBoundary() {
        deploy("doomed", DoomedDelegate.class.getName(), "R2/PT1S");
        ProcessInstance instance = engine.getRuntimeService()
                                         .startProcessInstanceByKey("doomed");

        // R2 = two total attempts; the SECOND one converts instead of dead-lettering, so the flow
        // ends through the error route with that attempt's message recorded.
        assertEquals("refused (attempt 2)", historicVariable(instance, "recorded"),
                "the error route must record the FINAL attempt's failure message");
        assertEquals(0, engine.getManagementService()
                              .createDeadLetterJobQuery()
                              .count(),
                "the converted failure must never dead-letter");

        // The routed final attempt is the one failure Flowable does not log (it is no job failure),
        // so the conversion logs it - once, at ERROR, with the stack, naming the route the boundary's
        // flow leads to and the very message the route recorded (#8082).
        List<ILoggingEvent> lines = finalFailureLines(instance);
        assertEquals(1, lines.size(), "exactly one line for the routed final attempt, got: " + lines);
        ILoggingEvent line = lines.get(0);
        assertEquals(Level.ERROR, line.getLevel());
        assertEquals(
                "Process [doomed] step [call] (instance " + instance.getId()
                        + ") failed its final attempt - routing to its onError step [recordFailure]: refused (attempt 2)",
                line.getFormattedMessage());
        assertEquals(IllegalStateException.class.getName(), line.getThrowableProxy()
                                                                .getClassName(),
                "the line must carry the failure's stack");
    }

    /**
     * An {@link Error} is a failure like any other to the step (#7840): before, it bypassed the
     * conversion, the job dead-lettered and the instance stayed parked on the step.
     */
    @Test
    void anErrorOnTheFinalAttemptRoutesThroughTheErrorBoundary() {
        deploy("unlinkable", UnlinkableDelegate.class.getName(), "R2/PT1S");
        ProcessInstance instance = engine.getRuntimeService()
                                         .startProcessInstanceByKey("unlinkable");

        assertEquals("unlinkable (attempt 2)", historicVariable(instance, "recorded"),
                "the declared cycle must still run, and its final Error must take the error route");
        assertEquals(0, engine.getManagementService()
                              .createDeadLetterJobQuery()
                              .count(),
                "the converted Error must never dead-letter");
    }

    @Test
    void anErrorWithoutAMessagePublishesItsClassName() {
        deploy("uninitializable", UninitializableDelegate.class.getName(), "R1/PT1S");
        ProcessInstance instance = engine.getRuntimeService()
                                         .startProcessInstanceByKey("uninitializable");

        assertEquals(ExceptionInInitializerError.class.getName(), historicVariable(instance, "recorded"));
    }

    @Test
    void aDelegateThatRecoversWithinItsCycleCompletesWithNoConversion() {
        deploy("flaky", FlakyDelegate.class.getName(), "R3/PT1S");
        ProcessInstance instance = engine.getRuntimeService()
                                         .startProcessInstanceByKey("flaky");

        assertEquals("OK-3", historicVariable(instance, "result"),
                "the declared cycle must re-run the delegate until it succeeds on its last attempt");
        assertEquals(List.of(), finalFailureLines(instance), "a step that recovers on a retried attempt routes nothing to log");
    }

    private static Logger resilienceLogger() {
        return (Logger) LoggerFactory.getLogger(IntentStepResilience.class);
    }

    /** The captured final-failure lines of one instance (the engine is shared across the tests). */
    private List<ILoggingEvent> finalFailureLines(ProcessInstance instance) {
        return appender.list.stream()
                            .filter(event -> event.getFormattedMessage()
                                                  .contains("(instance " + instance.getId() + ") failed its final attempt"))
                            .toList();
    }

    private static void deploy(String processId, String delegateClass, String retryCycle) {
        String xml = PROCESS_XML_TEMPLATE.formatted(processId, delegateClass, retryCycle, RecordFailureDelegate.class.getName());
        engine.getRepositoryService()
              .createDeployment()
              .addString(processId + ".bpmn20.xml", xml)
              .deploy();
    }

    /** Wait for the instance to end and return the historic value of the named variable. */
    private static String historicVariable(ProcessInstance instance, String name) {
        HistoryService history = engine.getHistoryService();
        await().atMost(Duration.ofSeconds(60))
               .pollInterval(Duration.ofMillis(250))
               .until(() -> history.createHistoricProcessInstanceQuery()
                                   .processInstanceId(instance.getId())
                                   .finished()
                                   .count() == 1);
        HistoricVariableInstance variable = history.createHistoricVariableInstanceQuery()
                                                   .processInstanceId(instance.getId())
                                                   .variableName(name)
                                                   .singleResult();
        return variable == null ? null : String.valueOf(variable.getValue());
    }
}
