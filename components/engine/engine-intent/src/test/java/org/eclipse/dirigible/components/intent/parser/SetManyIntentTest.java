/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.ProcessIntent;
import org.eclipse.dirigible.components.intent.model.StepIntent;
import org.junit.jupiter.api.Test;

/**
 * One setter step writes several members of one record in one statement - dirigible #8042.
 *
 * <p>
 * A setter wrote one member per step, and every step is its own job and transaction, so a change
 * that is one fact - a job releasing its slot as it completes - passed through a half-written state
 * another job could observe. {@code set:} names every member of the change; each key is held to the
 * rules of its single-member setter, and all of them must address the same record.
 */
class SetManyIntentTest {

    private static final String YAML =
            """
                    name: jobs
                    seeds:
                      - name: job-statuses
                        entity: JobStatus
                        rows:
                          - { id: 1, name: QUEUED }
                          - { id: 2, name: RUNNING }
                          - { id: 3, name: DONE }
                          - { id: 4, name: FAILED }
                    entities:
                      - name: JobStatus
                        kind: setting
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string, length: 50 }
                      - name: Machine
                        fields:
                          - { name: id,          type: integer, primaryKey: true, generated: true }
                          - { name: lastReading, type: string, length: 100 }
                          - { name: operator,    type: string, length: 320 }
                          - { name: count,       type: integer }
                      - name: Job
                        fields:
                          - { name: id,           type: integer, primaryKey: true, generated: true }
                          - { name: errorMessage, type: string, length: 2000 }
                          - { name: slotKey,      type: string, length: 64, unique: true }
                          - { name: finishedAt,   type: timestamp }
                        relations:
                          - { name: Status, kind: manyToOne, to: JobStatus, function: EntityStatus, init: QUEUED }
                          - { name: machine, kind: manyToOne, to: Machine }
                        lifecycle:
                          edges:
                            - { from: QUEUED, to: [RUNNING] }
                            - { from: RUNNING, to: [DONE, FAILED] }
                    processes:
                      - name: RunJob
                        trigger: { onCreate: Job }
                        steps:
                          - { name: start,       kind: serviceTask, args: { set: { errorMessage: null, Status: RUNNING }, next: work } }
                          - { name: work,        kind: serviceTask, args: { delegate: custom.JobRunner, onError: fail, next: finish } }
                          - { name: finish,      kind: serviceTask, args: { set: { slotKey: null, Status: DONE, finishedAt: "{now}" }, next: freeMachine } }
                          - { name: freeMachine, kind: serviceTask, args: { set: { machine.lastReading: null, machine.operator: null }, next: end } }
                          - { name: fail,        kind: serviceTask, args: { set: { errorMessage: "{error}", Status: FAILED }, next: end } }
                    """;

    @Test
    void aSetMapParses() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML));
    }

    @Test
    void aSeededNameInASetMapResolvesToItsId() {
        Map<?, ?> set = (Map<?, ?>) step(IntentParser.parse(YAML), "finish").getArgs()
                                                                            .get("set");

        assertEquals("3", String.valueOf(set.get("Status")), "the status name resolves to its seed id: " + set);
        assertEquals("{now}", set.get("finishedAt"), "a field value is left as authored: " + set);
    }

    @Test
    void aSetMapAcrossTwoRecordsIsRejected() {
        assertIssue(YAML.replace("{ machine.lastReading: null, machine.operator: null }", "{ errorMessage: null, machine.operator: null }"),
                "process [RunJob] step [freeMachine] set [machine.operator] writes another record than [errorMessage]");
    }

    @Test
    void aSetMapCombinedWithSetFieldIsRejected() {
        assertIssue(
                YAML.replace("args: { set: { errorMessage: null, Status: RUNNING }, next: work }",
                        "args: { set: { errorMessage: null, Status: RUNNING }, setField: slotKey, value: A, next: work }"),
                "process [RunJob] step [start] set cannot be combined with setField");
    }

    @Test
    void anEmptySetMapIsRejected() {
        assertIssue(YAML.replace("{ errorMessage: null, Status: RUNNING }", "{}"), "process [RunJob] step [start] set names no member");
    }

    @Test
    void nowOnAStringKeyIsRejectedWithTheSetFieldMessage() {
        assertIssue(YAML.replace("finishedAt: \"{now}\"", "slotKey: \"{now}\""),
                "process [RunJob] step [finish] set [slotKey] value {now} needs a timestamp or date field");
    }

    @Test
    void nowInsideALargerValueIsRejected() {
        assertIssue(YAML.replace("finishedAt: \"{now}\"", "errorMessage: \"at {now}\""),
                "process [RunJob] step [finish] set [errorMessage] value [at {now}] may use {now} only as the whole value");
    }

    @Test
    void nullOnARelationIsRejected() {
        assertIssue(YAML.replace("Status: RUNNING", "Status: null"),
                "process [RunJob] step [start] set [Status] is a relation of [Job] and cannot be cleared");
    }

    @Test
    void anUnknownSeededNameIsRejected() {
        assertIssue(YAML.replace("Status: RUNNING", "Status: PAUSED"), "names [PAUSED], which is not a seeded status of [JobStatus]");
    }

    @Test
    void aLiteralOnANumericFieldIsRejected() {
        assertIssue(YAML.replace("machine.operator: null", "machine.count: 3"),
                "process [RunJob] step [freeMachine] set [machine.count] must be a string/text field");
    }

    @Test
    void anUnknownMemberIsRejected() {
        assertIssue(YAML.replace("slotKey: null", "slotKy: null"),
                "process [RunJob] step [finish] set [slotKy] is not a field or a manyToOne/oneToOne relation of [Job]");
    }

    @Test
    void anErrorValueOffAnErrorRouteIsRejected() {
        assertIssue(YAML.replace("{ errorMessage: null, Status: RUNNING }", "{ errorMessage: \"{error}\", Status: RUNNING }"),
                "process [RunJob] step [start] set [errorMessage] value [{error}] is only resolvable on a step reachable from an onError route");
    }

    /** The record's lifecycle judges the status a set: map writes, exactly as a setRelationField's. */
    @Test
    void aStatusNoEdgeReachesIsRejected() {
        assertIssue(YAML.replace("Status: RUNNING", "Status: QUEUED"),
                "process [RunJob] step [start] sets the status to [QUEUED], which no edge of the [Job] lifecycle reaches");
    }

    /** A user task writes relations only - as its setRelationField does. */
    @Test
    void aFieldKeyOnAUserTaskIsRejected() {
        assertIssue(YAML.replace(
                "{ name: start,       kind: serviceTask, args: { set: { errorMessage: null, Status: RUNNING }, next: work } }",
                "{ name: start, kind: userTask, args: { assignee: ADMINISTRATOR, set: { errorMessage: null, Status: RUNNING }, next: work } }"),
                "process [RunJob] step [start] set [errorMessage] is a field of [Job] - a userTask writes only to-one relations");
    }

    private static StepIntent step(IntentModel model, String name) {
        for (ProcessIntent process : model.getProcesses()) {
            for (StepIntent step : process.getSteps()) {
                if (name.equals(step.getName())) {
                    return step;
                }
            }
        }
        throw new AssertionError("no step [" + name + "]");
    }

    private static void assertIssue(String yaml, String expected) {
        IntentValidationException ex = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        boolean found = ex.getIssues()
                          .stream()
                          .anyMatch(issue -> issue.contains(expected));
        assertEquals(true, found, "expected an issue containing [" + expected + "] but got " + ex.getIssues());
    }
}
