/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.imports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a delimited text file (CSV with any single-character separator) into records.
 *
 * <p>
 * Fields may be quoted with {@code "}; a quoted field may hold the separator and line breaks, and
 * {@code ""} inside it is one quote. Values are trimmed, and a line with nothing on it is skipped.
 * With a header, a record is keyed by the header's column names; without one, by the 1-based column
 * position ({@code "1"}, {@code "2"}, ...).
 *
 * <p>
 * The columns the import reads are checked against the file before any record is returned: a file
 * that lacks one is rejected by name, rather than importing a column of nulls.
 */
public final class DelimitedText {

    private static final char QUOTE = '"';

    private DelimitedText() {}

    /**
     * Read the records of a delimited file.
     *
     * @param text the file's text
     * @param separator the field separator
     * @param header whether the first line (after the skipped ones) names the columns
     * @param skipLines how many leading lines to ignore - a bank's preamble before the header
     * @param columns the columns the import reads: header names, or 1-based positions without a header
     * @return the records, in file order
     * @throws IllegalArgumentException when the file lacks a read column, a quote is left open, or a
     *         record is shorter than the columns it must provide
     */
    public static List<Map<String, String>> read(String text, char separator, boolean header, int skipLines, Collection<String> columns) {
        List<List<String>> rows = rows(text, separator, skipLines);
        List<String> names;
        if (header) {
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("the file has no header line");
            }
            names = rows.remove(0);
            for (String column : columns) {
                if (!names.contains(column)) {
                    throw new IllegalArgumentException("the file has no column [" + column + "] - its columns are " + names);
                }
            }
        } else {
            names = null;
        }
        List<Map<String, String>> records = new ArrayList<>(rows.size());
        for (int index = 0; index < rows.size(); index++) {
            records.add(record(rows.get(index), names, columns, index + 1));
        }
        return records;
    }

    private static Map<String, String> record(List<String> values, List<String> names, Collection<String> columns, int number) {
        Map<String, String> record = new LinkedHashMap<>();
        if (names == null) {
            for (String column : columns) {
                int position = position(column);
                if (position > values.size()) {
                    throw new IllegalArgumentException(
                            "record " + number + " has " + values.size() + " columns, so it has no column " + position);
                }
            }
            for (int i = 0; i < values.size(); i++) {
                record.put(Integer.toString(i + 1), values.get(i));
            }
            return record;
        }
        for (String column : columns) {
            if (names.indexOf(column) >= values.size()) {
                throw new IllegalArgumentException(
                        "record " + number + " has " + values.size() + " columns, so it has no value for [" + column + "]");
            }
        }
        // A repeated column name keeps its first occurrence - the one the header check found.
        for (int i = values.size() - 1; i >= 0; i--) {
            if (i < names.size()) {
                record.put(names.get(i), values.get(i));
            }
        }
        return record;
    }

    private static int position(String column) {
        try {
            int position = Integer.parseInt(column);
            if (position < 1) {
                throw new IllegalArgumentException("column [" + column + "] is not a 1-based position");
            }
            return position;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("column [" + column + "] is not a 1-based position - the file is read without a header", e);
        }
    }

    /** Split the text into rows of trimmed fields, honouring quotes; blank lines are dropped. */
    private static List<List<String>> rows(String text, char separator, int skipLines) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean fieldStarted = false;
        int line = 1;
        int quoteLine = 0;
        int skipped = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (quoted) {
                if (c == QUOTE) {
                    if (i + 1 < length && text.charAt(i + 1) == QUOTE) {
                        field.append(QUOTE);
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    if (c == '\n') {
                        line++;
                    }
                    field.append(c);
                }
                continue;
            }
            if (c == QUOTE && field.toString()
                                   .isBlank()) {
                quoted = true;
                fieldStarted = true;
                quoteLine = line;
                field.setLength(0);
            } else if (c == separator) {
                row.add(field.toString()
                             .trim());
                field.setLength(0);
                fieldStarted = true;
            } else if (c == '\r' || c == '\n') {
                if (c == '\r' && i + 1 < length && text.charAt(i + 1) == '\n') {
                    i++;
                }
                line++;
                if (skipped < skipLines) {
                    skipped++;
                } else {
                    endRow(rows, row, field, fieldStarted);
                }
                row = new ArrayList<>();
                field.setLength(0);
                fieldStarted = false;
            } else {
                field.append(c);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("a quote opened on line " + quoteLine + " is never closed");
        }
        if (skipped >= skipLines) {
            endRow(rows, row, field, fieldStarted);
        }
        return rows;
    }

    private static void endRow(List<List<String>> rows, List<String> row, StringBuilder field, boolean fieldStarted) {
        String last = field.toString()
                           .trim();
        if (row.isEmpty() && !fieldStarted && last.isEmpty()) {
            return; // a blank line
        }
        row.add(last);
        rows.add(row);
    }
}
