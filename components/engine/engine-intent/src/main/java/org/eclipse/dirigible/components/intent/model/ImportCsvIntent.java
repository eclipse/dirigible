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

/**
 * The locale rules of a {@code format: csv} {@link ImportIntent}: how its fields are separated and
 * how its numbers and dates are written. Every key is optional; the defaults read a plain
 * comma-separated file with a header, {@code .} decimals and ISO dates.
 */
public class ImportCsvIntent {

    /** The field separator - default {@code ,}. */
    private String separator;
    /** The decimal separator - default {@code .}. */
    private String decimal;
    /** The grouping (thousands) separator - default none. */
    private String grouping;
    /** The pattern of a date value - default {@code yyyy-MM-dd}. */
    private String dateFormat;
    /** The pattern of a timestamp value, read at UTC - default the ISO shapes. */
    private String timestampFormat;
    /** Whether the first line names the columns - default true. */
    private Boolean header;
    /** How many leading lines to skip before the header - a bank's preamble; default 0. */
    private Integer skipLines;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public Boolean getHeader() {
        return header;
    }

    public void setHeader(Boolean header) {
        this.header = header;
    }

    public Integer getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(Integer skipLines) {
        this.skipLines = skipLines;
    }
}
