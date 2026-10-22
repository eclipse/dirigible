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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * A notify path that walks a chain of to-one relations ({@code {subscription.customer.name}},
 * dirigible #8030) is checked segment by segment when the file is read: an unresolvable one would
 * otherwise be mailed as its own literal text, with nothing reporting it.
 */
class NotifyPathIntentTest {

    private static final String YAML = """
            name: renewals
            entities:
              - name: Customer
                fields:
                  - { name: id,    type: integer, primaryKey: true, generated: true }
                  - { name: name,  type: string }
                  - { name: email, type: string }
              - name: Plan
                fields:
                  - { name: id,        type: integer, primaryKey: true, generated: true }
                  - { name: name,      type: string }
                  - { name: portalUrl, type: string }
              - name: Subscription
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                relations:
                  - { name: customer, kind: manyToOne, to: Customer, required: true }
                  - { name: plan,     kind: manyToOne, to: Plan,     required: true }
                  - { name: renewals, kind: oneToMany, to: Renewal }
              - name: Renewal
                fields:
                  - { name: id,          type: integer, primaryKey: true, generated: true }
                  - { name: requestedBy, type: string }
                relations:
                  - { name: subscription, kind: manyToOne, to: Subscription, required: true }
            notifications:
              - name: renewalRequested
                event: { onCreate: Renewal }
                to: requestedBy
                subject: "Your {subscription.plan.name} renewal for {subscription.customer.name}"
                body: "Open it at {subscription.plan.portalUrl}"
            """;

    private static final String BLOCK = "notification [renewalRequested]";

    @Test
    void aChainedPathParses() {
        assertEquals("renewals", IntentParser.parse(YAML)
                                             .getName());
    }

    /** A chained recipient used to be refused outright as a multi-hop path. */
    @Test
    void aChainedRecipientParses() {
        assertEquals("renewals", IntentParser.parse(YAML.replace("to: requestedBy", "to: subscription.customer.email"))
                                             .getName());
    }

    @Test
    void anUnknownSegmentIsRejected() {
        String issues = issues(YAML.replace("{subscription.customer.name}", "{subscription.custmer.name}"));
        assertTrue(
                issues.contains(BLOCK + " placeholder [{subscription.custmer.name}]: [custmer] is not a to-one relation of [Subscription]"),
                issues);
    }

    @Test
    void anUnknownFirstSegmentIsRejected() {
        String issues = issues(YAML.replace("{subscription.customer.name}", "{subscripton.customer.name}"));
        assertTrue(
                issues.contains(BLOCK + " placeholder [{subscripton.customer.name}]: [subscripton] is not a to-one relation of [Renewal]"),
                issues);
    }

    @Test
    void aToManySegmentIsRejected() {
        String issues = issues(YAML.replace("{subscription.customer.name}", "{subscription.renewals.requestedBy}"));
        assertTrue(issues.contains("[renewals] is not a to-one relation of [Subscription]"), issues);
    }

    @Test
    void anUnknownFieldAtTheEndIsRejected() {
        String issues = issues(YAML.replace("{subscription.customer.name}", "{subscription.customer.nmae}"));
        assertTrue(issues.contains(BLOCK + " placeholder [{subscription.customer.nmae}]: [nmae] is not a field of [Customer]"), issues);
    }

    @Test
    void anUnresolvableRecipientIsRejected() {
        String issues = issues(YAML.replace("to: requestedBy", "to: subscription.custmer.email"));
        assertTrue(issues.contains(BLOCK + " recipient [subscription.custmer.email]: [custmer] is not a to-one relation of [Subscription]"),
                issues);
    }

    /** A projection of another model's entity carries no relations, so nothing walks on past it. */
    @Test
    void aCrossModelRelationNotLastIsRejected() {
        String yaml = YAML.replace("name: renewals\n", "name: renewals\nuses:\n  - { model: billing }\n")
                          .replace("  - { name: subscription, kind: manyToOne, to: Subscription, required: true }",
                                  "  - { name: subscription, kind: manyToOne, to: Subscription, required: true, model: billing }");
        String issues = issues(yaml);
        assertTrue(issues.contains("cross-model relation [subscription] must be the last relation of the path"), issues);
    }

    /**
     * Every prefix is loaded into its segments joined by `_`, which must not be a relation's own name.
     */
    @Test
    void aLocalCollisionIsRejected() {
        String yaml = YAML.replace("  - { name: subscription, kind: manyToOne, to: Subscription, required: true }",
                "  - { name: subscription, kind: manyToOne, to: Subscription, required: true }\n"
                        + "      - { name: subscription_customer, kind: manyToOne, to: Customer }");
        String issues = issues(yaml);
        assertTrue(issues.contains(BLOCK + " placeholder [{subscription.customer.name}]: the relation path [subscription.customer] is"
                + " loaded into the local [subscription_customer], which is also the name of the relation [subscription_customer]"
                + " of [Renewal]"), issues);
    }

    /** Inside a fan-out every bare path walks from the ROW - here a contact, not the renewal. */
    @Test
    void aFanOutPathWalksFromTheRow() {
        String yaml = """
                name: renewals
                entities:
                  - name: Customer
                    fields:
                      - { name: id,   type: integer, primaryKey: true, generated: true }
                      - { name: name, type: string }
                  - name: Subscription
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                    relations:
                      - { name: customer, kind: manyToOne, to: Customer }
                  - name: RenewalStatus
                    function: Setting
                    fields:
                      - { name: id,   type: integer, primaryKey: true, generated: true }
                      - { name: name, type: string }
                  - name: Renewal
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                    relations:
                      - { name: Status, kind: manyToOne, to: RenewalStatus, function: EntityStatus, init: 1 }
                  - name: RenewalContact
                    fields:
                      - { name: id,    type: integer, primaryKey: true, generated: true }
                      - { name: email, type: string }
                    relations:
                      - { name: renewal,      kind: manyToOne, to: Renewal }
                      - { name: subscription, kind: manyToOne, to: Subscription }
                transitions:
                  - name: Remind
                    forEntity: Renewal
                    from: [1]
                    setStatus: 2
                    notify:
                      forEach: RenewalContact
                      to: email
                      subject: "Renewal for {subscription.customer.name}"
                      body: "Hello"
                """;
        assertEquals("renewals", IntentParser.parse(yaml)
                                             .getName());
        String issues = issues(yaml.replace("{subscription.customer.name}", "{subscription.customer.nmae}"));
        assertTrue(issues.contains("placeholder [{subscription.customer.nmae}]: [nmae] is not a field of [Customer]"), issues);
    }

    private static String issues(String yaml) {
        return assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml)).getMessage();
    }
}
