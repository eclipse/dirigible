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

    private static Map<String, Object> send(String yaml) {
        List<Map<String, Object>> sends = GlueIntentGenerator.buildSendsForTest(IntentParser.parse(yaml));
        assertEquals(1, sends.size(), sends.toString());
        return sends.get(0);
    }
}
