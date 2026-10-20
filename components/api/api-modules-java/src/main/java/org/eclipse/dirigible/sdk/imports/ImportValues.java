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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.eclipse.dirigible.sdk.utils.LenientJavaTime;

/**
 * Converts the text values of an imported file to the types of the fields they fill, by the locale
 * rules the import declares - {@code 1.234,56} with a {@code ,} decimal separator and a {@code .}
 * grouping separator, a date as {@code dd.MM.yyyy}.
 *
 * <p>
 * Every conversion is strict: a value the rules cannot read throws, and the caller rejects the
 * whole file. A number with a separator the rules do not declare is refused rather than guessed -
 * read with {@code ,} as the decimal separator, {@code 1.234} is ambiguous unless {@code .} is the
 * declared grouping. A blank value converts to {@code null} for every type.
 */
public final class ImportValues {

    private static final char NO_GROUPING = 0;
    private static final String ISO_DATE = "yyyy-MM-dd";

    private final char decimalSeparator;
    private final char groupingSeparator;
    private final DateTimeFormatter dateFormat;
    private final String datePattern;
    private final DateTimeFormatter timestampFormat;
    private final String timestampPattern;

    private ImportValues(char decimalSeparator, char groupingSeparator, String datePattern, String timestampPattern) {
        this.decimalSeparator = decimalSeparator;
        this.groupingSeparator = groupingSeparator;
        this.datePattern = datePattern;
        this.dateFormat = DateTimeFormatter.ofPattern(datePattern, Locale.ROOT);
        this.timestampPattern = timestampPattern;
        this.timestampFormat = timestampPattern == null ? null : DateTimeFormatter.ofPattern(timestampPattern, Locale.ROOT);
    }

    /**
     * Rules for a file in a declared locale.
     *
     * @param decimalSeparator the decimal separator
     * @param groupingSeparator the grouping (thousands) separator, or empty when the numbers carry none
     * @param datePattern the {@link DateTimeFormatter} pattern of a date, or {@code null} for ISO
     *        {@code yyyy-MM-dd}
     * @param timestampPattern the pattern of a timestamp (read at UTC), or {@code null} for the ISO
     *        shapes
     * @return the rules
     * @throws IllegalArgumentException when a pattern is invalid, a separator is a digit or the two
     *         separators are the same
     */
    public static ImportValues of(char decimalSeparator, String groupingSeparator, String datePattern, String timestampPattern) {
        char grouping = groupingSeparator == null || groupingSeparator.isEmpty() ? NO_GROUPING : groupingSeparator.charAt(0);
        if (Character.isDigit(decimalSeparator) || Character.isDigit(grouping) || decimalSeparator == grouping) {
            throw new IllegalArgumentException(
                    "invalid separators: decimal [" + decimalSeparator + "], grouping [" + groupingSeparator + "]");
        }
        return new ImportValues(decimalSeparator, grouping, datePattern == null ? ISO_DATE : datePattern, timestampPattern);
    }

    /**
     * The rules of the canonical values the statement readers ({@link Mt940}, {@link Camt053}) produce:
     * plain decimals with a {@code .}, ISO dates.
     *
     * @return the canonical rules
     */
    public static ImportValues canonical() {
        return of('.', null, null, null);
    }

    /**
     * A text value, trimmed.
     *
     * @param raw the value as read
     * @return the text, or {@code null} when blank
     */
    public String text(String raw) {
        return blank(raw) ? null : raw.strip();
    }

    /**
     * A decimal value.
     *
     * @param raw the value as read
     * @return the number, or {@code null} when blank
     * @throws IllegalArgumentException when the value is not a number under these rules
     */
    public BigDecimal decimal(String raw) {
        if (blank(raw)) {
            return null;
        }
        StringBuilder normalized = new StringBuilder(raw.length());
        boolean decimalSeen = false;
        String value = raw.strip();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                normalized.append(c);
            } else if ((c == '-' || c == '+') && normalized.isEmpty()) {
                normalized.append(c);
            } else if (c == decimalSeparator && !decimalSeen) {
                normalized.append('.');
                decimalSeen = true;
            } else if (groupingSeparator != NO_GROUPING && c == groupingSeparator && !decimalSeen) {
                continue;
            } else if (Character.isSpaceChar(c) && !decimalSeen) {
                continue; // a space or a no-break space grouping digits
            } else {
                throw new IllegalArgumentException("[" + raw + "] is not a number with decimal separator [" + decimalSeparator + "]"
                        + (groupingSeparator == NO_GROUPING ? " and no grouping separator" : " and grouping [" + groupingSeparator + "]"));
            }
        }
        try {
            return new BigDecimal(normalized.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[" + raw + "] is not a number", e);
        }
    }

    /**
     * An integer value.
     *
     * @param raw the value as read
     * @return the number, or {@code null} when blank
     * @throws IllegalArgumentException when the value is not a whole number within the integer range
     */
    public Integer integer(String raw) {
        BigDecimal value = decimal(raw);
        try {
            return value == null ? null : value.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[" + raw + "] is not a whole number in the integer range", e);
        }
    }

    /**
     * A long value.
     *
     * @param raw the value as read
     * @return the number, or {@code null} when blank
     * @throws IllegalArgumentException when the value is not a whole number within the long range
     */
    public Long longInteger(String raw) {
        BigDecimal value = decimal(raw);
        try {
            return value == null ? null : value.longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[" + raw + "] is not a whole number in the long range", e);
        }
    }

    /**
     * A boolean value: {@code true}/{@code yes}/{@code y}/{@code 1} or {@code false}/{@code no}/
     * {@code n}/{@code 0}, in any case.
     *
     * @param raw the value as read
     * @return the flag, or {@code null} when blank
     * @throws IllegalArgumentException for any other value
     */
    public Boolean bool(String raw) {
        if (blank(raw)) {
            return null;
        }
        return switch (raw.strip()
                          .toLowerCase(Locale.ROOT)) {
            case "true", "yes", "y", "1" -> Boolean.TRUE;
            case "false", "no", "n", "0" -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("[" + raw + "] is not a yes/no value");
        };
    }

    /**
     * A date value.
     *
     * @param raw the value as read
     * @return the date, or {@code null} when blank
     * @throws IllegalArgumentException when the value does not match the date pattern
     */
    public LocalDate date(String raw) {
        if (blank(raw)) {
            return null;
        }
        try {
            return LocalDate.parse(raw.strip(), dateFormat);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("[" + raw + "] is not a date in the format [" + datePattern + "]", e);
        }
    }

    /**
     * A timestamp value - by the declared pattern at UTC, or any ISO shape when none is declared.
     *
     * @param raw the value as read
     * @return the instant, or {@code null} when blank
     * @throws IllegalArgumentException when the value does not match
     */
    public Instant timestamp(String raw) {
        if (blank(raw)) {
            return null;
        }
        if (timestampFormat == null) {
            Instant instant = LenientJavaTime.parseInstant(raw.strip());
            if (instant == null) {
                throw new IllegalArgumentException("[" + raw + "] is not an ISO timestamp");
            }
            return instant;
        }
        try {
            return LocalDateTime.parse(raw.strip(), timestampFormat)
                                .toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("[" + raw + "] is not a timestamp in the format [" + timestampPattern + "]", e);
        }
    }

    private static boolean blank(String raw) {
        return raw == null || raw.isBlank();
    }
}
