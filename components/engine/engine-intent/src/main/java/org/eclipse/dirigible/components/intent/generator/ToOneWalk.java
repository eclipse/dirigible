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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.model.EntityIntent;
import org.eclipse.dirigible.components.intent.model.RelationIntent;

/**
 * A walk through a chain of <b>to-one relations</b>: each segment names a {@code manyToOne} /
 * {@code oneToOne} relation - the first of the entity the walk starts at, each further one of the
 * previous segment's target. A cross-model relation may only be the LAST segment: its target is a
 * projection that carries the owner's properties but not its relations, so there is nothing to walk
 * on from there.
 *
 * <p>
 * Shared by every path that walks more than one hop - a user task's assignee path
 * ({@link ProcessAssigneeSupport}) and a notify path ({@link NotificationSupport}) - so the parser
 * and the generators agree on what a walk is. It reports WHY a walk stops as a {@link Failure} and
 * leaves the wording to the caller, whose message names its own call site.
 */
public final class ToOneWalk {

    /** Why a walk did not resolve. */
    public enum Failure {
        /** A segment is empty ({@code a..b}). */
        EMPTY_SEGMENT,
        /** A segment is not a to-one relation of the entity it is looked up on. */
        NOT_TO_ONE,
        /** A same-model relation targets an entity the model does not declare. */
        UNKNOWN_TARGET,
        /** A segment follows a cross-model relation. */
        PAST_CROSS_MODEL
    }

    /**
     * One traversed relation.
     *
     * @param segment the authored segment (the relation's name)
     * @param relation the relation
     * @param target the entity reached, or {@code null} when the relation is cross-model
     */
    public record Link(String segment, RelationIntent relation, EntityIntent target) {

        /**
         * @return whether the relation points at an entity owned by another model
         */
        public boolean crossModel() {
            return isCrossModel(relation);
        }
    }

    /**
     * A walk, resolved or stopped at its first failing segment.
     *
     * @param links the relations traversed, in order (up to the failing segment when it failed)
     * @param failure why the walk stopped, or {@code null} when it resolved
     * @param segment the failing segment, or {@code null} when it resolved
     * @param at the entity the failing segment was looked up on, or {@code null} past a cross-model
     *        relation or when it resolved
     * @param relation the relation whose target is unknown ({@link Failure#UNKNOWN_TARGET} only), else
     *        {@code null}
     */
    public record Result(List<Link> links, Failure failure, String segment, EntityIntent at, RelationIntent relation) {

        /**
         * @return whether every segment resolved
         */
        public boolean resolved() {
            return failure == null;
        }
    }

    private ToOneWalk() {}

    /**
     * Walks the segments off {@code start}, stopping at the first one that does not resolve.
     *
     * @param start the entity the first segment is a relation of
     * @param segments the relation segments, in order
     * @param byName all LOCAL entities by name (a same-model relation's target is looked up here)
     * @return the walk
     */
    public static Result walk(EntityIntent start, List<String> segments, Map<String, EntityIntent> byName) {
        List<Link> links = new ArrayList<>();
        EntityIntent current = start;
        for (String segment : segments) {
            if (segment.isBlank()) {
                return new Result(links, Failure.EMPTY_SEGMENT, segment, current, null);
            }
            if (current == null) {
                return new Result(links, Failure.PAST_CROSS_MODEL, segment, null, null);
            }
            RelationIntent relation = toOneRelation(current, segment);
            if (relation == null) {
                return new Result(links, Failure.NOT_TO_ONE, segment, current, null);
            }
            EntityIntent target = isCrossModel(relation) ? null : byName.get(relation.getTo());
            if (!isCrossModel(relation) && target == null) {
                return new Result(links, Failure.UNKNOWN_TARGET, segment, current, relation);
            }
            links.add(new Link(segment, relation, target));
            current = target;
        }
        return new Result(links, null, null, null, null);
    }

    /**
     * The entity's {@code manyToOne} / {@code oneToOne} relation with that exact name.
     *
     * @param owner the entity
     * @param name the relation name
     * @return the relation, or {@code null}
     */
    public static RelationIntent toOneRelation(EntityIntent owner, String name) {
        for (RelationIntent relation : owner.getRelations()) {
            boolean toOne = "manyToOne".equals(relation.getKind()) || "oneToOne".equals(relation.getKind());
            if (toOne && name.equals(relation.getName())) {
                return relation;
            }
        }
        return null;
    }

    private static boolean isCrossModel(RelationIntent relation) {
        return relation.getModel() != null && !relation.getModel()
                                                       .isBlank();
    }
}
