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

import org.eclipse.dirigible.components.intent.model.InboundIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.junit.jupiter.api.Test;

/**
 * The parse-time half of mapping on arrival - the {@code accept:} gate and the {@code map:}
 * projection, including the business-key lookups that fill a record's relations.
 *
 * <p>
 * The rule worth the most here is the uniqueness of a lookup's {@code by:}: a lookup that could
 * match several rows would silently pick one, which is a worse outcome than failing, so it is
 * refused when the intent is read rather than discovered in production.
 */
class InboundMappingIntentTest {

    private static final String ENTITIES = """
            name: provisioning
            entities:
              - name: Tenant
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: tenantId, type: string, unique: true }
                  - { name: name,     type: string }
                  - { name: openedOn, type: date, unique: true }
              - name: AssignmentRole
                kind: setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string, unique: true }
              - name: TenantUserAssignment
                fields:
                  - { name: id,        type: integer, primaryKey: true, generated: true }
                  - { name: messageId, type: string, unique: true }
                  - { name: email,     type: string }
                relations:
                  - { name: tenant, kind: manyToOne, to: Tenant }
                  - { name: role,   kind: manyToOne, to: AssignmentRole }
            """;

    @Test
    void anArrivalParsesWithItsGateAndItsMappedLookups() {
        IntentModel model = IntentParser.parse(ENTITIES + """
                inbound:
                  - name: userAssignments
                    source: { queue: "global:codbex.user-assignment-requests" }
                    accept: { type: user.assignment.requested, version: 1 }
                    create: TenantUserAssignment
                    map:
                      messageId: messageId
                      email:     email
                      tenant:    { lookup: Tenant,         by: tenantId, from: tenantId }
                      role:      { lookup: AssignmentRole, by: name,     from: role }
                """);

        InboundIntent arrival = model.getInbound()
                                     .get(0);
        assertEquals("user.assignment.requested", arrival.getAccept()
                                                         .get("type"));
        assertEquals(4, arrival.getMap()
                               .size());
        assertEquals("messageId", arrival.getMap()
                                         .get("messageId"));
        assertTrue(arrival.getMap()
                          .get("tenant") instanceof java.util.Map,
                "a lookup value stays a map the generator reads");
    }

    /** The gate and the map are about the payload, so they are valid on all three arrivals. */
    @Test
    void aWebhookAndAPolledFolderTakeTheSameMapping() {
        IntentModel model = IntentParser.parse(ENTITIES + """
                inbound:
                  - name: assignmentHook
                    path: /assignments
                    accept: { type: user.assignment.requested }
                    create: TenantUserAssignment
                    map:
                      email:  email
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId }
                  - name: assignmentDrop
                    source: { folder: target/inbox, cron: "0/5 * * * * ?" }
                    accept: { type: user.assignment.requested }
                    create: TenantUserAssignment
                    map:
                      email:  email
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId }
                """);

        assertEquals(2, model.getInbound()
                             .size());
    }

