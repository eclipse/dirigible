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

import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.ProcessIntent;
import org.eclipse.dirigible.components.intent.model.StepIntent;
import org.junit.jupiter.api.Test;

/**
 * A process setter can write a member of the record its trigger entity points at - dirigible #7934.
 *
 * <p>
 * A setter wrote the trigger entity only, so "an approval request moves its order to APPROVED" was
 * a hand-written {@code delegate:} whose body was one {@code updateProperty} on another repository.
 * {@code <relation>.<member>} is that one hop: the first segment is a to-one relation of the
 * trigger entity, the second a member of its target, and every rule of the plain setter applies to
 * the member - its type, the whole-value tokens, a status name resolved against the TARGET's
 * nomenclature, the target's lifecycle.
 */
class OneHopSetterIntentTest {

    private static final String YAML =
            """
                    name: approvals
                    seeds:
                      - name: order-statuses
                        entity: OrderStatus
                        rows:
                          - { id: 1, name: DRAFT }
                          - { id: 2, name: APPROVED }
                          - { id: 3, name: SHIPPED }
                      - name: request-statuses
                        entity: RequestStatus
                        rows:
                          - { id: 7, name: OPEN }
                          - { id: 8, name: DONE }
                    entities:
                      - name: OrderStatus
                        kind: setting
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string, length: 50 }
                      - name: RequestStatus
                        kind: setting
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string, length: 50 }
                      - name: Customer
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string }
                      - name: Order
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: note, type: string }
                          - { name: quantity, type: integer }
                          - { name: reviewedAt, type: timestamp }
                          - { name: approvedAt, type: timestamp, stampOn: { status: APPROVED } }
                        relations:
                          - { name: Status, kind: manyToOne, to: OrderStatus, function: EntityStatus, init: DRAFT }
                          - { name: customer, kind: manyToOne, to: Customer }
                        lifecycle:
                          edges:
                            - { from: DRAFT, to: [APPROVED] }
                            - { from: APPROVED, to: [SHIPPED] }
                      - name: ApprovalRequest
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: comment, type: string }
                        relations:
                          - { name: order, kind: manyToOne, to: Order, required: true }
                          - { name: Status, kind: manyToOne, to: RequestStatus, function: EntityStatus, init: OPEN }
                          - { name: notes, kind: oneToMany, to: RequestNote }
                      - name: RequestNote
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: text, type: string }
                        relations:
                          - { name: request, kind: manyToOne, to: ApprovalRequest, composition: true }
                    processes:
                      - name: Approve
                        trigger: { onCreate: ApprovalRequest }
                        steps:
                          - { name: markOrderApproved, kind: serviceTask, args: { setRelationField: order.Status, value: APPROVED, next: noteOrder } }
                          - { name: noteOrder, kind: serviceTask, args: { setField: order.note, value: approved by request, next: stampOrder } }
                          - { name: stampOrder, kind: serviceTask, args: { setField: order.reviewedAt, value: "{now}", next: markDone } }
                          - { name: markDone, kind: serviceTask, args: { setRelationField: Status, value: DONE, next: done } }
                          - { name: done, kind: end }
                    """;

    @Test
    void acceptsASetterOneHopThroughAToOneRelation() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML));
    }

    @Test
    void acceptsAClearFieldOneHopThroughAToOneRelation() {
        assertDoesNotThrow(
                () -> IntentParser.parse(YAML.replace("setField: order.note, value: approved by request", "clearField: order.note")));
    }

    /**
     * The name is the TARGET's: APPROVED is a seed of OrderStatus, not of the request's own
     * RequestStatus.
     */
    @Test
    void resolvesTheSeedNameAgainstTheTargetsNomenclature() {
        IntentModel model = IntentParser.parse(YAML);

        assertEquals("2", String.valueOf(step(model, "markOrderApproved").getArgs()
                                                                         .get("value")));
        assertEquals("8", String.valueOf(step(model, "markDone").getArgs()
                                                                .get("value")));
    }

    @Test
    void refusesAnUnknownTargetSeed() {
        assertIssue(YAML.replace("setRelationField: order.Status, value: APPROVED", "setRelationField: order.Status, value: CLOSED"),
                "names [CLOSED], which is not a seeded status of [OrderStatus]");
    }

    @Test
    void refusesTwoHops() {
        assertIssue(YAML.replace("setField: order.note", "setField: order.customer.name"),
                "process [Approve] step [noteOrder] setField [order.customer.name] is more than one hop");
    }

    @Test
    void refusesAOneToManyAsTheFirstSegment() {
        assertIssue(YAML.replace("setField: order.note", "setField: notes.text"),
                "process [Approve] step [noteOrder] setField [notes.text] goes through [notes], a oneToMany relation of [ApprovalRequest]");
    }

    @Test
    void refusesAFieldAsTheFirstSegment() {
        assertIssue(YAML.replace("setField: order.note", "setField: comment.note"),
                "process [Approve] step [noteOrder] setField [comment.note] goes through [comment], a field of [ApprovalRequest]");
    }

    @Test
    void refusesAnUnknownFirstSegment() {
        assertIssue(YAML.replace("setField: order.note", "setField: ordr.note"),
                "process [Approve] step [noteOrder] setField [ordr.note] goes through [ordr], which is not a relation of [ApprovalRequest]");
    }

    @Test
    void refusesACrossModelTarget() {
        String yaml = YAML.replace("name: approvals\n", "name: approvals\nuses:\n  - { model: sales }\n")
                          .replace("{ name: order, kind: manyToOne, to: Order, required: true }",
                                  "{ name: order, kind: manyToOne, to: Order, model: sales, required: true }");
        assertIssue(yaml, "process [Approve] step [noteOrder] setField [order.note] goes through [order] to [sales:Order], an entity of"
                + " another model");
    }

    @Test
    void refusesAnUnknownMemberOfTheTarget() {
        assertIssue(YAML.replace("setField: order.note", "setField: order.nte"),
                "process [Approve] step [noteOrder] setField [order.nte] is not a field of [Order]");
    }

    /**
     * The plain setter's own message: the member is held to the same rules as a field of the trigger.
     */
    @Test
    void refusesAMemberTypeTheSetterCannotWrite() {
        assertIssue(YAML.replace("setField: order.note", "setField: order.quantity"),
                "process [Approve] step [noteOrder] setField [order.quantity] must be a string/text field");
    }

    @Test
    void refusesARelationSetterOnAMemberThatIsNotAToOne() {
        assertIssue(YAML.replace("setRelationField: order.Status, value: APPROVED", "setRelationField: order.note, value: 2"),
                "process [Approve] step [markOrderApproved] setRelationField [order.note] is not a manyToOne/oneToOne relation of [Order]");
    }

    /** The target's lifecycle judges the write, exactly as it judges the target's own setter. */
    @Test
    void refusesATargetStatusNoEdgeOfTheTargetsLifecycleReaches() {
        assertIssue(YAML.replace("setRelationField: order.Status, value: APPROVED", "setRelationField: order.Status, value: DRAFT"),
                "process [Approve] step [markOrderApproved] sets the status to [DRAFT], which no edge of the [Order] lifecycle reaches");
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
