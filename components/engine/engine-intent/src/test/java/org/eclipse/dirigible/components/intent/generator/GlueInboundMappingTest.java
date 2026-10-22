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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.ide.template.service.model.GlueTemplateRendering;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * The arrival mapping reaches all three inbound collections through the one shared entry, so a
 * webhook, a queue consumer and a polled folder cannot drift in how they read the same envelope.
 */
class GlueInboundMappingTest {

    private static final String YAML = """
            name: provisioning
            entities:
              - name: Tenant
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: tenantId, type: string, unique: true }
                  - { name: name,     type: string }
              - name: TenantUserAssignment
                fields:
                  - { name: id,        type: integer, primaryKey: true, generated: true }
                  - { name: messageId, type: string, unique: true }
                  - { name: email,     type: string }
                relations:
                  - { name: tenant, kind: manyToOne, to: Tenant }
            inbound:
              - name: assignmentHook
                path: /assignments
                accept: { type: user.assignment.requested, version: 1 }
                create: TenantUserAssignment
                map:
                  messageId: messageId
                  email:     email
                  tenant:    { lookup: Tenant, by: tenantId, from: tenantId }
              - name: assignmentQueue
                source: { queue: "global:codbex.user-assignment-requests" }
                accept: { type: user.assignment.requested, version: 1 }
                create: TenantUserAssignment
                map:
                  messageId: messageId
                  tenant:    { lookup: Tenant, by: tenantId, from: tenantId }
              - name: assignmentDrop
                source: { folder: target/inbox, cron: "0/5 * * * * ?" }
                accept: { type: user.assignment.requested, version: 1 }
                create: TenantUserAssignment
                map:
                  messageId: messageId
                  tenant:    { lookup: Tenant, by: tenantId, from: tenantId }
            """;

    /** The map-less arrival, which must keep emitting exactly what it emitted before the feature. */
    private static final String PLAIN = """
            name: crm
            entities:
              - name: Lead
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
            inbound:
              - { name: leadHook, path: /webhooks/lead, create: Lead }
            """;

    /** An order with its lines (objects, one element lookup each) and its tags (bare values). */
    private static final String ORDERS = """
            name: shop
            entities:
              - name: Product
                fields:
                  - { name: id,  type: integer, primaryKey: true, generated: true }
                  - { name: sku, type: string, unique: true }
              - name: PurchaseOrder
                fields:
                  - { name: id,          type: integer, primaryKey: true, generated: true }
                  - { name: orderNumber, type: string, unique: true }
                relations:
                  - { name: lines, kind: oneToMany, to: OrderLine }
                  - { name: tags,  kind: oneToMany, to: OrderTag }
              - name: OrderLine
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: quantity, type: integer }
                relations:
                  - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder, composition: true }
                  - { name: product,       kind: manyToOne, to: Product }
              - name: OrderTag
                fields:
                  - { name: id,    type: integer, primaryKey: true, generated: true }
                  - { name: label, type: string }
                relations:
                  - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder, composition: true }
            inbound:
              - name: orderHook
                path: /orders
                create: PurchaseOrder
                map: &order
                  orderNumber: orderNumber
                  lines:
                    from: lines
                    max: 50
                    map:
                      quantity: qty
                      product: { lookup: Product, by: sku, from: sku }
                  tags: { from: tags, map: { label: "." } }
              - name: orderQueue
                source: { queue: orders }
                create: PurchaseOrder
                map: *order
              - name: orderDrop
                source: { folder: target/orders, cron: "0/5 * * * * ?" }
                create: PurchaseOrder
                map: *order
            """;

