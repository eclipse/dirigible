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

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * One field of an {@link ExportRecordIntent}: where its value comes from - exactly one of
 * {@link #from} (a source property, or a total {@code sum(property)} / {@code count()}),
 * {@link #literal} or {@link #param} - and how it is written.
 */
public class ExportFieldIntent {

    /**
     * {@code xml}: the element or attribute name; derived from {@link #from} / {@link #param} when
     * omitted.
     */
    private String name;

    /**
     * A field of an entity source or a one-hop {@code relation.field}; a dimension or measure of a
     * report source; or {@code sum(property)} / {@code count()} over the rows.
     */
    private String from;

    /** A constant text. */
    private String literal;

    /** A declared parameter of the export. */
    private String param;

    /** {@code fixedWidth}: the width in characters - required there, refused elsewhere. */
    private Integer width;

    /** {@code fixedWidth}: {@code left} (the default) or {@code right}. */
    private String align;

    /** {@code fixedWidth}: the one padding character, a space when omitted. */
    private String pad;

    /** {@code fixedWidth}: cut a wider value to the width instead of refusing the run. */
    private Boolean truncate;

    /** Write the value as a decimal rounded half-up to this many places. */
    private Integer scale;

    /** With {@link #scale}: the separator, {@code .} when omitted; empty writes an implied point. */
    private String decimalSeparator;

    /** Write the value as a date/time in this {@code DateTimeFormatter} pattern. */
    private String pattern;

    /** {@code xml}: write the field as an attribute of its record element. */
    private Boolean attribute;

    /** Text normalisation (#7925): {@code upper} or {@code lower}. */
    @SerializedName("case")
    private String textCase;

    /**
     * The characters the field accepts, as a regular-expression character class without brackets; any
     * other is handled by {@link #onDisallowed}.
     */
    private String allowed;

    /** Text replaced first, in the declared order: each key by its value. */
    private Map<String, String> replace;

    /**
     * What a character outside {@link #allowed} becomes: {@code strip}, {@code space} or {@code fail}
     * (the default).
     */
    private String onDisallowed;

    /** Write every run of whitespace as one space, and none at either end. */
    private Boolean collapseSpaces;

    /** {@code fail}: refuse the run when the normalisation changed a letter or a digit. */
    private String onLoss;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public Boolean getTruncate() {
        return truncate;
    }

    public void setTruncate(Boolean truncate) {
        this.truncate = truncate;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getAttribute() {
        return attribute;
    }

    public void setAttribute(Boolean attribute) {
        this.attribute = attribute;
    }

    public String getTextCase() {
        return textCase;
    }

    public void setTextCase(String textCase) {
        this.textCase = textCase;
    }

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    public Map<String, String> getReplace() {
        return replace;
    }

    public void setReplace(Map<String, String> replace) {
        this.replace = replace;
    }

    public String getOnDisallowed() {
        return onDisallowed;
    }

    public void setOnDisallowed(String onDisallowed) {
        this.onDisallowed = onDisallowed;
    }

    public Boolean getCollapseSpaces() {
        return collapseSpaces;
    }

    public void setCollapseSpaces(Boolean collapseSpaces) {
        this.collapseSpaces = collapseSpaces;
    }

    public String getOnLoss() {
        return onLoss;
    }

    public void setOnLoss(String onLoss) {
        this.onLoss = onLoss;
    }
}
