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

import java.util.List;

import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ErrorEventDefinition;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.job.api.Job;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the FINAL failed attempt of a {@code flowable:class} service task into the caught
 * {@link #ERROR_CODE} BPMN error - the runtime half of the intent DSL's declarative step resilience
 * ({@code retry:} / {@code onError:}, dirigible #6762).
 *
 * <p>
 * A BPMN error boundary event fires only on a {@link BpmnError}; a delegate's plain
 * {@code RuntimeException} or {@code Error} fails the async job instead (rollback, retry cycle,
 * dead-letter incident), so an {@code onError:} route would never be taken. This helper closes that
 * gap at the one moment the outcome is decided: when an attempt fails AND the task carries an
 * intent error boundary AND no further retry will run, the failure message is published as the
 * {@link #ERROR_MESSAGE_VARIABLE} process variable (a {@code BpmnError} commits, so the variable
 * persists for the error route's {@code {error}} reads) and the error is propagated to the
 * boundary. Every other failure is left to the engine's own machinery - a task without the boundary
 * behaves exactly as before.
 *
 * <p>
 * "Final" mirrors Flowable's {@code JobRetryCmd} arithmetic exactly: with no
 * {@code failedJobRetryTimeCycle} the first failure is final (the intent declared no retry); with a
 * cycle {@code R<n>/<every>}, the first failure (no exception message on the job yet) is final only
 * when {@code n <= 1}, and a later attempt is final when the job's remaining retries are down to
 * one. The error code and the variable name are string contracts with the intent BPMN generator
 * ({@code ProcessResilienceSupport} in {@code engine-intent}), like the {@code ${JavaTask}}
 * delegate-expression literal.
 */
final class IntentStepResilience {

    /** The BPMN error code the generated {@code onError} boundary events catch. */
    static final String ERROR_CODE = "INTENT_STEP_FAILED";

    /** The process variable carrying the failure message for the error route's {@code {error}}. */
    static final String ERROR_MESSAGE_VARIABLE = "__errorMessage";

    private static final Logger LOGGER = LoggerFactory.getLogger(IntentStepResilience.class);

    private IntentStepResilience() {}

    /**
     * Convert the failure to the caught intent BPMN error when this is the final attempt of a task
     * carrying an intent error boundary.
     *
     * <p>
     * Whatever the delegate threw counts, an {@link Error} included (#7840): a
     * {@code NoClassDefFoundError} from a library that left the platform is exactly when an operator
     * needs the declared route, and it used to dead-letter the job instead. Only a
     * {@link VirtualMachineError} is left to the engine - the JVM itself is failing, and running an
     * error route on top of it would hide that.
     *
     * @param execution the failing execution
     * @param failure the delegate's failure
     * @return {@code true} when the failure was converted and routed (the caller must not rethrow);
     *         {@code false} when the engine's own failure handling should proceed
     */
    static boolean convertFinalFailure(DelegateExecution execution, Throwable failure) {
        if (failure instanceof VirtualMachineError) {
            return false;
        }
        FlowElement element = execution.getCurrentFlowElement();
        if (!(element instanceof ServiceTask serviceTask)) {
            return false;
        }
        Process process = ProcessDefinitionUtil.getProcess(execution.getProcessDefinitionId());
        BoundaryEvent boundary = intentErrorBoundary(execution, process, serviceTask);
        if (boundary == null) {
            return false;
        }
        Job job = currentJob(execution, serviceTask);
        if (!isFinalAttempt(serviceTask.getFailedJobRetryTimeCycleValue(), job == null ? null : job.getExceptionMessage(),
                job == null ? null : job.getRetries())) {
            return false;
        }
        String message = failure.getMessage() == null || failure.getMessage()
                                                                .isBlank() ? failure.getClass()
                                                                                    .getName()
                                                                        : failure.getMessage();
        LOGGER.error(
                finalFailureLine(process.getId(), serviceTask.getId(), execution.getProcessInstanceId(), routeTarget(boundary), message),
                failure);
        execution.setVariable(ERROR_MESSAGE_VARIABLE, message);
        ErrorPropagation.propagateError(new BpmnError(ERROR_CODE, message), execution);
        return true;
    }

    /**
     * The one ERROR line of a final failed attempt routed to its {@code onError} step (#8082): the
     * retried attempts before it are Flowable's own logged job failures, while the routed one is not a
     * job failure at all, so without this line the failure that ends the step is the one the log never
     * shows.
     *
     * @param processKey the process definition key
     * @param stepId the failed task's element id (the intent step name)
     * @param processInstanceId the failed instance
     * @param route the element the error boundary routes to, or null when it cannot be resolved (the
     *        line then omits it)
     * @param message the failure message - the same text the error route reads as {@code {error}}
     * @return the log line
     */
    static String finalFailureLine(String processKey, String stepId, String processInstanceId, String route, String message) {
        String target = route == null ? "its onError boundary" : "its onError step [" + route + "]";
        return "Process [" + processKey + "] step [" + stepId + "] (instance " + processInstanceId
                + ") failed its final attempt - routing to " + target + ": " + message;
    }

    /**
     * The task's error boundary event catching the intent error code - the generated marker that opts a
     * task into the conversion - or null when it carries none. A hand-authored process without it (or
     * with a boundary for a different code) keeps today's behaviour untouched.
     */
    private static BoundaryEvent intentErrorBoundary(DelegateExecution execution, Process process, ServiceTask serviceTask) {
        BpmnModel model = ProcessDefinitionUtil.getBpmnModel(execution.getProcessDefinitionId());
        if (process == null || model == null) {
            return null;
        }
        for (BoundaryEvent boundary : process.findFlowElementsOfType(BoundaryEvent.class, true)) {
            if (!serviceTask.getId()
                            .equals(boundary.getAttachedToRefId())) {
                continue;
            }
            for (EventDefinition definition : boundary.getEventDefinitions()) {
                if (definition instanceof ErrorEventDefinition errorDefinition
                        && ERROR_CODE.equals(resolveErrorCode(model, errorDefinition.getErrorCode()))) {
                    return boundary;
                }
            }
        }
        return null;
    }

    /**
     * The element the boundary's outgoing flow leads to - the onError step - or null when unresolved.
     */
    private static String routeTarget(BoundaryEvent boundary) {
        List<SequenceFlow> outgoing = boundary.getOutgoingFlows();
        return outgoing == null || outgoing.isEmpty() ? null
                : outgoing.get(0)
                          .getTargetRef();
    }

    /**
     * The error code an event definition's reference resolves to: a reference to a declared
     * {@code <error>} maps through the model's error registry, a bare code stands for itself (the same
     * resolution {@code ErrorPropagation} applies when matching a thrown error).
     */
    private static String resolveErrorCode(BpmnModel model, String errorReference) {
        if (errorReference == null) {
            return null;
        }
        String mapped = model.getErrors()
                             .get(errorReference);
        return mapped != null ? mapped : errorReference;
    }

    /**
     * The async job currently executing this task, or null when none is visible (a synchronous
     * execution). Looked up by PROCESS INSTANCE and matched on execution + element id - deliberately
     * not {@code findJobsByExecutionId}, which does not surface the very job being executed (verified
     * against a live engine in {@code ResilientClassDelegateEngineTest}), while the instance-scoped
     * query does.
     */
    private static Job currentJob(DelegateExecution execution, ServiceTask serviceTask) {
        List<JobEntity> jobs = CommandContextUtil.getJobService()
                                                 .findJobsByProcessInstanceId(execution.getProcessInstanceId());
        if (jobs == null || jobs.isEmpty()) {
            return null;
        }
        JobEntity elementMatch = null;
        for (JobEntity job : jobs) {
            boolean sameElement = serviceTask.getId()
                                             .equals(job.getElementId());
            if (sameElement && execution.getId()
                                        .equals(job.getExecutionId())) {
                return job;
            }
            if (sameElement && elementMatch == null) {
                elementMatch = job;
            }
        }
        return elementMatch;
    }

    /**
     * Whether no further retry will run after this failure - the exact mirror of {@code JobRetryCmd}'s
     * decision, so the conversion fires precisely where the engine would otherwise dead-letter.
     *
     * @param retryCycle the task's {@code failedJobRetryTimeCycle} ({@code R<n>/<every>}), or null when
     *        the step declares no retry
     * @param jobExceptionMessage the executing job's recorded exception message - null exactly on the
     *        first attempt (that is how {@code JobRetryCmd} detects it too)
     * @param jobRetries the executing job's remaining retries, or null when no job is visible
     * @return whether this failure is the final one
     */
    static boolean isFinalAttempt(String retryCycle, String jobExceptionMessage, Integer jobRetries) {
        if (retryCycle == null || retryCycle.isBlank()) {
            return true; // no declared retry: the non-retried failure routes immediately
        }
        if (jobRetries == null) {
            return true; // no job to re-run - nothing would ever retry this attempt
        }
        if (jobExceptionMessage == null) {
            // First failure of a cycle job: JobRetryCmd reads the attempt budget off the cycle, so the
            // job's current retries (still the engine default) say nothing yet.
            return cycleAttempts(retryCycle) <= 1;
        }
        return jobRetries <= 1;
    }

    /**
     * The total attempts of an {@code R<n>/<every>} cycle, or {@link Integer#MAX_VALUE} for a shape
     * this helper does not recognize - never claim finality on a cycle the engine may read differently.
     */
    private static int cycleAttempts(String retryCycle) {
        String trimmed = retryCycle.trim();
        int slash = trimmed.indexOf('/');
        if (!trimmed.startsWith("R") || slash < 2) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(trimmed.substring(1, slash));
        } catch (NumberFormatException unrecognizedCycleShape) {
            return Integer.MAX_VALUE;
        }
    }
}