    @Test
    void everyArrivalKindCarriesTheSameGateAndProjection() {
        IntentModel model = IntentParser.parse(YAML);
        Map<String, Object> webhook = GlueIntentGenerator.buildInboundForTest(model)
                                                         .get(0);
        Map<String, Object> consumer = GlueIntentGenerator.buildInboundMessagesForTest(model)
                                                          .get(0);
        Map<String, Object> job = GlueIntentGenerator.buildInboundFilesForTest(model)
                                                     .get(0);

        for (Map<String, Object> entry : List.of(webhook, consumer, job)) {
            assertEquals(Boolean.TRUE, entry.get("hasEnvelope"));
            assertEquals(Boolean.TRUE, entry.get("hasAccept"));
            assertEquals(Boolean.TRUE, entry.get("hasMap"));
            assertEquals("type=user.assignment.requested, version=1", entry.get("acceptSummary"));
            assertTrue(entry.get("acceptExpression")
                            .toString()
                            .contains("\"user.assignment.requested\".equals(envelope.get(\"type\"))"),
                    "the gate is pre-rendered as one Java expression: " + entry.get("acceptExpression"));

            List<?> lookups = (List<?>) entry.get("lookups");
            assertEquals(1, lookups.size());
            Map<?, ?> lookup = (Map<?, ?>) lookups.get(0);
            assertEquals("Tenant", lookup.get("property"));
            assertEquals("Tenant", lookup.get("targetEntity"));
            assertEquals("TenantId", lookup.get("byProperty"));
            assertEquals("Id", lookup.get("targetKeyProperty"));
            assertEquals("String.valueOf(lookupTenantKey)", lookup.get("byValueExpression"));
        }

        // The projection is the entry's own, not a shared one: only the webhook maps the e-mail. A
        // lookup is not a mapped field - it is a query, so it lands in its own collection.
        assertEquals(List.of("MessageId", "Email"), propertiesOf(webhook));
        assertEquals(List.of("MessageId"), propertiesOf(consumer));
    }

    private static List<String> propertiesOf(Map<String, Object> entry) {
        return ((List<?>) entry.get("mapFields")).stream()
                                                 .map(field -> String.valueOf(((Map<?, ?>) field).get("property")))
                                                 .toList();
    }

    @Test
    void anArrivalWithoutTheKeysDeclaresNoMappingAtAll() {
        Map<String, Object> webhook = GlueIntentGenerator.buildInboundForTest(IntentParser.parse(PLAIN))
                                                         .get(0);

        // Present but false, never absent - a template branches on them, and an undefined Velocity
        // variable renders as its own literal name.
        assertEquals(Boolean.FALSE, webhook.get("hasEnvelope"));
        assertEquals(Boolean.FALSE, webhook.get("hasAccept"));
        assertEquals(Boolean.FALSE, webhook.get("hasMap"));
        assertTrue(((List<?>) webhook.get("mapFields")).isEmpty());
        assertTrue(((List<?>) webhook.get("lookups")).isEmpty());
        assertEquals("", webhook.get("acceptExpression"));
    }

    @Test
    void everyArrivalKindCarriesTheSameCollections() {
        IntentModel model = IntentParser.parse(ORDERS);
        Map<String, Object> webhook = GlueIntentGenerator.buildInboundForTest(model)
                                                         .get(0);
        Map<String, Object> consumer = GlueIntentGenerator.buildInboundMessagesForTest(model)
                                                          .get(0);
        Map<String, Object> job = GlueIntentGenerator.buildInboundFilesForTest(model)
                                                     .get(0);

        for (Map<String, Object> entry : List.of(webhook, consumer, job)) {
            assertEquals(Boolean.TRUE, entry.get("hasMap"));
            assertEquals(Boolean.TRUE, entry.get("hasCollections"));
            assertEquals(List.of("OrderNumber"), propertiesOf(entry), "a collection is not a top-level field");
            assertTrue(((List<?>) entry.get("lookups")).isEmpty(), "an element lookup is the collection's, not the record's");

            List<?> collections = (List<?>) entry.get("collections");
            assertEquals(2, collections.size());
            Map<?, ?> lines = (Map<?, ?>) collections.get(0);
            assertEquals("collectionLines", lines.get("local"));
            assertEquals("\"lines\"", lines.get("fromLiteral"));
            assertEquals(Boolean.TRUE, lines.get("hasMax"));
            assertEquals("50", lines.get("max"));
            assertEquals("OrderLine", lines.get("childEntity"));
            assertEquals("PurchaseOrder", lines.get("childPerspective"));
            assertEquals("PurchaseOrder", lines.get("backRef"));
            assertEquals("Id", lines.get("masterKey"));
            assertEquals(Boolean.FALSE, lines.get("scalarElements"));
            assertEquals(List.of("Quantity"), elementPropertiesOf(lines));
            Map<?, ?> product = (Map<?, ?>) ((List<?>) lines.get("lookups")).get(0);
            assertEquals("lookupLinesProduct", product.get("local"));
            assertEquals("Product", product.get("targetEntity"));
            assertEquals("Sku", product.get("byProperty"));
            assertEquals("String.valueOf(lookupLinesProductKey)", product.get("byValueExpression"));

            Map<?, ?> tags = (Map<?, ?>) collections.get(1);
            assertEquals(Boolean.TRUE, tags.get("scalarElements"));
            assertEquals(Boolean.FALSE, tags.get("hasMax"));
            assertEquals(List.of("Label"), elementPropertiesOf(tags));
            assertTrue(((List<?>) tags.get("lookups")).isEmpty());
        }
    }

