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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dirigible.components.intent.generator.CheckSupport;
import org.eclipse.dirigible.components.intent.generator.IntentEntities;
import org.eclipse.dirigible.components.intent.generator.IntentNaming;
import org.eclipse.dirigible.components.intent.model.EntityIntent;
import org.eclipse.dirigible.components.intent.model.FieldIntent;
import org.eclipse.dirigible.components.intent.model.GeneratesIntent;
import org.eclipse.dirigible.components.intent.model.GeneratesItemsIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.RelationIntent;
import org.eclipse.dirigible.components.intent.model.ReversalIntent;
import org.eclipse.dirigible.components.intent.model.ReversalRefusalIntent;

/**
 * Expands every {@code reversals:} entry (issue #7886) into the create-from it is: a
 * {@link GeneratesIntent} whose source and target are the same entity, mapping every copyable
 * header field and line field onto itself, guarded by the reversal's status. The entry itself rides
 * along ({@link GeneratesIntent#getReversal()}), carrying what only a reversal does - the negation,
 * the refusals and the back-link - to the glue.
 *
 * <p>
 * What is NOT copied is what the create path owns, or what would make the twin a second copy of the
 * original rather than its reversal: the primary key, a {@code unique} field, a document number
 * (the twin draws its own from the series), a calculated or aggregated field (recomputed from what
 * is copied), the {@code function: EntityStatus} relation (the twin starts at the init status), a
 * {@code personal} owner (the twin belongs to whoever reverses), the link and the back-link, and a
 * line's composition foreign key (re-pointed at the twin). A {@code defaults:} entry replaces the
 * copy of its field.
 *
 * <p>
 * Runs after the entities are validated and before the create-froms are, so the expanded entry is
 * checked by the same validators as an authored one and every generator sees one ordinary
 * {@code generates}.
 */
final class ReversalExpander {

    /** The from-status guard a reversal's {@code when:} is, once its status name resolved to an id. */
    private static final Pattern WHEN = Pattern.compile("\\s*(\\w+)\\s*==\\s*(\\d+)\\s*");

    private static final Set<String> TO_ONE = Set.of("manyToOne", "oneToOne");

    private ReversalExpander() {}

    /**
     * Append the create-from of every valid reversal to the model's {@code generates}.
     *
     * @param model the typed model, its entities already validated
     * @param issues the collecting issue list
     */
    static void expand(IntentModel model, List<String> issues) {
        if (model.getReversals()
                 .isEmpty()) {
            return;
        }
        Map<String, EntityIntent> byName = IntentEntities.byName(model);
        Set<String> names = new LinkedHashSet<>();
        for (GeneratesIntent authored : model.getGenerates()) {
            names.add(authored.getName());
        }
        for (ReversalIntent reversal : model.getReversals()) {
            String name = reversal.getName();
            if (isBlank(name)) {
                issues.add("reversal has no name");
                continue;
            }
            String subject = "reversal [" + name + "]";
            if (!names.add(name)) {
                issues.add(subject + " has the name of another reversal or generates action - the names share one action namespace");
                continue;
            }
            EntityIntent entity = isBlank(reversal.getForEntity()) ? null : byName.get(reversal.getForEntity());
            if (entity == null) {
                issues.add(isBlank(reversal.getForEntity()) ? subject + " has no forEntity"
                        : subject + " forEntity references unknown entity [" + reversal.getForEntity() + "]");
                continue;
            }
            int before = issues.size();
            RelationIntent link = selfRelation(entity, reversal.getLink(), subject + " link", issues);
            RelationIntent backLink =
                    isBlank(reversal.getBackLink()) ? null : selfRelation(entity, reversal.getBackLink(), subject + " backLink", issues);
            if (link != null && backLink != null && link == backLink) {
                issues.add(subject + " link and backLink both name [" + link.getName()
                        + "] - the reversal points at the original through one relation and the original at the reversal through another");
            }
            List<Integer> fromStatus = fromStatus(entity, reversal.getWhen(), subject, issues);
            EntityIntent items = IntentEntities.documentItemsChild(entity.getName(), model.getEntities());
            validateNegate(reversal, entity, items, subject, issues);
            validateDefaults(reversal, entity, subject, issues);
            validateRefusals(reversal, entity, byName, subject, issues);
            if (issues.size() > before) {
                continue;
            }
            model.getGenerates()
                 .add(createFrom(reversal, entity, items, link, backLink, fromStatus));
        }
    }

