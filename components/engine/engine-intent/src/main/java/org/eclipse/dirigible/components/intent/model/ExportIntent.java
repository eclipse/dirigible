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
import java.util.List;

/**
 * A declared export: a file in an exacting format produced over a query - a statutory filing (a VAT
 * ledger, a payroll declaration), a bank bulk-transfer file, the feed of a third-party ledger.
 *
 * <p>
 * {@link #source} names the rows - an entity, or a report of the same intent - and {@link #records}
 * the layout every row is written in, between optional header and trailer records that carry
 * literals, parameters and totals. {@link #format} selects the writer: {@code fixedWidth}
 * (positional, padded), {@code delimited} (CSV and its relatives) or {@code xml}. Every run is
 * recorded on the {@link #ledger} entity - its parameters, the stored file, its checksum and its
 * status - and the export is generated as a download endpoint and as a bean any job or custom code
 * can call.
 */
public class ExportIntent {

    /**
     * The export name: the generated class, the permission resource ({@code <name>:read}) and the
     * ledger's {@code Export} value.
     */
    private String name;

    /** The rows written: a declared entity, or a declared (non-ledger) report. */
    private String source;

    /** {@code fixedWidth}, {@code delimited} or {@code xml}. */
    private String format;

    /**
     * The character encoding, {@code UTF-8} when omitted - e.g. {@code windows-1251} for a legacy
     * authority format.
     */
    private String encoding;

    /** {@code fixedWidth} / {@code delimited}: {@code crlf} (the default) or {@code lf}. */
    private String lineEnding;

    /** {@code delimited}: the field separator, {@code ,} when omitted. */
    private String delimiter;

    /**
     * {@code delimited}: the quote character, {@code "} when omitted; the empty string quotes nothing.
     */
    private String quote;

    /** {@code delimited}: {@code minimal} (the default - only what must be quoted) or {@code all}. */
    private String quoting;

    /** {@code xml}: the document element, the export name when omitted. */
    private String root;

    /**
     * The stored file's name: literals plus {@code {parameter}} tokens; {@code <name>.<txt|csv|xml>}
     * when omitted.
     */
    private String fileName;

    /** The entity every run is recorded on - declared with no fields, its columns are injected. */
    private String ledger;

    /** An entity source's row order: its fields, ascending; the primary key when omitted. */
    private List<String> orderBy = new ArrayList<>();

    /** The run's inputs - query parameters of the download, arguments of the bean call. */
    private List<ExportParameterIntent> parameters = new ArrayList<>();

    /**
     * The record layouts: {@code header} (once, first), {@code line} (once per row), {@code trailer}
     * (once, last).
     */
    private List<ExportRecordIntent> records = new ArrayList<>();

    /** The signed-submission step - reserved: declaring it is refused until the step ships. */
    private Object submit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getLineEnding() {
        return lineEnding;
    }

    public void setLineEnding(String lineEnding) {
        this.lineEnding = lineEnding;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getQuoting() {
        return quoting;
    }

    public void setQuoting(String quoting) {
        this.quoting = quoting;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLedger() {
        return ledger;
    }

    public void setLedger(String ledger) {
        this.ledger = ledger;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy == null ? new ArrayList<>() : orderBy;
    }

    public List<ExportParameterIntent> getParameters() {
        return parameters;
    }

    public void setParameters(List<ExportParameterIntent> parameters) {
        this.parameters = parameters == null ? new ArrayList<>() : parameters;
    }

    public List<ExportRecordIntent> getRecords() {
        return records;
    }

    public void setRecords(List<ExportRecordIntent> records) {
        this.records = records == null ? new ArrayList<>() : records;
    }

    public Object getSubmit() {
        return submit;
    }

    public void setSubmit(Object submit) {
        this.submit = submit;
    }
}