    @Test
    void anArrivalWithoutCollectionsCarriesThemEmpty() {
        IntentModel model = IntentParser.parse(YAML);
        List<Map<String, Object>> entries = List.of(GlueIntentGenerator.buildInboundForTest(model)
                                                                       .get(0),
                GlueIntentGenerator.buildInboundMessagesForTest(model)
                                   .get(0),
                GlueIntentGenerator.buildInboundFilesForTest(model)
                                   .get(0),
                GlueIntentGenerator.buildInboundForTest(IntentParser.parse(PLAIN))
                                   .get(0));
        for (Map<String, Object> entry : entries) {
            assertEquals(Boolean.FALSE, entry.get("hasCollections"));
            assertTrue(((List<?>) entry.get("collections")).isEmpty());
        }
    }

    private static List<String> elementPropertiesOf(Map<?, ?> collection) {
        return ((List<?>) collection.get("mapFields")).stream()
                                                      .map(field -> String.valueOf(((Map<?, ?>) field).get("property")))
                                                      .toList();
    }

    /** An order naming its customer by a business key - the arrival #8031 is about. */
    private static final String CUSTOMER_ORDERS = """
            name: sales
            entities:
              - name: Customer
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string, unique: true }
              - name: Order
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: number, type: string, unique: true, required: true }
                relations:
                  - { name: customer, kind: manyToOne, to: Customer, required: true }
            inbound:
              - name: orders
                source: { queue: "orders.placed" }
                accept: { type: order.placed, version: 1 }
                create: Order
                map:
                  number:   number
                  customer: { lookup: Customer, by: code, from: customerCode%s }
            """;

    /**
     * The order lines of {@link #ORDERS}, arriving on a queue only - a webhook does not take the key.
     */
    private static final String QUEUED_LINES = """
            name: shop
            entities:
              - name: Product
                fields:
                  - { name: id,  type: integer, primaryKey: true, generated: true }
                  - { name: sku, type: string, unique: true }
              - name: PurchaseOrder
                fields:
                  - { name: id,          type: integer, primaryKey: true, generated: true }
                  - { name: orderNumber, type: string, unique: true }
                relations:
                  - { name: lines, kind: oneToMany, to: OrderLine }
              - name: OrderLine
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: quantity, type: integer }
                relations:
                  - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder, composition: true }
                  - { name: product,       kind: manyToOne, to: Product }
            inbound:
              - name: orderQueue
                source: { queue: orders }
                create: PurchaseOrder
                map:
                  orderNumber: orderNumber
                  lines:
                    from: lines
                    map:
                      quantity: qty
                      product: { lookup: Product, by: sku, from: sku, onMissing: deadLetter }
            """;

