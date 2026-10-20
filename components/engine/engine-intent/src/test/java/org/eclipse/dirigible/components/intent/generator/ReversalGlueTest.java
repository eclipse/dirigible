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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.model.GeneratesIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.components.intent.parser.IntentValidationException;
import org.junit.jupiter.api.Test;

/**
 * A {@code reversals:} entry (issue #7886) is parsed into the create-from of its entity onto
 * itself, and its glue entry carries the negated copies, the refusals over the original and the
 * back-link.
 */
class ReversalGlueTest {

    private static final String ENTITIES = """
            name: journal
            entities:
              - name: EntryStatus
                kind: setting
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: Entry
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: number, type: string, length: 100, number: { series: Entry } }
                  - { name: date, type: date, required: true }
                  - { name: note, type: string }
                  - { name: uuid, type: string, unique: true }
                  - { name: debitTotal, type: decimal, precision: 18, scale: 2, aggregate: true }
                relations:
                  - { name: Status, kind: manyToOne, to: EntryStatus, function: EntityStatus, init: 1 }
                  - { name: Storno, kind: manyToOne, to: Entry }
                  - { name: ReversedBy, kind: manyToOne, to: Entry }
              - name: EntryItem
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: debit, type: decimal, precision: 18, scale: 2 }
                  - { name: credit, type: decimal, precision: 18, scale: 2 }
                  - { name: rate, type: decimal, precision: 18, scale: 6 }
                  - { name: note, type: string }
                relations:
                  - { name: Entry, kind: manyToOne, to: Entry, composition: true, required: true }
            seeds:
              - name: entry-statuses
                entity: EntryStatus
                rows:
                  - { id: 1, name: DRAFT }
                  - { id: 2, name: POSTED }
            """;

    private static final String REVERSAL = """
            reversals:
              - name: reverseEntry
                forEntity: Entry
                when: "Status == POSTED"
                label: Reverse
                link: Storno
                backLink: ReversedBy
                negate: [debit, credit]
                defaults: { date: now }
                refuse:
                  - { when: "ReversedBy != null", message: "This entry is already reversed" }
                  - { when: "Storno != null", message: "A reversal cannot be reversed - reverse the original entry" }
            """;

    @Test
    void a_reversal_is_the_create_from_of_its_entity_onto_itself() {
        IntentModel model = IntentParser.parse(ENTITIES + REVERSAL);
        GeneratesIntent generate = model.getGenerates()
                                        .get(0);
        assertEquals("reverseEntry", generate.getName());
        assertEquals("Entry", generate.getFrom());
        assertEquals("Entry", generate.getTo());
        assertEquals("Entry", generate.getForEntity());
        assertTrue(generate.isReversal());
        // the status name resolved to its seed id and became the from-status guard
        assertEquals(List.of(2), generate.getFromStatus());
        // copied: the plain fields and to-ones; the link carries the original's key. Not copied: the
        // key, the number, the unique and aggregated fields, the status, the back-link, the defaulted date.
        assertEquals(Map.of("note", "note", "Storno", "id"), generate.getMap());
        assertEquals(Map.of("date", "now"), generate.getDefaults());
        // the lines: every field but the key, never the composition FK (re-pointed at the twin)
        assertEquals("EntryItem", generate.getItems()
                                          .getFrom());
        assertEquals("EntryItem", generate.getItems()
                                          .getTo());
        assertEquals(Map.of("debit", "debit", "credit", "credit", "rate", "rate", "note", "note"), generate.getItems()
                                                                                                           .getMap());
    }

