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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads a SWIFT MT940 customer statement into one record per statement line ({@code :61:}).
 *
 * <p>
 * Every record carries the keys of {@link #KEYS}, in the same vocabulary {@link Camt053} uses, so
 * an import maps the two formats identically. Values are canonical: an amount is a plain decimal
 * with a {@code .} and the sign of its debit/credit mark (a debit and a reversed credit are
 * negative), and a date is ISO {@code yyyy-MM-dd}. A key the file does not carry for a line is
 * absent from its record.
 *
 * <p>
 * A file may hold several statements (each opened by {@code :20:}), with or without the SWIFT block
 * envelope ({@code {1:...}{4:} ... -}}). {@code lineNumber} counts the lines of one statement from
 * 1, so a statement id plus a line number identifies a line across uploads.
 */
public final class Mt940 {

    /** Every key a record may carry. */
    public static final Set<String> KEYS = Set.of("statementId", "sequence", "account", "currency", "lineNumber", "valueDate",
            "bookingDate", "mark", "amount", "transactionType", "reference", "bankReference", "supplementary", "details");

    private static final Pattern TAG = Pattern.compile("^:(\\d{2}[A-Z]?):(.*)$");

    /**
     * {@code :61:} - value date, optional entry date (MMDD), mark, optional funds code, amount,
     * transaction type, then the references. The references are split at the first {@code //} in code
     * (a customer reference may itself hold a single {@code /}), and the supplementary details (the
     * field's second line) are taken separately.
     */
    private static final Pattern STATEMENT_LINE =
            Pattern.compile("^(\\d{6})(\\d{4})?(R?[CD])([A-Z])?(\\d{1,15},\\d{0,15})([A-Z][A-Z0-9]{3})(.*)$");

    private static final String BANK_REFERENCE_SEPARATOR = "//";

    private Mt940() {}