    /**
     * Both misses of a dead-letter lookup THROW: the listener container logs the failure and hands the
     * message back to the broker, which retries it and then dead-letters it. A {@code return} would
     * acknowledge it - the very loss the key exists to prevent.
     */
    @Test
    void aDeadLetterLookupThrowsOnBothMisses() throws IOException {
        String consumer = renderConsumer(CUSTOMER_ORDERS.formatted(", onMissing: deadLetter"));

        String noKey = branch(consumer, "if (lookupCustomerKey == null) {");
        assertTrue(noKey.contains("throw new IllegalStateException(\"Inbound orders: a message on [\" + destination() + \"] carries no [\""
                + " + \"customerCode\""), noKey);
        assertTrue(noKey.contains("to resolve Customer by - dead-lettered\");"), noKey);
        String noMatch = branch(consumer, "if (lookupCustomerMatches.size() != 1) {");
        assertTrue(
                noMatch.contains(
                        "throw new IllegalStateException(\"Inbound orders: no unique Customer matches Code [\" + lookupCustomerKey"),
                noMatch);
        assertTrue(noMatch.contains("+ \"] from [\" + \"customerCode\" + \"] - dead-lettered\");"), noMatch);
        for (String branch : List.of(noKey, noMatch)) {
            assertTrue(!branch.contains("return;") && !branch.contains("LOG."),
                    "the consumer does not log the miss itself - the listener container logs every throwing handler: " + branch);
        }
        assertTrue(consumer.contains("A lookup declared onMissing: deadLetter rejects it by THROWING"),
                "the class javadoc says which way the message is rejected");
    }

    /**
     * A lookup without the key - or with the default spelled out - generates exactly what it did before
     * the key existed: the miss is logged and the message acknowledged.
     */
    @Test
    void aDropLookupIsUnchanged() throws IOException {
        String withoutKey = renderConsumer(CUSTOMER_ORDERS.formatted(""));

        assertEquals(withoutKey, renderConsumer(CUSTOMER_ORDERS.formatted(", onMissing: drop")));
        assertEquals(
                """
                        if (lookupCustomerKey == null) {
                                    LOG.error("Inbound orders: a message on [{}] carries no [{}] to resolve Customer by - NOT ingested", destination(),
                                            "customerCode");
                                    return;
                                }
                        """,
                branch(withoutKey, "if (lookupCustomerKey == null) {"));
        assertEquals("""
                if (lookupCustomerMatches.size() != 1) {
                            LOG.error("Inbound orders: no unique Customer matches Code [{}] from [{}] - NOT ingested",
                                    lookupCustomerKey, "customerCode");
                            return;
                        }
                """, branch(withoutKey, "if (lookupCustomerMatches.size() != 1) {"));
        assertTrue(!withoutKey.contains("onMissing") && !withoutKey.contains("dead-letter"), withoutKey);
    }

    /**
     * An element lookup dead-letters the same way. Every element is resolved before anything is
     * written, so throwing there leaves nothing half-saved; the other element rejections keep their
     * behaviour.
     */
    @Test
    void aCollectionElementLookupDeadLetters() throws IOException {
        String consumer = renderConsumer(QUEUED_LINES);

        String noKey = branch(consumer, "if (lookupLinesProductKey == null) {");
        assertTrue(
                noKey.contains("throw new IllegalStateException(\"Inbound orderQueue: a message on [\" + destination() + \"]: element [\""
                        + " + index + \"] of [\""),
                noKey);
        assertTrue(noKey.contains("+ \"] to resolve Product by - dead-lettered\");"), noKey);
        String noMatch = branch(consumer, "if (lookupLinesProductMatches.size() != 1) {");
        assertTrue(noMatch.contains("]: no unique Product matches Sku [\" + lookupLinesProductKey"), noMatch);
        assertTrue(noMatch.contains("- dead-lettered\");"), noMatch);
        for (String branch : List.of(noKey, noMatch)) {
            assertTrue(!branch.contains("return;") && !branch.contains("LOG."), branch);
        }
        assertTrue(consumer.contains("is not an object - NOT ingested"), "an element of the wrong shape is still acknowledged");
    }

