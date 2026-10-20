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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * The copy recipients and the process-variable recipient of a sending step (dirigible #7935): what
 * the {@code sends} descriptor carries, and that a block using neither carries exactly what it
 * always did.
 */
class GlueNotifyRecipientsTest {

    private static final String YAML =
            """
                    name: closing
                    entities:
                      - name: Person
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: email, type: string }
                      - name: ClosingRequest
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string }
                          - { name: requestedBy, type: string }
                        relations:
                          - { name: Owner, kind: manyToOne, to: Person }
                      - name: ClosingTask
                        fields:
                          - { name: id, type: integer, primaryKey: true, generated: true }
                          - { name: assigneeEmail, type: string }
                          - { name: watcherEmail, type: string }
                        relations:
                          - { name: Request, kind: manyToOne, to: ClosingRequest }
                    processes:
                      - name: Closing
                        trigger: { onCreate: ClosingRequest }
                        vars:
                          - { name: approverEmails }
                        steps:
                          - { name: collectApprovers, kind: serviceTask, args: { delegate: my.CollectApprovers, produces: [approverEmails], next: notify } }
                          - name: notify
                            kind: serviceTask
                            args:
                              notify:
                                to: "@var:approverEmails"
                                cc: requestedBy
                                bcc: "@config:ARCHIVE_EMAIL"
                                subject: "Closing {name} is complete"
                                body: "Closing {name} is complete."
                              uses: [approverEmails]
                              next: end
                          - { name: end, kind: end }
                    """;

    private static final String VAR = "(String) execution.getVariable(\"approverEmails\")";

    @Test
    void theDescriptorCarriesEveryRecipient() {
        Map<String, Object> send = send(YAML);
        assertEquals(VAR, send.get("notifyToExpression"));
        assertEquals("entity.RequestedBy", send.get("notifyCcExpression"));
        assertEquals("org.eclipse.dirigible.sdk.core.Configurations.get(\"ARCHIVE_EMAIL\")", send.get("notifyBccExpression"));
        assertEquals("true", send.get("notifyReadsVariables"), "an @var: recipient needs the execution in scope");
    }

    /** A relation.field copy recipient loads its relation exactly as a relation.field to does. */
    @Test
    void aRelationCopyRecipientRegistersItsLoad() {
        Map<String, Object> send = send(YAML.replace("cc: requestedBy", "cc: Owner.email"));
        assertEquals("(Owner == null ? null : Owner.Email)", send.get("notifyCcExpression"));
        List<?> loads = (List<?>) send.get("notifyRelationLoads");
        assertEquals(1, loads.size(), loads.toString());
        assertEquals("Owner", ((Map<?, ?>) loads.get(0)).get("local"));
    }

    @Test
    void aBccOnlyStepHasNoAddressee() {
        Map<String, Object> send = send(YAML.replace("            to: \"@var:approverEmails\"\n", "")
                                            .replace("            cc: requestedBy\n", ""));
        assertEquals("null", send.get("notifyToExpression"));
        assertFalse(send.containsKey("notifyCcExpression"));
        assertEquals("org.eclipse.dirigible.sdk.core.Configurations.get(\"ARCHIVE_EMAIL\")", send.get("notifyBccExpression"));
        assertFalse(send.containsKey("notifyReadsVariables"), "nothing reads the process instance");
    }

    /** Byte-identical generation for a block that uses neither copy key nor @var:. */
    @Test
    void aToOnlyBlockCarriesNothingNew() {
        Map<String, Object> send = send(YAML.replace("to: \"@var:approverEmails\"", "to: requestedBy")
                                            .replace("            cc: requestedBy\n", "")
                                            .replace("            bcc: \"@config:ARCHIVE_EMAIL\"\n", ""));
        assertEquals("entity.RequestedBy", send.get("notifyToExpression"));
        assertFalse(send.containsKey("notifyCcExpression"), send.toString());
        assertFalse(send.containsKey("notifyBccExpression"), send.toString());
        assertFalse(send.containsKey("notifyReadsVariables"), send.toString());
    }

    /** Inside a fan-out a copy recipient resolves per ROW, like the recipient. */
    @Test
    void aFanOutResolvesTheCopyRecipientPerRow() {
        Map<String, Object> send = send(YAML.replace("            to: \"@var:approverEmails\"\n            cc: requestedBy\n",
                "            forEach: ClosingTask\n            to: assigneeEmail\n            cc: watcherEmail\n"));
        assertEquals("ClosingTask", send.get("forEach"));
        assertEquals("entity.AssigneeEmail", send.get("notifyToExpression"));
        assertEquals("entity.WatcherEmail", send.get("notifyCcExpression"));
    }

    @Test
    void aFanOutReadingAVarIsToldToPassTheExecution() {
        Map<String, Object> send = send(YAML.replace("            to: \"@var:approverEmails\"\n            cc: requestedBy\n",
                "            forEach: ClosingTask\n            to: assigneeEmail\n            cc: \"@var:approverEmails\"\n"));
        assertEquals(VAR, send.get("notifyCcExpression"));
        assertEquals("true", send.get("notifyReadsVariables"));
    }

    /** An unresolvable copy recipient drops the block, as an unresolvable to does. */
    @Test
    void anUnresolvableCopyRecipientDropsTheSend() {
        assertTrue(GlueIntentGenerator.buildSendsForTest(IntentParser.parse(YAML.replace("cc: requestedBy", "cc: Owner.phone")))
                                      .isEmpty());
    }

    /** The other three notify blocks (#7938): a transition, a lifecycle notification, a schedule. */
    private static final String COPIES_EVERYWHERE = """
            name: closing
            entities:
              - name: ClosingStatus
                function: Setting
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: Person
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: email, type: string }
              - name: ClosingRequest
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
                  - { name: requestedBy, type: string }
                relations:
                  - { name: Owner, kind: manyToOne, to: Person }
                  - { name: Status, kind: manyToOne, to: ClosingStatus, function: EntityStatus, init: 1 }
            transitions:
              - name: Reopen
                forEntity: ClosingRequest
                from: [2]
                setStatus: 1
                notify:
                  to: requestedBy
                  cc: Owner.email
                  bcc: "@config:ARCHIVE_EMAIL"
                  subject: "Closing {name} was reopened"
                  body: "Reopened."
            notifications:
              - name: closingCreated
                event: { onCreate: ClosingRequest }
                to: requestedBy
                cc: Owner.email
                bcc: "@config:ARCHIVE_EMAIL"
                subject: "New closing {name}"
                body: "New closing."
            schedules:
              - name: nudge
                cron: "0 0 8 * * ?"
                entity: ClosingRequest
                notify:
                  to: requestedBy
                  cc: Owner.email
                  bcc: "@config:ARCHIVE_EMAIL"
                  subject: "Pending {name}"
                  body: "Pending."
            """;

    private static final String CC = "(Owner == null ? null : Owner.Email)";
    private static final String BCC = "org.eclipse.dirigible.sdk.core.Configurations.get(\"ARCHIVE_EMAIL\")";

    @Test
    void aTransitionCarriesItsCopies() {
        Map<String, Object> t = GlueIntentGenerator.buildTransitionsForTest(IntentParser.parse(COPIES_EVERYWHERE))
                                                   .get(0);
        assertEquals("entity.RequestedBy", t.get("notifyToExpression"));
        assertEquals(CC, t.get("notifyCcExpression"));
        assertEquals(BCC, t.get("notifyBccExpression"));
        assertEquals("Owner", ((Map<?, ?>) ((List<?>) t.get("notifyRelationLoads")).get(0)).get("local"),
                "a relation.field copy recipient registers its load");
    }

    @Test
    void aNotificationCarriesItsCopies() {
        Map<String, Object> n = GlueIntentGenerator.buildNotificationsForTest(IntentParser.parse(COPIES_EVERYWHERE))
                                                   .get(0);
        assertEquals("entity.RequestedBy", n.get("toExpression"));
        assertEquals(CC, n.get("ccExpression"));
        assertEquals(BCC, n.get("bccExpression"));
        assertEquals(1, ((List<?>) n.get("relationLoads")).size(), n.toString());
    }

    @Test
    void aScheduleCarriesItsCopies() {
        Map<String, Object> s = GlueIntentGenerator.buildSchedulesForTest(IntentParser.parse(COPIES_EVERYWHERE))
                                                   .get(0);
        assertEquals("entity.RequestedBy", s.get("toExpression"));
        assertEquals(CC, s.get("ccExpression"));
        assertEquals(BCC, s.get("bccExpression"));
    }

    /** Byte-identical generation for the other three blocks too, when they author neither key. */
    @Test
    void theOtherBlocksWithoutCopiesCarryNothingNew() {
        String plain = COPIES_EVERYWHERE.replaceAll("(?m)^\\s*(cc|bcc): .*\\R", "");
        Map<String, Object> t = GlueIntentGenerator.buildTransitionsForTest(IntentParser.parse(plain))
                                                   .get(0);
        assertFalse(t.containsKey("notifyCcExpression") || t.containsKey("notifyBccExpression"), t.toString());
        Map<String, Object> n = GlueIntentGenerator.buildNotificationsForTest(IntentParser.parse(plain))
                                                   .get(0);
        assertFalse(n.containsKey("ccExpression") || n.containsKey("bccExpression"), n.toString());
        Map<String, Object> s = GlueIntentGenerator.buildSchedulesForTest(IntentParser.parse(plain))
                                                   .get(0);
        assertFalse(s.containsKey("ccExpression") || s.containsKey("bccExpression"), s.toString());
    }

    /** An unresolvable copy recipient drops the notification, as an unresolvable to does. */
    @Test
    void anUnresolvableCopyRecipientDropsTheNotification() {
        String yaml = COPIES_EVERYWHERE.replace("    cc: Owner.email\n    bcc: \"@config:ARCHIVE_EMAIL\"\n    subject: \"New closing",
                "    cc: Owner.phone\n    bcc: \"@config:ARCHIVE_EMAIL\"\n    subject: \"New closing");
        assertTrue(GlueIntentGenerator.buildNotificationsForTest(IntentParser.parse(yaml))
                                      .isEmpty());
    }

    private static Map<String, Object> send(String yaml) {
        List<Map<String, Object>> sends = GlueIntentGenerator.buildSendsForTest(IntentParser.parse(yaml));
        assertEquals(1, sends.size(), sends.toString());
        return sends.get(0);
    }
}
