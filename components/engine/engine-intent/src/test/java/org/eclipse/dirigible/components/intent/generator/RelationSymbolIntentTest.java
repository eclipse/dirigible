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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.components.intent.parser.IntentValidationException;
import org.junit.jupiter.api.Test;

/**
 * A seeded name compared against a to-one relation that is NOT the status (issue #8015) resolves in
 * a process {@code trigger.when} and a schedule {@code where} exactly as it does in a decision
 * {@code if}: against the nomenclature that relation points at. Left as written, the name was
 * generated as a string compared with the integer foreign key, so a trigger never started its
 * process and a schedule never matched a row - the #6862 failure, on a non-status relation.
 */
class RelationSymbolIntentTest {

    private static final String YAML = """
            name: requests
            entities:
              - name: RequestType
                function: Setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: RequestStatus
                function: Setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: Request
                fields:
                  - { name: id,    type: integer, primaryKey: true, generated: true }
                  - { name: note,  type: string, length: 200 }
                  - { name: email, type: string }
                relations:
                  - { name: type,   kind: manyToOne, to: RequestType, required: true }
                  - { name: status, kind: manyToOne, to: RequestStatus, function: EntityStatus, init: NEW }
            seeds:
              - name: request-types
                entity: RequestType
                rows:
                  - { id: 1, name: OPEN }
                  - { id: 2, name: CLOSE }
              - name: request-statuses
                entity: RequestStatus
                rows:
                  - { id: 1, name: NEW }
                  - { id: 2, name: DONE }
            processes:
              - name: OpenFlow
                trigger: { onCreate: Request, when: "type == OPEN" }
                steps:
                  - { name: done, kind: end }
              - name: CloseFlow
                trigger: { onCreate: Request, when: "type == CLOSE" }
                steps:
                  - { name: done, kind: end }
            schedules:
              - name: closeReminder
                cron: "0 0 8 * * ?"
                entity: Request
                where:
                  - { field: type, op: eq, value: CLOSE }
                notify:
                  to: email
                  subject: "Request {id} awaits closing"
                  body: "Please close the request."
            """;

    @Test
    void aTriggerGuardResolvesANameOnAToOneRelation() {
        IntentModel model = IntentParser.parse(YAML);

        assertEquals("type == 1", TriggerSupport.triggerWhen(model.getProcesses()
                                                                  .get(0)));
        assertEquals("type == 2", TriggerSupport.triggerWhen(model.getProcesses()
                                                                  .get(1)));
        List<Map<String, Object>> triggers = GlueIntentGenerator.buildTriggersForTest(model);
        assertEquals("java.util.Objects.equals(entity.Type, 1)", GlueRendering.eventGuard(triggers.get(0)));
        assertEquals("java.util.Objects.equals(entity.Type, 2)", GlueRendering.eventGuard(triggers.get(1)));
    }

    @Test
    void aListTriggerGuardResolvesEachElement() {
        IntentModel model = IntentParser.parse(YAML.replace("when: \"type == CLOSE\"", "when: [\"type == CLOSE\", \"note != null\"]"));

        assertEquals(List.of("type == 2", "note != null"), TriggerSupport.triggerWhen(model.getProcesses()
                                                                                           .get(1)));
    }

    /** The status pass and the relation pass share one guard, each on its own nomenclature. */
    @Test
    void theStatusAndAnotherRelationResolveInOneGuard() {
        IntentModel model = IntentParser.parse(YAML.replace("when: \"type == CLOSE\"", "when: [\"status == DONE\", \"type != OPEN\"]"));

        assertEquals(List.of("status == 2", "type != 1"), TriggerSupport.triggerWhen(model.getProcesses()
                                                                                          .get(1)));
    }

    @Test
    void aScheduleWhereResolvesANameOnAToOneRelation() {
        Map<String, Object> schedule = GlueIntentGenerator.buildSchedulesForTest(IntentParser.parse(YAML))
                                                          .get(0);

        assertEquals("Criteria.create().eq(\"Type\", 2)", GlueRendering.criteria(schedule));
    }

    @Test
    void anUnknownNameOnARelationIsRejectedInATriggerGuard() {
        assertIssue(YAML.replace("type == CLOSE", "type == CLOSED"), "process [CloseFlow] trigger when names [CLOSED]",
                "not a seeded status of [RequestType]");
    }

    @Test
    void anUnknownNameOnARelationIsRejectedInAScheduleWhere() {
        assertIssue(YAML.replace("value: CLOSE", "value: CLOSED"), "schedule [closeReminder] where [type] names [CLOSED]",
                "not a seeded status of [RequestType]");
    }

    @Test
    void anOrderingComparisonOnARelationIsRejectedInATriggerGuard() {
        assertIssue(YAML.replace("type == CLOSE", "type >= CLOSE"), "a seeded name has no ordering");
    }

    @Test
    void anOrderingComparisonOnARelationIsRejectedInAScheduleWhere() {
        assertIssue(YAML.replace("op: eq, value: CLOSE", "op: gt, value: CLOSE"), "a seeded name has no ordering");
    }

    /** The relation pass leaves the status to the status pass, so a misspelt status is one issue. */
    @Test
    void aMisspeltStatusInATriggerGuardIsReportedOnce() {
        IntentValidationException thrown = assertThrows(IntentValidationException.class,
                () -> IntentParser.parse(YAML.replace("when: \"type == CLOSE\"", "when: \"status == DONNE\"")));

        assertEquals(1, thrown.getIssues()
                              .stream()
                              .filter(issue -> issue.contains("DONNE"))
                              .count(),
                "the misspelt status must be reported exactly once: " + thrown.getIssues());
    }

    /**
     * A field compared with an identifier-looking literal is a string comparison, not a nomenclature.
     */
    @Test
    void aComparisonOnAFieldIsLeftAlone() {
        IntentModel model = IntentParser.parse(YAML.replace("when: \"type == CLOSE\"", "when: [\"type == CLOSE\", \"note == urgent\"]")
                                                   .replace("- { field: type, op: eq, value: CLOSE }",
                                                           "- { field: type, op: eq, value: CLOSE }\n      - { field: note, op: eq, value: urgent }"));

        assertEquals(List.of("type == 2", "note == urgent"), TriggerSupport.triggerWhen(model.getProcesses()
                                                                                             .get(1)));
        assertEquals("Criteria.create().eq(\"Type\", 2).eq(\"Note\", \"urgent\")",
                GlueRendering.criteria(GlueIntentGenerator.buildSchedulesForTest(model)
                                                          .get(0)));
    }

    @Test
    void numericIdsOnARelationAreLeftAlone() {
        IntentModel model = IntentParser.parse(YAML.replace("type == CLOSE", "type == 2")
                                                   .replace("value: CLOSE", "value: 2"));

        assertEquals("type == 2", TriggerSupport.triggerWhen(model.getProcesses()
                                                                  .get(1)));
        assertEquals("Criteria.create().eq(\"Type\", 2)", GlueRendering.criteria(GlueIntentGenerator.buildSchedulesForTest(model)
                                                                                                    .get(0)));
    }

    private static void assertIssue(String yaml, String... expected) {
        IntentValidationException thrown = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        assertTrue(thrown.getIssues()
                         .stream()
                         .anyMatch(issue -> List.of(expected)
                                                .stream()
                                                .allMatch(issue::contains)),
                "expected an issue containing " + List.of(expected) + " but got " + thrown.getIssues());
    }
}