    /** The usage report of #8033: it names its subscription by the customer and plan codes. */
    private static final String USAGE = """
            name: billing
            entities:
              - name: Customer
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string, unique: true }
              - name: Plan
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string, unique: true }
              - name: Subscription
                unique:
                  - fields: [customer, plan]
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: region, type: string }
                relations:
                  - { name: customer, kind: manyToOne, to: Customer, required: true }
                  - { name: plan,     kind: manyToOne, to: Plan,     required: true }
              - name: UsageReport
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: reportId, type: string, unique: true, required: true }
                  - { name: units,    type: integer }
                relations:
                  - { name: subscription, kind: manyToOne, to: Subscription, required: true }
            inbound:
              - name: usage
                source: { queue: "usage.reported" }
                create: UsageReport
                map: &usage
                  reportId: reportId
                  units:    units
                  subscription: { lookup: Subscription, by: [customer.code, plan.code], from: [customerCode, planCode] }
              - name: usageHook
                path: /usage
                create: UsageReport
                map: *usage
              - name: usageDrop
                source: { folder: target/usage, cron: "0/5 * * * * ?" }
                create: UsageReport
                map: *usage
            """;

    /** The query that fills the relation: every hop resolved, then ONE query matching both parts. */
    private static final String COMPOSITE_QUERY = """
                    java.util.List<gen.sales.data.subscription.SubscriptionEntity> lookupSubscriptionMatches =
                            new gen.sales.data.subscription.SubscriptionRepository()
                                    .findAll(Criteria.create()
                                                     .eq("Customer", lookupSubscriptionByCustomerMatches.get(0).Id)
                                                     .eq("Plan", lookupSubscriptionByPlanMatches.get(0).Id));
            """;

    @Test
    void aCompositeLookupIsPlannedAsItsPartsOnEveryArrival() {
        IntentModel model = IntentParser.parse(USAGE);
        for (Map<String, Object> entry : List.of(GlueIntentGenerator.buildInboundForTest(model)
                                                                    .get(0),
                GlueIntentGenerator.buildInboundMessagesForTest(model)
                                   .get(0),
                GlueIntentGenerator.buildInboundFilesForTest(model)
                                   .get(0))) {
            Map<?, ?> lookup = (Map<?, ?>) ((List<?>) entry.get("lookups")).get(0);
            assertEquals(Boolean.TRUE, lookup.get("composite"));
            assertEquals("Subscription", lookup.get("targetEntity"));
            assertEquals("Customer.Code, Plan.Code", lookup.get("byProperty"));
            assertEquals("java.util.List.of(lookupSubscriptionByCustomerKey, lookupSubscriptionByPlanKey)", lookup.get("keyList"));
            List<?> terms = (List<?>) lookup.get("terms");
            assertEquals(2, terms.size());
            Map<?, ?> customer = (Map<?, ?>) terms.get(0);
            assertEquals("Customer.Code", customer.get("label"));
            assertEquals("\"customerCode\"", customer.get("fromLiteral"));
            assertEquals(Boolean.TRUE, customer.get("hop"));
            assertEquals("Customer", customer.get("hopEntity"));
            assertEquals("Code", customer.get("hopByProperty"));
            assertEquals("String.valueOf(lookupSubscriptionByCustomerKey)", customer.get("hopByValueExpression"));
            assertEquals("Customer", customer.get("property"));
            assertEquals("lookupSubscriptionByCustomerMatches.get(0).Id", customer.get("valueExpression"));
        }
    }

    @Test
    void aCompositeLookupResolvesEachHopThenQueriesOnce() throws IOException {
        String consumer = renderConsumer(USAGE);

        String customer = branch(consumer, "java.util.List<gen.sales.data.customer.CustomerEntity> lookupSubscriptionByCustomerMatches =");
        assertTrue(customer.contains(".eq(\"Code\", String.valueOf(lookupSubscriptionByCustomerKey)));"), customer);
        assertTrue(consumer.contains(".eq(\"Code\", String.valueOf(lookupSubscriptionByPlanKey)));"), consumer);
        assertTrue(consumer.contains(COMPOSITE_QUERY), consumer);
        assertTrue(consumer.indexOf("lookupSubscriptionByPlanMatches =") < consumer.indexOf("lookupSubscriptionMatches ="),
                "every hop is resolved before the target is queried");
        assertTrue(consumer.contains("entity.Subscription = lookupSubscriptionMatches.get(0).Id;"), consumer);
    }

