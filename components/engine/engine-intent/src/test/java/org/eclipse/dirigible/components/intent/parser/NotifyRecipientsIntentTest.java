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

import org.junit.jupiter.api.Test;

/**
 * A notify block's copy recipients and a recipient read off the process - dirigible #7935.
 *
 * <p>
 * A declared {@code notify:} had one recipient key, {@code to:}, naming a field of the record, a
 * one-hop {@code relation.field}, an {@code @config:KEY} or a literal. A list an earlier step
 * computes (the approvers of a document) is carried by no field, and a copy to an archive mailbox
 * is not the addressee - both needed a {@code delegate:} calling {@code Mail.send} itself, which
 * moved the message out of the model and lost the step's declared resilience.
 *
 * <p>
 * {@code cc:} / {@code bcc:} take the forms {@code to:} takes, and every recipient key also takes
 * {@code @var:<name>} - on a process step only, for a var the process declares and the step lists
 * in {@code uses:}.
 */
class NotifyRecipientsIntentTest {

    private static final String YAML =
            """
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
                    transitions:
                      - name: Reopen
                        forEntity: ClosingRequest
                        from: [2]
                        setStatus: 1
                        notify:
                          to: requestedBy
                          subject: "Closing {name} was reopened"
                          body: "Closing {name} was reopened."
                    """;