    @Test
    void aLookupOnANonUniqueFieldFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: name, from: tenantName }
                """, "which is not unique on [Tenant]");
    }

    /**
     * The primary key is unique by construction, so it needs no flag - lookup by id is the strict case.
     */
    @Test
    void aLookupOnThePrimaryKeyIsAccepted() {
        IntentParser.parse(ENTITIES + """
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: id, from: tenantId }
                """);
    }

    @Test
    void aLookupOnATemporalFieldFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: openedOn, from: openedOn }
                """, "a business key is a string or an integer field");
    }

    @Test
    void aLookupOfAnEntityTheRelationDoesNotTargetFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: AssignmentRole, by: name, from: role }
                """, "looks up [AssignmentRole] but [tenant] relates to [Tenant]");
    }

    @Test
    void aLookupOnAPlainFieldFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      email: { lookup: Tenant, by: tenantId, from: tenantId }
                """, "which is not a to-one relation");
    }

    @Test
    void anIncompleteLookupFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId }
                """, "has no from");
    }

    @Test
    void anUnknownKeyInsideALookupFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId, form: tenantId }
                """, "declares unknown key [form]");
    }

    /** A queue and a topic both have dead letters, so both take either outcome of a miss. */
    @Test
    void onMissingParsesOnAQueueAndATopic() {
        IntentParser.parse(ENTITIES + """
                inbound:
                  - name: queued
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant,         by: tenantId, from: tenantId, onMissing: deadLetter }
                      role:   { lookup: AssignmentRole, by: name,     from: role,     onMissing: drop }
                  - name: published
                    source: { topic: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId, onMissing: deadLetter }
                """);
    }

    @Test
    void anUnknownOnMissingValueIsRejected() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId, onMissing: retry }
                """, "inbound [userAssignments] map [tenant] lookup onMissing [retry] must be drop or deadLetter");
    }

    /**
     * A webhook answers a miss with 400 - its poster already knows, and there is no message to keep.
     */
    @Test
    void onMissingOnAWebhookIsRejected() {
        assertFails("""
                inbound:
                  - name: assignmentHook
                    path: /assignments
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId, onMissing: deadLetter }
                """, "inbound [assignmentHook] map [tenant] lookup declares onMissing on a webhook source");
    }

    @Test
    void onMissingOnAFileSourceIsRejected() {
        assertFails("""
                inbound:
                  - name: assignmentDrop
                    source: { folder: target/inbox, cron: "0/5 * * * * ?" }
                    create: TenantUserAssignment
                    map:
                      tenant: { lookup: Tenant, by: tenantId, from: tenantId, onMissing: drop }
                """, "inbound [assignmentDrop] map [tenant] lookup declares onMissing on a file source");
    }

    @Test
    void aMapKeyThatIsNotAPropertyFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      emial: email
                """, "map [emial] is not a field or a to-one relation of [TenantUserAssignment]");
    }

    /** A generated key filled from the envelope is a record that cannot be inserted. */
    @Test
    void mappingThePrimaryKeyFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      id: externalId
                """, "fills the primary key, which is generated on insert");
    }

    @Test
    void aNonScalarAcceptValueFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    accept: { type: [a, b] }
                    create: TenantUserAssignment
                """, "accept [type] must be a scalar");
    }

    @Test
    void anEmptyAcceptValueFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    accept: { type: }
                    create: TenantUserAssignment
                """, "accept [type] has no value to gate on");
    }

    /**
     * Gson omits a null value, so a valueless key would leave an EMPTY gate - every message accepted -
     * or a field nobody fills, with the author's key gone from the model entirely. Both are caught on
     * the raw tree, while the key still exists.
     */
    @Test
    void aValuelessMapKeyFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    map:
                      email:
                """, "map [email] has no value - name the envelope key it is filled from");
    }

    @Test
    void anUnknownKeyOnAnArrivalFailsTheParse() {
        assertFails("""
                inbound:
                  - name: userAssignments
                    source: { queue: assignments }
                    create: TenantUserAssignment
                    accepts: { type: x }
                """, "unknown key [accepts]");
    }

    /**
     * An order with its lines and tags - the neutral model the collection cases use. The lines and tags
     * are composition children of the order; the notes are related to it, but not owned by it.
     */
    private static final String ORDERS = """
            name: shop
            entities:
              - name: Product
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: sku,  type: string, unique: true }
                  - { name: name, type: string }
              - name: PurchaseOrder
                fields:
                  - { name: id,          type: integer, primaryKey: true, generated: true }
                  - { name: orderNumber, type: string, unique: true }
                relations:
                  - { name: lines, kind: oneToMany, to: OrderLine }
                  - { name: tags,  kind: oneToMany, to: OrderTag }
                  - { name: notes, kind: oneToMany, to: OrderNote }
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
              - name: OrderNote
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: text, type: string }
                relations:
                  - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder }
            """;

    private static final String ORDER_ARRIVAL = """
            inbound:
              - name: orders
                source: { queue: orders }
                create: PurchaseOrder
                map:
                  orderNumber: orderNumber
            """;

    /** Both element forms, a bound and a per-element lookup parse into the typed map untouched. */
    @Test
    void aCollectionMapsAnArrayOntoCompositionChildRows() {
        IntentModel model = IntentParser.parse(ORDERS + ORDER_ARRIVAL + """
                      lines:
                        from: lines
                        max: 5
                        map:
                          quantity: qty
                          product:  { lookup: Product, by: sku, from: sku }
                      tags:
                        from: tags
                        map: { label: "." }
                """);

        InboundIntent arrival = model.getInbound()
                                     .get(0);
        assertTrue(arrival.getMap()
                          .get("lines") instanceof java.util.Map,
                "a collection value stays a map the generator reads");
        assertEquals(3, arrival.getMap()
                               .size());
    }

    @Test
    void aValueThatIsBothALookupAndACollectionFailsTheParse() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { lookup: Product, from: lines, map: { quantity: qty } }
                """, "map [lines] declares both lookup and map - a value is either a lookup or a collection");
    }

    @Test
    void aOneToManyFilledByAnEnvelopeKeyFailsTheParse() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: lines
                """, "map [lines] is a one-to-many relation of [PurchaseOrder] - it is filled by a collection { from, map }");
    }

    @Test
    void aCollectionOnAFieldFailsTheParse() {
        assertFailsOn(ORDERS, """
                inbound:
                  - name: orders
                    source: { queue: orders }
                    create: PurchaseOrder
                    map:
                      orderNumber: { from: numbers, map: { quantity: qty } }
                """, "map [orderNumber] is not a one-to-many relation of [PurchaseOrder]");
    }

    @Test
    void anUnknownMapKeyNamesTheOneToManyAlternativeWhereItApplies() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      colour: colour
                """, "map [colour] is not a field, a to-one or a one-to-many relation of [PurchaseOrder]");
    }

    @Test
    void aCollectionOfRowsTheArrivalDoesNotOwnFailsTheParse() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      notes: { from: notes, map: { text: "." } }
                """,
                "map [notes] fills [OrderNote], which is not a composition child of [PurchaseOrder]; an arrival creates only rows it owns");
    }

    @Test
    void aCollectionOfAnotherModelsRowsFailsTheParse() {
        String remote = ORDERS.replace("      - { name: notes, kind: oneToMany, to: OrderNote }",
                "      - { name: notes, kind: oneToMany, to: OrderNote }\n      - { name: remoteLines, kind: oneToMany, to: RemoteLine, model: warehouse }");
        assertFailsOn(remote, ORDER_ARRIVAL + """
                      remoteLines: { from: lines, map: { quantity: qty } }
                """, "map [remoteLines] fills [RemoteLine], which must be an entity declared in this model");
    }

    @Test
    void aCollectionsKeysAreAClosedSet() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, maximum: 5, map: { quantity: qty } }
                """, "map [lines] declares unknown key [maximum] - a collection names from, map and max");
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { map: { quantity: qty } }
                """, "map [lines] has no from - the envelope key holding the array");
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: ".", map: { quantity: qty } }
                """, "map [lines] reads its array from [.]");
    }

    @Test
    void aCollectionsMaxIsAWholeNumberInRange() {
        for (String max : new String[] {"0", "2.5", "20000", "many"}) {
            assertFailsOn(ORDERS, ORDER_ARRIVAL + "      lines: { from: lines, max: " + max + ", map: { quantity: qty } }\n",
                    "must be a whole number from 1 to 10000");
        }
    }

    @Test
    void anElementMapFillsOnlyTheChildsOwnPropertiesTheArrivalMayWrite() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { colour: colour } }
                """, "map [lines] element [colour] is not a field or a to-one relation of [OrderLine]");
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { id: lineId } }
                """, "map [lines] element [id] fills the primary key of [OrderLine], which is generated on insert");
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { purchaseOrder: orderId } }
                """,
                "map [lines] element [purchaseOrder] fills the composition back-reference, which is filled from the saved [PurchaseOrder]");
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { product: { from: products, map: { sku: sku } } } }
                """, "map [lines] element [product] is a nested collection - nested collections are not supported in this revision");
    }

    @Test
    void anElementIsEitherAValueOrAnObject() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { quantity: ".", product: { lookup: Product, by: sku, from: sku } } }
                """, "map [lines] mixes [.] with named element keys - an element is either a value or an object");
    }

    /**
     * Every element is resolved before anything is written, so failing on one leaves nothing behind.
     */
    @Test
    void onMissingInACollectionElementLookupIsAccepted() {
        IntentParser.parse(ORDERS + ORDER_ARRIVAL + """
                      lines:
                        from: lines
                        map:
                          quantity: qty
                          product:  { lookup: Product, by: sku, from: sku, onMissing: deadLetter }
                """);
    }

    @Test
    void anElementLookupsOnMissingFollowsTheSameRules() {
        assertFailsOn(ORDERS, """
                inbound:
                  - name: orderHook
                    path: /orders
                    create: PurchaseOrder
                    map:
                      lines: { from: lines, map: { product: { lookup: Product, by: sku, from: sku, onMissing: later } } }
                """, "map [lines.product] lookup onMissing [later] must be drop or deadLetter");
        assertFailsOn(ORDERS, """
                inbound:
                  - name: orderHook
                    path: /orders
                    create: PurchaseOrder
                    map:
                      lines: { from: lines, map: { product: { lookup: Product, by: sku, from: sku, onMissing: deadLetter } } }
                """, "map [lines.product] lookup declares onMissing on a webhook source");
    }

    @Test
    void anElementLookupFollowsEveryLookupRule() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines: { from: lines, map: { product: { lookup: Product, by: name, from: productName } } }
                """, "map [lines.product] lookup matches on [name], which is not unique on [Product]");
    }

    /** One level down, Gson drops a valueless element key exactly as it drops a top-level one. */
    @Test
    void aValuelessElementKeyFailsTheParse() {
        assertFailsOn(ORDERS, ORDER_ARRIVAL + """
                      lines:
                        from: lines
                        map:
                          quantity:
                """, "map [lines] element [quantity] has no value");
    }

    @Test
    void aChildFilledByTwoCollectionsFailsTheParse() {
        String twoRoutesToLines = ORDERS.replace("      - { name: tags,  kind: oneToMany, to: OrderTag }",
                "      - { name: tags,  kind: oneToMany, to: OrderTag }\n      - { name: moreLines, kind: oneToMany, to: OrderLine }");
        assertFailsOn(twoRoutesToLines, ORDER_ARRIVAL + """
                      lines:     { from: lines, map: { quantity: qty } }
                      moreLines: { from: more,  map: { quantity: qty } }
                """, "map [moreLines] fills [OrderLine] a second time - one collection per child relation");
    }

    /** The issue's subscriptions model: a usage report names its subscription by the pair it is for. */
    private static final String SUBSCRIPTIONS = """
            name: billing
            entities:
              - name: Customer
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: code,   type: string, unique: true }
                  - { name: name,   type: string }
                  - { name: region, type: string }
              - name: Plan
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string, unique: true }
              - name: Subscription
                unique:
                  - fields: [customer, plan]
                    message: "The customer already subscribes to this plan"
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: region, type: string }
                  - { name: since,  type: date }
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
            """;

    private static final String USAGE_ARRIVAL = """
            inbound:
              - name: usage
                source: { queue: "usage.reported" }
                accept: { type: usage.reported, version: 1 }
                create: UsageReport
                map:
                  reportId: reportId
                  units:    units
            """;

    @Test
    void aCompositeLookupMatchingADeclaredUniqueIsAccepted() {
        IntentModel model = IntentParser.parse(SUBSCRIPTIONS + USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code], from: [customerCode, planCode] }
                """);

        assertTrue(model.getInbound()
                        .get(0)
                        .getMap()
                        .get("subscription") instanceof java.util.Map,
                "a composite lookup stays a map the generator reads");
        // The declared order of the parts does not have to be the unique entry's own.
        IntentParser.parse(SUBSCRIPTIONS + USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [plan.code, customer.code], from: [planCode, customerCode] }
                """);
    }

    @Test
    void aCompositeLookupWithoutAMatchingUniqueIsRejected() {
        String undeclared = SUBSCRIPTIONS.replace("""
                    unique:
                      - fields: [customer, plan]
                        message: "The customer already subscribes to this plan"
                """, "");
        assertFailsOn(undeclared, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code], from: [customerCode, planCode] }
                """, "map [subscription] lookup matches on [customer.code, plan.code], which is not a declared unique key of [Subscription]"
                + " - declare unique: [{ fields: [customer, plan] }] on it, since a lookup that could match several rows would silently"
                + " pick one; its declared keys: none");
        // Only one of the key's parts: the customer alone may subscribe to several plans.
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, region], from: [customerCode, region] }
                """, "which is not a declared unique key of [Subscription] - declare unique: [{ fields: [customer, region] }] on it,"
                + " since a lookup that could match several rows would silently pick one; its declared keys: [customer, plan]");
        // A third part is not the declared key either - it would be a key nothing enforces.
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code, region], from: [customerCode, planCode, region] }
                """, "which is not a declared unique key of [Subscription]");
    }

    @Test
    void aHopToANonUniqueFieldIsRejected() {
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.name, plan.code], from: [customerName, planCode] }
                """, "map [subscription] lookup part [customer.name] matches on [name], which is not unique on [Customer]");
    }

    @Test
    void unequalByAndFromAreRejected() {
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code], from: [customerCode] }
                """, "map [subscription] lookup pairs by and from by position - both must be lists of the same length");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code], from: customerCode }
                """, "map [subscription] lookup pairs by and from by position - both must be lists of the same length");
    }

    @Test
    void aCompositeLookupsPartsAreHeldToTheSingleKeyRules() {
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, since], from: [customerCode, since] }
                """, "map [subscription] lookup matches on the [date] field [since] - a business key is a string or an integer field");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, owner.code], from: [customerCode, ownerCode] }
                """, "map [subscription] lookup matches on [owner.code], but [owner] is not a to-one relation of [Subscription]");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.owner.code], from: [customerCode, ownerCode] }
                """, "map [subscription] lookup matches on [plan.owner.code], which hops more than one relation");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, customer.id], from: [customerCode, customerId] }
                """, "map [subscription] lookup names [customer] twice");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, plan.code], from: [customerCode, "."] }
                """, "map [subscription] lookup from [.] must name a key");
        assertFailsOn(SUBSCRIPTIONS, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code], from: [customerCode] }
                """, "map [subscription] lookup names a composite key of one part");
    }

    @Test
    void aCrossModelHopIsRejected() {
        String remote = SUBSCRIPTIONS.replace("      - { name: plan,     kind: manyToOne, to: Plan,     required: true }",
                "      - { name: plan,     kind: manyToOne, to: Plan,     required: true }\n"
                        + "      - { name: warehouse, kind: manyToOne, to: Warehouse, model: logistics }");
        assertFailsOn(remote, USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, warehouse.code], from: [customerCode, warehouseCode] }
                """,
                "map [subscription] lookup matches on [warehouse.code], which reads [Warehouse] - it must be an entity declared in this"
                        + " model (cross-model lookups are not supported yet)");
    }

    @Test
    void aCompositeLookupMayMixAFieldAndAHop() {
        // A customer holds one subscription per region: the key pairs a hop with a field of the target.
        String regional = SUBSCRIPTIONS.replace("      - fields: [customer, plan]",
                "      - fields: [customer, region]\n      - fields: [customer, plan]");
        IntentParser.parse(regional + USAGE_ARRIVAL + """
                      subscription: { lookup: Subscription, by: [customer.code, region], from: [customerCode, region] }
                """);
    }

    /** An order arriving on a queue - the arrival a {@code dedupe:} identifies (#6852). */
    private static final String DEDUPED_ORDERS = """
            name: sales
            entities:
              - name: Order
                fields:
                  - { name: id,        type: integer, primaryKey: true, generated: true }
                  - { name: orderId,   type: string, unique: true, required: true }
                  - { name: reference, type: string, required: true }
                  - { name: note,      type: string, unique: true }
                  - { name: code,      type: string, unique: true, required: true }
            inbound:
              - name: orders
                source: { queue: "global:shop.orders" }
                create: Order
                dedupe: %s
                map:
                  orderId:   orderId
                  reference: reference
                  note:      note
            """;

    @Test
    void aDedupeOnAUniqueRequiredMappedFieldParses() {
        IntentModel model = IntentParser.parse(DEDUPED_ORDERS.formatted("orderId"));

        assertEquals("orderId", model.getInbound()
                                     .get(0)
                                     .getDedupe());
    }

    @Test
    void aDedupeOnANonUniqueFieldIsRefused() {
        assertRefused(DEDUPED_ORDERS.formatted("reference"), "inbound [orders] dedupe [reference] must be unique: true");
    }

    /** PostgreSQL counts NULLs as distinct, so an optional identifier would dedupe nothing. */
    @Test
    void aDedupeOnAnOptionalFieldIsRefused() {
        assertRefused(DEDUPED_ORDERS.formatted("note"), "inbound [orders] dedupe [note] must be required: true");
    }

    /** The pre-read runs before the record is built, so it can only read a value the map names. */
    @Test
    void aDedupeOnAnUnmappedFieldIsRefused() {
        assertRefused(DEDUPED_ORDERS.formatted("code"), "inbound [orders] dedupe [code] is not a field the map fills");
        assertRefused(DEDUPED_ORDERS.formatted("customer"), "inbound [orders] dedupe [customer] is not a field the map fills");
    }

    private static void assertRefused(String model, String expected) {
        IntentValidationException failure = assertThrows(IntentValidationException.class, () -> IntentParser.parse(model));
        assertTrue(failure.getMessage()
                          .contains(expected),
                failure.getMessage());
    }

    private static void assertFailsOn(String model, String inbound, String expected) {
        IntentValidationException failure = assertThrows(IntentValidationException.class, () -> IntentParser.parse(model + inbound));
        assertTrue(failure.getMessage()
                          .contains(expected),
                failure.getMessage());
    }

    private static void assertFails(String inbound, String expected) {
        IntentValidationException failure = assertThrows(IntentValidationException.class, () -> IntentParser.parse(ENTITIES + inbound));
        assertTrue(failure.getMessage()
                          .contains(expected),
                failure.getMessage());
    }
}