    @Test
    void aCompositeMissLogsEveryKey() throws IOException {
        String consumer = renderConsumer(USAGE);

        assertEquals(
                """
                        if (lookupSubscriptionMatches.size() != 1) {
                                    LOG.error("Inbound usage: no unique Subscription matches {} from [{}] - NOT ingested",
                                            "Customer.Code [" + lookupSubscriptionByCustomerKey + "], Plan.Code [" + lookupSubscriptionByPlanKey + "]", "customerCode, planCode");
                                    return;
                                }
                        """,
                branch(consumer, "if (lookupSubscriptionMatches.size() != 1) {"));
        String hop = branch(consumer, "if (lookupSubscriptionByPlanMatches.size() != 1) {");
        assertTrue(hop.contains("no unique Subscription matches {} from [{}] - no unique Plan matches Plan.Code - NOT ingested"), hop);
        assertTrue(hop.contains("\"Customer.Code [\" + lookupSubscriptionByCustomerKey + \"], Plan.Code [\""), hop);
        String noKey = branch(consumer, "if (lookupSubscriptionByPlanKey == null) {");
        assertTrue(noKey.contains("carries no [{}] to resolve Subscription by - NOT ingested"), noKey);
        assertTrue(noKey.contains("\"planCode\");"), noKey);
    }

    /** A webhook answers every composite miss with 400, and a drop file fails the whole file. */
    @Test
    void aCompositeLookupOnTheWebhookAndTheFileJob() throws IOException {
        IntentModel model = IntentParser.parse(USAGE);
        // Line ends normalized as in renderConsumer: the templates carry the checkout's (CRLF on Windows).
        String webhook = GlueTemplateRendering.renderWebhook(GlueIntentGenerator.buildInboundForTest(model)
                                                                                .get(0),
                "sales")
                                              .replace("\r\n", "\n");
        String job = GlueTemplateRendering.renderInboundFile(GlueIntentGenerator.buildInboundFilesForTest(model)
                                                                                .get(0),
                "sales")
                                          .replace("\r\n", "\n");

        for (String source : List.of(webhook, job)) {
            assertTrue(source.contains(COMPOSITE_QUERY), source);
            assertTrue(source.contains("entity.Subscription = lookupSubscriptionMatches.get(0).Id;"), source);
        }
        String rejected = branch(webhook, "if (lookupSubscriptionMatches.size() != 1) {");
        assertTrue(rejected.contains("Response.setStatus(400);"), rejected);
        assertTrue(rejected.contains("no unique Subscription matches the Customer.Code, Plan.Code in the payload"), rejected);
        assertTrue(
                branch(webhook, "if (lookupSubscriptionByCustomerKey == null) {").contains("no Customer.Code to resolve Subscription by"));
        assertTrue(branch(job, "if (lookupSubscriptionMatches.size() != 1) {").contains(
                "throw new IllegalArgumentException(\"record \" + record + \": no unique Subscription matches \" + \"Customer.Code [\""));
    }

    /** The per-lookup onMissing of #8031 applies to the whole composite lookup. */
    @Test
    void aCompositeLookupDeadLettersEveryMiss() throws IOException {
        // The queue arrival alone: a webhook or a drop folder does not take the key.
        String queued = USAGE.substring(0, USAGE.indexOf("  - name: usageHook"));
        String consumer = renderConsumer(
                queued.replace("from: [customerCode, planCode] }", "from: [customerCode, planCode], onMissing: deadLetter }"));

        for (String opening : List.of("if (lookupSubscriptionByCustomerKey == null) {",
                "if (lookupSubscriptionByPlanMatches.size() != 1) {", "if (lookupSubscriptionMatches.size() != 1) {")) {
            String branch = branch(consumer, opening);
            assertTrue(branch.contains("throw new IllegalStateException(") && branch.contains("- dead-lettered\");"), branch);
            assertTrue(!branch.contains("return;") && !branch.contains("LOG."), branch);
        }
    }