    @Test
    void theIssueExampleParses() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML));
    }

    /** cc/bcc accept exactly the forms to accepts. */
    @Test
    void copyRecipientsTakeEveryRecipientForm() {
        for (String form : new String[] {"requestedBy", "Owner.email", "\"@config:SUPPORT_EMAIL\"", "\"@var:approverEmails\"",
                "archive@example.com", "\"a@example.com, b@example.com\""}) {
            assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("cc: requestedBy", "cc: " + form)), "cc: " + form);
            assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("bcc: \"@config:ARCHIVE_EMAIL\"", "bcc: " + form)), "bcc: " + form);
        }
    }

    /** A copy to an archive mailbox needs no addressee of its own. */
    @Test
    void aStepWithOnlyABccParses() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("            to: \"@var:approverEmails\"\n", "")
                                                        .replace("            cc: requestedBy\n", "")));
    }

    @Test
    void aStepWithNoRecipientAtAllIsRefused() {
        assertIssue(YAML.replace("            to: \"@var:approverEmails\"\n", "")
                        .replace("            cc: requestedBy\n", "")
                        .replace("            bcc: \"@config:ARCHIVE_EMAIL\"\n", ""),
                "process [Closing] step [notify] notify has no recipient (to)");
    }

    @Test
    void anUndeclaredVarIsRefused() {
        assertIssue(YAML.replace("@var:approverEmails", "@var:approvers"),
                "process [Closing] step [notify] notify recipient [@var:approvers] names undeclared var [approvers]");
    }

    @Test
    void aVarTheStepDoesNotUseIsRefused() {
        assertIssue(YAML.replace("uses: [approverEmails]", "uses: []"),
                "process [Closing] step [notify] notify recipient [@var:approverEmails] reads var [approverEmails], which the step does"
                        + " not list in `uses:`");
    }

    @Test
    void aVarInACopyRecipientFollowsTheSameRules() {
        assertIssue(YAML.replace("cc: requestedBy", "cc: \"@var:watchers\""),
                "process [Closing] step [notify] notify cc [@var:watchers] names undeclared var [watchers]");
    }

    @Test
    void anEmptyVarNameIsRefused() {
        assertIssue(YAML.replace("bcc: \"@config:ARCHIVE_EMAIL\"", "bcc: \"@var:\""),
                "process [Closing] step [notify] notify bcc [@var:] has an empty @var: name");
    }

    @Test
    void aVarOutsideAProcessStepIsRefusedNamingTheBlock() {
        assertIssue(YAML.replace("      to: requestedBy\n", "      to: \"@var:approverEmails\"\n"),
                "transition [Reopen] notify recipient [@var:approverEmails] reads a process variable, which only a process step's notify can");
    }

    @Test
    void aVarInANotificationEntryIsRefused() {
        assertIssue(YAML + """
                notifications:
                  - name: closingCreated
                    event: { onCreate: ClosingRequest }
                    to: "@var:approverEmails"
                    subject: "New closing"
                    body: "New closing."
                """, "notification [closingCreated] recipient [@var:approverEmails] reads a process variable");
    }

    @Test
    void aVarInAScheduleIsRefused() {
        assertIssue(YAML + """
                schedules:
                  - name: nudge
                    cron: "0 0 8 * * ?"
                    entity: ClosingRequest
                    notify:
                      to: "@var:approverEmails"
                      subject: "Pending"
                      body: "Pending."
                """, "schedule [nudge] notify recipient [@var:approverEmails] reads a process variable");
    }

    /** cc/bcc are generated on every notify block (#7938), not only a process step's. */
    @Test
    void aTransitionTakesCopyRecipients() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("      to: requestedBy\n",
                "      to: requestedBy\n      cc: Owner.email\n      bcc: \"@config:ARCHIVE_EMAIL\"\n")));
    }

    /** A copy recipient is enough on any block - the archive mailbox needs no addressee of its own. */
    @Test
    void aTransitionWithOnlyABccParses() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("      to: requestedBy\n", "      bcc: archive@example.com\n")));
    }

    @Test
    void aNotificationEntryTakesCopyRecipients() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML + """
                notifications:
                  - name: closingCreated
                    event: { onCreate: ClosingRequest }
                    to: requestedBy
                    cc: Owner.email
                    bcc: "@config:ARCHIVE_EMAIL"
                    subject: "New closing"
                    body: "New closing."
                """));
    }

    @Test
    void aScheduleTakesCopyRecipients() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML + """
                schedules:
                  - name: nudge
                    cron: "0 0 8 * * ?"
                    entity: ClosingRequest
                    notify:
                      to: requestedBy
                      cc: Owner.email
                      bcc: "@config:ARCHIVE_EMAIL"
                      subject: "Pending"
                      body: "Pending."
                """));
    }

    /**
     * Outside a process step there is no instance to read, so @var: stays refused - in a copy key too.
     */
    @Test
    void aVarInATransitionCopyRecipientIsRefused() {
        assertIssue(YAML.replace("      to: requestedBy\n", "      to: requestedBy\n      bcc: \"@var:approverEmails\"\n"),
                "transition [Reopen] notify bcc [@var:approverEmails] reads a process variable");
    }

    /**
     * A fan-out's copy recipient resolves per ROW, so a record-scoped one would be copied once per row.
     */
    @Test
    void aRecordScopedCopyRecipientInAFanOutIsRefused() {
        String fanOut = YAML.replace("""
                      - { name: Status, kind: manyToOne, to: ClosingStatus, function: EntityStatus, init: 1 }
                """, """
                      - { name: Status, kind: manyToOne, to: ClosingStatus, function: EntityStatus, init: 1 }
                  - name: ClosingTask
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: email, type: string }
                    relations:
                      - { name: Request, kind: manyToOne, to: ClosingRequest }
                """)
                            .replace("      to: requestedBy\n",
                                    "      forEach: ClosingTask\n      to: email\n      cc: record.requestedBy\n");
        assertIssue(fanOut, "transition [Reopen] notify cc [record.requestedBy] is record-scoped");
    }

    @Test
    void aMultiHopCopyRecipientIsRefused() {
        assertIssue(YAML.replace("cc: requestedBy", "cc: Owner.Manager.email"),
                "process [Closing] step [notify] notify cc [Owner.Manager.email] uses a multi-hop path");
    }

    @Test
    void anEmptyConfigKeyInACopyRecipientIsRefused() {
        assertIssue(YAML.replace("@config:ARCHIVE_EMAIL", "@config:"),
                "process [Closing] step [notify] notify bcc [@config:] has an empty" + " @config: key");
    }

    /** A misspelled key is a typo, not a silently ignored recipient. */
    @Test
    void aMisspelledCopyKeyIsRefused() {
        IntentValidationException ex = assertThrows(IntentValidationException.class,
                () -> IntentParser.parse(YAML.replace("cc: requestedBy", "ccc: requestedBy")));
        assertEquals(true, String.join("; ", ex.getIssues())
                                 .contains("ccc"),
                "expected the unknown key [ccc] to be reported, got " + ex.getIssues());
    }

    private static String assertIssue(String yaml, String expected) {
        IntentValidationException ex = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        String issue = ex.getIssues()
                         .stream()
                         .filter(i -> i.contains(expected))
                         .findFirst()
                         .orElse(null);
        assertEquals(true, issue != null, "expected an issue containing [" + expected + "] but got " + ex.getIssues());
        return issue;
    }
}
