/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A manual Reverse action on a booked document (issue #7886): the red storno an accountant writes
 * to correct a journal entry that has no source event to void. One click creates the document's
 * NEGATED TWIN - every line copied onto the same side with the declared amounts negated - and links
 * the two both ways, so the pair nets to zero and each record names the other.
 *
 * <pre>
 * reversals:
 *   - name: reverseEntry
 *     forEntity: JournalEntry
 *     when: "Status == POSTED"
 *     link: Storno
 *     backLink: ReversedBy
 *     negate: [debit, credit, currencyAmount]
 *     defaults: { date: now }
 *     refuse:
 *       - { when: "ReversedBy != null", message: "This entry is already reversed" }
 * </pre>
 *
 * <p>
 * It is not a second machinery: the parser expands it into the create-from it is
 * ({@code ReversalExpander}) - source and target the same entity, every copyable header field and
 * line field mapped onto itself, {@link #when} as the from-status guard - so the twin is saved
 * through the generated repositories like any create (checks, numbering, the init status, the
 * period lock and the entity's process all apply), in one unit of work with its lines and the
 * {@link #backLink} write on the original.
 */
public class ReversalIntent {

    /**
     * Unique name within the model; drives the action id, the contribution files and the controller.
     */
    private String name;

    /** The document reversed - a local entity; the button renders on its view. */
    private String forEntity;

    /**
     * Optional status guard {@code <status relation> == <status>} (a seeded name or its id): the action
     * is offered, and accepted, only on a record standing in it.
     */
    private String when;

    /** Button label; defaults to a humanized {@link #name}. */
    private String label;

    /** Optional Lucide icon name carried onto the contribution descriptor. */
    private String icon;

    /** Optional ordering hint among the contributed actions of a view. */
    private Integer order;

    /** The to-one of {@link #forEntity} to itself the reversal points at the original through. */
    private String link;

    /**
     * Optional to-one of {@link #forEntity} to itself the ORIGINAL points at its reversal through,
     * written in the same unit of work as a targeted system write - past {@code immutableWhen}, which
     * guards user writes.
     */
    private String backLink;

    /**
     * The decimal fields negated on the twin - of the header, of its line items, or both; every other
     * copyable value is copied as it stands, so a line keeps its side (red storno).
     */
    private List<String> negate = new ArrayList<>();

    /** Header values set on the twin instead of copied: {@code now} or a literal. */
    private Map<String, String> defaults = new LinkedHashMap<>();

    /** Conditions over the original that refuse the reversal, each with the message the caller gets. */
    private List<ReversalRefusalIntent> refuse = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForEntity() {
        return forEntity;
    }

    public void setForEntity(String forEntity) {
        this.forEntity = forEntity;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBackLink() {
        return backLink;
    }

    public void setBackLink(String backLink) {
        this.backLink = backLink;
    }

    public List<String> getNegate() {
        return negate;
    }

    public void setNegate(List<String> negate) {
        this.negate = negate == null ? new ArrayList<>() : negate;
    }

    public Map<String, String> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, String> defaults) {
        this.defaults = defaults == null ? new LinkedHashMap<>() : defaults;
    }

    public List<ReversalRefusalIntent> getRefuse() {
        return refuse;
    }

    public void setRefuse(List<ReversalRefusalIntent> refuse) {
        this.refuse = refuse == null ? new ArrayList<>() : refuse;
    }
}