    /** The reversal as a create-from of its entity onto itself. */
    private static GeneratesIntent createFrom(ReversalIntent reversal, EntityIntent entity, EntityIntent items, RelationIntent link,
            RelationIntent backLink, List<Integer> fromStatus) {
        GeneratesIntent generate = new GeneratesIntent();
        generate.setName(reversal.getName());
        generate.setFrom(entity.getName());
        generate.setTo(entity.getName());
        generate.setForEntity(entity.getName());
        generate.setLabel(reversal.getLabel());
        generate.setIcon(reversal.getIcon());
        generate.setOrder(reversal.getOrder());
        generate.setFromStatus(fromStatus);
        Set<String> skipped = new LinkedHashSet<>();
        skipped.add(IntentNaming.pascalCase(link.getName()));
        if (backLink != null) {
            skipped.add(IntentNaming.pascalCase(backLink.getName()));
        }
        for (String defaulted : reversal.getDefaults()
                                        .keySet()) {
            skipped.add(IntentNaming.pascalCase(defaulted));
        }
        Map<String, String> map = copyMap(entity, skipped, null);
        // The link IS a copy: the twin's to-one to the original carries the original's key.
        map.put(link.getName(), primaryKeyName(entity));
        generate.setMap(map);
        generate.setDefaults(new LinkedHashMap<>(reversal.getDefaults()));
        if (items != null) {
            GeneratesItemsIntent lines = new GeneratesItemsIntent();
            lines.setFrom(items.getName());
            lines.setTo(items.getName());
            lines.setMap(copyMap(items, Set.of(), entity.getName()));
            generate.setItems(lines);
        }
        generate.setReversal(reversal);
        return generate;
    }

    /**
     * Every copyable property of the entity mapped onto itself, by its authored name.
     *
     * @param entity the header or the line entity
     * @param skipped PascalCased properties left out on top of the structural rules
     * @param master the line's composition master, whose foreign key is re-pointed rather than copied;
     *        {@code null} for the header
     */
    private static Map<String, String> copyMap(EntityIntent entity, Set<String> skipped, String master) {
        Map<String, String> map = new LinkedHashMap<>();
        for (FieldIntent field : nonNull(entity.getFields())) {
            if (field.getName() == null || skipped.contains(IntentNaming.pascalCase(field.getName())) || !copyable(field)) {
                continue;
            }
            map.put(field.getName(), field.getName());
        }
        for (RelationIntent relation : nonNull(entity.getRelations())) {
            if (relation.getName() == null || !TO_ONE.contains(relation.getKind()) || relation.isEntityStatus() || relation.isPersonal()
                    || skipped.contains(IntentNaming.pascalCase(relation.getName()))
                    || (master != null && relation.isComposition() && master.equals(relation.getTo()))) {
                continue;
            }
            map.put(relation.getName(), relation.getName());
        }
        return map;
    }

    /** Whether a field's value belongs on the twin as it stands on the original. */
    private static boolean copyable(FieldIntent field) {
        return !field.isPrimaryKey() && !field.isUnique() && field.getNumber() == null && !field.isCalculated() && !field.isAggregate();
    }

    /** The to-one of the entity to itself named {@code name}, or {@code null} with an issue. */
    private static RelationIntent selfRelation(EntityIntent entity, String name, String subject, List<String> issues) {
        if (isBlank(name)) {
            issues.add(subject + " is missing - name the to-one of [" + entity.getName() + "] to itself that links the two records");
            return null;
        }
        for (RelationIntent relation : nonNull(entity.getRelations())) {
            if (name.equals(relation.getName())) {
                if (!TO_ONE.contains(relation.getKind()) || !entity.getName()
                                                                   .equals(relation.getTo())
                        || relation.isCrossModel()) {
                    issues.add(subject + " [" + name + "] must be a manyToOne/oneToOne of [" + entity.getName() + "] to itself");
                    return null;
                }
                return relation;
            }
        }
        issues.add(subject + " references unknown relation [" + name + "] of [" + entity.getName() + "]");
        return null;
    }