    /**
     * Read the statement lines of an MT940 file.
     *
     * @param text the file's text
     * @return one record per {@code :61:} line, in file order
     * @throws IllegalArgumentException when the file holds no statement line, or a {@code :61:} line
     *         cannot be read
     */
    public static List<Map<String, String>> read(String text) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> statement = new LinkedHashMap<>();
        Map<String, String> current = null;
        int lineNumber = 0;
        for (Tag tag : tags(text)) {
            switch (tag.name()) {
                case "20" -> {
                    statement = new LinkedHashMap<>();
                    statement.put("statementId", tag.value()
                                                    .trim());
                    current = null;
                    lineNumber = 0;
                }
                case "25" -> statement.put("account", tag.value()
                                                         .trim());
                case "28C" -> statement.put("sequence", tag.value()
                                                           .trim());
                case "60F", "60M" -> statement.put("currency", openingCurrency(tag.value()));
                case "61" -> {
                    lineNumber++;
                    current = new LinkedHashMap<>(statement);
                    current.put("lineNumber", Integer.toString(lineNumber));
                    readStatementLine(tag.value(), current, records.size() + 1);
                    records.add(current);
                }
                case "86" -> {
                    if (current != null) {
                        current.put("details", tag.value()
                                                  .strip());
                    }
                }
                default -> {
                    // closing and available balances, the statement's own free text: not line data
                }
            }
        }
        if (records.isEmpty()) {
            throw new IllegalArgumentException("the file holds no MT940 statement line (:61:)");
        }
        return records;
    }

    private static void readStatementLine(String value, Map<String, String> record, int number) {
        String first = value;
        String supplementary = null;
        int lineBreak = value.indexOf('\n');
        if (lineBreak >= 0) {
            first = value.substring(0, lineBreak);
            supplementary = value.substring(lineBreak + 1)
                                 .strip();
        }
        Matcher matcher = STATEMENT_LINE.matcher(first.strip());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("statement line " + number + " is not a valid :61: field [" + first.strip() + "]");
        }
        LocalDate valueDate = date(matcher.group(1), number);
        record.put("valueDate", valueDate.toString());
        if (matcher.group(2) != null) {
            record.put("bookingDate", entryDate(valueDate, matcher.group(2), number).toString());
        }
        String mark = matcher.group(3);
        record.put("mark", mark);
        String amount = matcher.group(5)
                               .replace(',', '.');
        if (amount.endsWith(".")) {
            amount = amount.substring(0, amount.length() - 1);
        }
        boolean negative = "D".equals(mark) || "RC".equals(mark);
        record.put("amount", negative ? "-" + amount : amount);
        record.put("transactionType", matcher.group(6));
        String references = matcher.group(7);
        int separator = references.indexOf(BANK_REFERENCE_SEPARATOR);
        if (separator < 0) {
            putIfPresent(record, "reference", references);
        } else {
            putIfPresent(record, "reference", references.substring(0, separator));
            putIfPresent(record, "bankReference", references.substring(separator + BANK_REFERENCE_SEPARATOR.length()));
        }
        putIfPresent(record, "supplementary", supplementary);
    }

    private static void putIfPresent(Map<String, String> record, String key, String value) {
        if (value != null && !value.isBlank()) {
            record.put(key, value.strip());
        }
    }

    /** {@code :60F:} is mark, YYMMDD, currency, amount - the statement's currency is characters 7-9. */
    private static String openingCurrency(String value) {
        String balance = value.strip();
        return balance.length() >= 10 ? balance.substring(7, 10) : null;
    }

    private static LocalDate date(String yymmdd, int number) {
        int year = Integer.parseInt(yymmdd.substring(0, 2));
        try {
            return LocalDate.of(year < 80 ? 2000 + year : 1900 + year, Integer.parseInt(yymmdd.substring(2, 4)),
                    Integer.parseInt(yymmdd.substring(4, 6)));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("statement line " + number + " has an invalid value date [" + yymmdd + "]", e);
        }
    }

    /**
     * The entry date carries no year: it is the value date's, unless the two straddle a year end (a
     * December value date booked in January, or the reverse).
     */
    private static LocalDate entryDate(LocalDate valueDate, String mmdd, int number) {
        int month = Integer.parseInt(mmdd.substring(0, 2));
        int year = valueDate.getYear();
        if (month < valueDate.getMonthValue() - 6) {
            year++;
        } else if (month > valueDate.getMonthValue() + 6) {
            year--;
        }
        try {
            return LocalDate.of(year, month, Integer.parseInt(mmdd.substring(2, 4)));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("statement line " + number + " has an invalid entry date [" + mmdd + "]", e);
        }
    }

    /** The tags of the message text, a continuation line joined to its tag with a line break. */
    private static List<Tag> tags(String text) {
        List<Tag> tags = new ArrayList<>();
        String name = null;
        StringBuilder value = new StringBuilder();
        for (String raw : text.split("\\r\\n|\\r|\\n")) {
            String line = envelopeContent(raw);
            if (line == null) {
                continue;
            }
            Matcher matcher = TAG.matcher(line);
            if (matcher.matches()) {
                if (name != null) {
                    tags.add(new Tag(name, value.toString()));
                }
                name = matcher.group(1);
                value.setLength(0);
                value.append(matcher.group(2));
            } else if (name != null && !line.isBlank()) {
                value.append('\n')
                     .append(line);
            }
        }
        if (name != null) {
            tags.add(new Tag(name, value.toString()));
        }
        return tags;
    }

    /**
     * The message content of a line, or {@code null} for a line that is only envelope: the SWIFT header
     * blocks before {@code {4:}} and the {@code -}} that closes block 4.
     */
    private static String envelopeContent(String line) {
        String trimmed = line.strip();
        if (trimmed.equals("-") || trimmed.startsWith("-}")) {
            return null;
        }
        if (trimmed.startsWith("{")) {
            int block4 = trimmed.indexOf("{4:");
            if (block4 < 0) {
                return null;
            }
            String rest = trimmed.substring(block4 + 3);
            return rest.isEmpty() ? null : rest;
        }
        return line;
    }

    private record Tag(String name, String value) {
    }
}