    private static final String USAGE_LINES = """
            name: billing
            entities:
              - name: Customer
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string, unique: true }
              - name: Subscription
                unique:
                  - fields: [customer, region]
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: region, type: string }
                relations:
                  - { name: customer, kind: manyToOne, to: Customer, required: true }
              - name: UsageBatch
                fields:
                  - { name: id,      type: integer, primaryKey: true, generated: true }
                  - { name: batchId, type: string, unique: true }
                relations:
                  - { name: lines, kind: oneToMany, to: UsageLine }
              - name: UsageLine
                fields:
                  - { name: id,    type: integer, primaryKey: true, generated: true }
                  - { name: units, type: integer }
                relations:
                  - { name: batch,        kind: manyToOne, to: UsageBatch, composition: true }
                  - { name: subscription, kind: manyToOne, to: Subscription }
            inbound:
              - name: usageBatches
                source: { queue: "usage.batches" }
                create: UsageBatch
                map:
                  batchId: batchId
                  lines:
                    from: lines
                    map:
                      units: units
                      subscription: { lookup: Subscription, by: [customer.code, region], from: [customerCode, region] }
            """;

    /**
     * An element's composite lookup reads its parts from the element, and caches its matches by the
     * whole combination - a field part compared with its converted value, a hop through its key.
     */
    @Test
    void aCompositeLookupInACollectionElement() throws IOException {
        String consumer = renderConsumer(USAGE_LINES);

        assertTrue(consumer.contains("Object lookupLinesSubscriptionByCustomerKey = item.get(\"customerCode\");"), consumer);
        assertTrue(consumer.contains("Object lookupLinesSubscriptionByRegionKey = item.get(\"region\");"), consumer);
        assertTrue(consumer.contains("java.util.List<Object> lookupLinesSubscriptionKey ="
                + " java.util.List.of(lookupLinesSubscriptionByCustomerKey, lookupLinesSubscriptionByRegionKey);"), consumer);
        assertTrue(consumer.contains(".eq(\"Customer\", lookupLinesSubscriptionByCustomerMatches.get(0).Id)\n"), consumer);
        assertTrue(consumer.contains(".eq(\"Region\", String.valueOf(lookupLinesSubscriptionByRegionKey)));\n"), consumer);
        assertTrue(consumer.contains("lookupLinesSubscriptionCache.put(lookupLinesSubscriptionKey, lookupLinesSubscriptionMatch);"),
                consumer);
        assertTrue(consumer.contains("child.Subscription = lookupLinesSubscriptionMatch.Id;"), consumer);
        assertTrue(!consumer.contains("lookupLinesSubscriptionByRegionMatches"), "a field part is not resolved by a query of its own");
    }

    /** A single-key lookup carries none of the composite keys, so its descriptor is what it was. */
    @Test
    void theScalarFormIsUnchanged() {
        IntentModel model = IntentParser.parse(YAML);
        for (Map<String, Object> entry : List.of(GlueIntentGenerator.buildInboundForTest(model)
                                                                    .get(0),
                GlueIntentGenerator.buildInboundMessagesForTest(model)
                                   .get(0),
                GlueIntentGenerator.buildInboundFilesForTest(model)
                                   .get(0))) {
            Map<?, ?> lookup = (Map<?, ?>) ((List<?>) entry.get("lookups")).get(0);
            assertEquals(List.of("property", "local", "from", "fromLiteral", "targetEntity", "targetPerspective", "byProperty",
                    "byValueExpression", "targetKeyProperty", "deadLetter"), List.copyOf(lookup.keySet()));
        }
    }

    /**
     * The rendered source with {@code \n} line ends. The template's own line ends are the checkout's
     * (CRLF on a Windows runner), and the blocks asserted here are matched line by line.
     */
    private static String renderConsumer(String yaml) throws IOException {
        Map<String, Object> entry = GlueIntentGenerator.buildInboundMessagesForTest(IntentParser.parse(yaml))
                                                       .get(0);
        return GlueTemplateRendering.renderInboundMessage(entry, "sales")
                                    .replace("\r\n", "\n");
    }

    /** The block a line opens, up to the brace closing it at the same indentation. */
    private static String branch(String source, String opening) {
        int start = source.indexOf(opening);
        assertTrue(start >= 0, "no [" + opening + "] in:\n" + source);
        int lineStart = source.lastIndexOf('\n', start) + 1;
        String closing = "\n" + " ".repeat(start - lineStart) + "}\n";
        int end = source.indexOf(closing, start);
        assertTrue(end >= 0, "[" + opening + "] is never closed");
        return source.substring(start, end + closing.length());
    }
}