    /** The from-status guard {@code when:} declares, or {@code null} when it declares none. */
    private static List<Integer> fromStatus(EntityIntent entity, String when, String subject, List<String> issues) {
        if (isBlank(when)) {
            return null;
        }
        RelationIntent status = statusRelation(entity);
        Matcher matcher = WHEN.matcher(when);
        if (status == null || !matcher.matches() || !IntentNaming.pascalCase(status.getName())
                                                                 .equals(IntentNaming.pascalCase(matcher.group(1)))) {
            issues.add(subject + " when [" + when + "] must compare the function: EntityStatus relation of [" + entity.getName()
                    + "] with one status (\"<Status> == POSTED\") - a condition on anything else belongs under refuse:");
            return null;
        }
        return new ArrayList<>(List.of(Integer.valueOf(matcher.group(2))));
    }

    private static RelationIntent statusRelation(EntityIntent entity) {
        for (RelationIntent relation : nonNull(entity.getRelations())) {
            if (relation.isEntityStatus()) {
                return relation;
            }
        }
        return null;
    }

    /**
     * Each negated name must be a decimal field of the header or of its lines - negating anything else
     * has no meaning.
     */
    private static void validateNegate(ReversalIntent reversal, EntityIntent entity, EntityIntent items, String subject,
            List<String> issues) {
        for (String name : reversal.getNegate()) {
            FieldIntent header = CheckSupport.field(entity, name);
            FieldIntent line = items == null ? null : CheckSupport.field(items, name);
            if (header == null && line == null) {
                issues.add(subject + " negate names [" + name + "], which is a field of neither [" + entity.getName() + "]"
                        + (items == null ? "" : " nor its lines [" + items.getName() + "]"));
                continue;
            }
            for (FieldIntent field : new FieldIntent[] {header, line}) {
                if (field != null && !"decimal".equalsIgnoreCase(field.getType())) {
                    issues.add(subject + " negate names [" + name + "], a field of type [" + field.getType()
                            + "] - only a decimal amount is negated");
                } else if (field != null && !copyable(field)) {
                    issues.add(subject + " negate names [" + name + "], which the reversal does not copy (a key, unique, numbered,"
                            + " calculated or aggregated field is derived on the twin, never negated)");
                }
            }
        }
    }

    private static void validateDefaults(ReversalIntent reversal, EntityIntent entity, String subject, List<String> issues) {
        for (String name : reversal.getDefaults()
                                   .keySet()) {
            if (CheckSupport.field(entity, name) == null && CheckSupport.toOne(entity, name) == null) {
                issues.add(subject + " defaults names [" + name + "], which is not a field or to-one of [" + entity.getName() + "]");
            } else if (reversal.getNegate()
                               .stream()
                               .anyMatch(name::equalsIgnoreCase)) {
                issues.add(subject + " both negates and defaults [" + name + "] - a field has one writer");
            }
        }
    }

    private static void validateRefusals(ReversalIntent reversal, EntityIntent entity, Map<String, EntityIntent> byName, String subject,
            List<String> issues) {
        for (ReversalRefusalIntent refusal : reversal.getRefuse()) {
            if (refusal == null || isBlank(refusal.getMessage())) {
                issues.add(subject + " refuse entry has no message - the message is what the refused caller is told");
            }
            if (refusal == null || CheckSupport.conditionTerms(entity, byName, refusal.getWhen()) == null) {
                issues.add(subject + " refuse when [" + (refusal == null ? null : refusal.getWhen()) + "] does not read: each term is"
                        + " \"<field or to-one of " + entity.getName() + "> ==|!= <value | null>\"");
            }
        }
    }

    private static String primaryKeyName(EntityIntent entity) {
        for (FieldIntent field : nonNull(entity.getFields())) {
            if (field.isPrimaryKey()) {
                return field.getName();
            }
        }
        return "id";
    }

    private static <T> List<T> nonNull(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