    @Test
    void the_glue_negates_the_declared_amounts_and_carries_the_refusals_and_the_back_link() {
        Map<String, Object> entry = GlueIntentGenerator.buildGeneratesForTest(IntentParser.parse(ENTITIES + REVERSAL))
                                                       .get(0);
        Map<String, Map<String, Object>> lines = byTarget(entry.get("itemFieldAssignments"));
        assertEquals(Map.of("kind", "negate", "owner", "srcItem", "property", "Debit"), lines.get("Debit")
                                                                                             .get("reading"));
        assertEquals(Map.of("kind", "negate", "owner", "srcItem", "property", "Credit"), lines.get("Credit")
                                                                                              .get("reading"));
        // the rate multiplies the amount into the base currency - copied as it stands, never negated
        assertEquals(Map.of("kind", "read", "owner", "srcItem", "property", "Rate"), lines.get("Rate")
                                                                                          .get("reading"));
        assertEquals(Map.of("kind", "read", "owner", "source", "property", "Id"), byTarget(entry.get("fieldAssignments")).get("Storno")
                                                                                                                         .get("reading"));
        assertEquals(true, entry.get("hasBackLink"));
        assertEquals("ReversedBy", entry.get("backLinkProperty"));
        assertEquals(true, entry.get("hasRefusals"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> refusals = (List<Map<String, Object>>) entry.get("refusals");
        assertEquals(2, refusals.size());
        assertEquals("This entry is already reversed", refusals.get(0)
                                                               .get("message"));
        @SuppressWarnings("unchecked")
        Map<String, Object> term = ((List<Map<String, Object>>) refusals.get(0)
                                                                        .get("terms")).get(0);
        assertEquals("source", term.get("owner"));
        assertEquals("ReversedBy", term.get("property"));
        assertEquals(false, term.get("equal"));
        assertEquals("null", term.get("type"));
        assertEquals(true, entry.get("hasStatusGuard"));
    }

    @Test
    void an_authored_create_from_carries_no_reversal_half() {
        Map<String, Object> entry = GlueIntentGenerator.buildGeneratesForTest(IntentParser.parse(ENTITIES + """
                generates:
                  - name: copyEntry
                    from: Entry
                    to: Entry
                    map: { note: note }
                """))
                                                       .get(0);
        assertEquals(false, entry.get("hasRefusals"));
        assertEquals(false, entry.get("hasBackLink"));
        assertFalse(String.valueOf(entry.get("fieldAssignments"))
                          .contains("negate"));
    }

    @Test
    void a_reversal_with_a_bad_link_status_guard_negation_or_refusal_is_refused() {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> IntentParser.parse(ENTITIES + """
                reversals:
                  - name: reverseEntry
                    forEntity: Entry
                    when: "note == 'x'"
                    link: Status
                    negate: [note, missing]
                    refuse:
                      - { when: "nothing != null" }
                """));
        String message = refused.getMessage();
        assertTrue(message.contains("reversal [reverseEntry] link [Status] must be a manyToOne/oneToOne of [Entry] to itself"), message);
        assertTrue(message.contains("reversal [reverseEntry] when [note == 'x'] must compare the function: EntityStatus relation"),
                message);
        assertTrue(message.contains("negate names [note], a field of type [string]"), message);
        assertTrue(message.contains("negate names [missing], which is a field of neither [Entry] nor its lines [EntryItem]"), message);
        assertTrue(message.contains("refuse entry has no message"), message);
        assertTrue(message.contains("refuse when [nothing != null] does not read"), message);
    }

    @Test
    void a_reversal_needs_a_link_and_a_name_of_its_own() {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> IntentParser.parse(ENTITIES + """
                generates:
                  - name: reverseEntry
                    from: Entry
                    to: Entry
                reversals:
                  - name: reverseEntry
                    forEntity: Entry
                  - name: other
                    forEntity: Entry
                """));
        String message = refused.getMessage();
        assertTrue(message.contains("reversal [reverseEntry] has the name of another reversal or generates action"), message);
        assertTrue(message.contains("reversal [other] link is missing"), message);
    }

    @Test
    void a_reversal_key_is_not_a_generates_key() {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> IntentParser.parse(ENTITIES + """
                generates:
                  - name: copyEntry
                    from: Entry
                    to: Entry
                    reversal: { link: Storno }
                """));
        // the create-from's reversal half is internal: spelled on a generates, it is an unknown key, not
        // a silently dropped one
        assertTrue(refused.getMessage()
                          .contains("unknown key [reversal]"),
                refused.getMessage());
    }

    private static Map<String, Map<String, Object>> byTarget(Object assignments) {
        Map<String, Map<String, Object>> byTarget = new java.util.LinkedHashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) assignments;
        for (Map<String, Object> assignment : list) {
            byTarget.put(String.valueOf(assignment.get("targetProp")), assignment);
        }
        return byTarget;
    }
}
