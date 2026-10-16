/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.export;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * One field of an export record: what the value is called and how it is written - its date pattern
 * or decimal scale, and in a positional file its width, alignment and padding.
 *
 * <p>
 * Immutable: every refinement returns a copy, so a generated export declares its record layouts
 * once, as constants, and reuses them for every row.
 */
public final class ExportField {

    private final String name;
    private final int width;
    private final boolean alignRight;
    private final char pad;
    private final boolean truncate;
    private final Integer scale;
    private final String decimalSeparator;
    private final String pattern;
    private final boolean attribute;

    private ExportField(String name, int width, boolean alignRight, char pad, boolean truncate, Integer scale, String decimalSeparator,
            String pattern, boolean attribute) {
        this.name = name;
        this.width = width;
        this.alignRight = alignRight;
        this.pad = pad;
        this.truncate = truncate;
        this.scale = scale;
        this.decimalSeparator = decimalSeparator;
        this.pattern = pattern;
        this.attribute = attribute;
    }

    /**
     * A field written as its plain text, with no width.
     *
     * @param name the field name - the XML element or attribute name, and how errors refer to it
     * @return the field
     */
    public static ExportField named(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("An export field needs a name");
        }
        return new ExportField(name, 0, false, ' ', false, null, ".", null, false);
    }

    /**
     * The same field occupying exactly {@code width} characters of a positional record.
     *
     * @param width the width in characters, at least 1
     * @return the refined field
     */
    public ExportField width(int width) {
        if (width < 1) {
            throw new IllegalArgumentException("Export field [" + name + "] width must be at least 1, got " + width);
        }
        return new ExportField(name, width, alignRight, pad, truncate, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same field right-aligned within its width (a field is left-aligned by default).
     *
     * @return the refined field
     */
    public ExportField alignRight() {
        return new ExportField(name, width, true, pad, truncate, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same field padded with {@code pad} instead of a space. A right-aligned negative number padded
     * with {@code 0} keeps its sign in front of the zeros.
     *
     * @param pad the padding character
     * @return the refined field
     */
    public ExportField pad(char pad) {
        return new ExportField(name, width, alignRight, pad, truncate, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same field cut to its width when the value is longer. Without it a value wider than the field
     * is refused, because a positional file whose fields shift is unreadable by its recipient.
     *
     * @return the refined field
     */
    public ExportField truncate() {
        return new ExportField(name, width, alignRight, pad, true, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same field written as a decimal rounded half-up to {@code scale} places.
     *
     * @param scale the number of decimal places, at least 0
     * @return the refined field
     */
    public ExportField scale(int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("Export field [" + name + "] scale must not be negative, got " + scale);
        }
        return new ExportField(name, width, alignRight, pad, truncate, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same decimal field written with {@code separator} between its integer and fraction digits;
     * the empty separator writes an implied decimal point ({@code 12.30} as {@code 1230}).
     *
     * @param separator the separator, {@code .} by default
     * @return the refined field
     */
    public ExportField decimalSeparator(String separator) {
        return new ExportField(name, width, alignRight, pad, truncate, scale, separator == null ? "." : separator, pattern, attribute);
    }

    /**
     * The same field written as a date/time in a {@link DateTimeFormatter} pattern.
     *
     * @param pattern the pattern, e.g. {@code ddMMyyyy}
     * @return the refined field
     */
    public ExportField pattern(String pattern) {
        DateTimeFormatter.ofPattern(pattern);
        return new ExportField(name, width, alignRight, pad, truncate, scale, decimalSeparator, pattern, attribute);
    }

    /**
     * The same field written as an attribute of its record's XML element instead of a child element.
     *
     * @return the refined field
     */
    public ExportField attribute() {
        return new ExportField(name, width, alignRight, pad, truncate, scale, decimalSeparator, pattern, true);
    }

    /**
     * The field name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    boolean isAttribute() {
        return attribute;
    }

    /**
     * The value as this field writes it: in its date pattern or decimal scale when it declares one,
     * empty for {@code null}.
     *
     * @param value the value
     * @return the text
     */
    String text(Object value) {
        if (value == null) {
            return "";
        }
        if (pattern != null) {
            return DateTimeFormatter.ofPattern(pattern)
                                    .format(temporal(value));
        }
        if (scale != null) {
            String plain = decimal(value).setScale(scale, RoundingMode.HALF_UP)
                                         .toPlainString();
            return ".".equals(decimalSeparator) ? plain : plain.replace(".", decimalSeparator);
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof Double || value instanceof Float) {
            // A report row comes back through JSON, so its whole numbers arrive as doubles: 3.0 is 3.
            BigDecimal decimal = BigDecimal.valueOf(((Number) value).doubleValue());
            return decimal.signum() == 0 ? "0"
                    : decimal.stripTrailingZeros()
                             .toPlainString();
        }
        return String.valueOf(value);
    }

    /**
     * The text fitted to this field's width - padded, or cut when the field truncates.
     *
     * @param text the text
     * @return the fitted text
     * @throws IllegalArgumentException when the text is wider than the field and the field does not
     *         truncate
     */
    String fit(String text) {
        if (width == 0) {
            return text;
        }
        if (text.length() > width) {
            if (!truncate) {
                throw new IllegalArgumentException(
                        "field [" + name + "]: [" + text + "] is " + text.length() + " characters, wider than its " + width);
            }
            return text.substring(0, width);
        }
        String padding = String.valueOf(pad)
                               .repeat(width - text.length());
        if (!alignRight) {
            return text + padding;
        }
        if (pad == '0' && text.startsWith("-")) {
            return "-" + padding + text.substring(1);
        }
        return padding + text;
    }

    private BigDecimal decimal(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        try {
            return new BigDecimal(String.valueOf(value)
                                        .trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("field [" + name + "]: [" + value + "] is not a number", ex);
        }
    }

    private TemporalAccessor temporal(Object value) {
        if (value instanceof Instant instant) {
            return instant.atZone(ZoneId.systemDefault());
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof Date date) {
            return date.toInstant()
                       .atZone(ZoneId.systemDefault());
        }
        if (value instanceof TemporalAccessor temporal) {
            return temporal;
        }
        if (value instanceof Number epochMillis) {
            return Instant.ofEpochMilli(epochMillis.longValue())
                          .atZone(ZoneId.systemDefault());
        }
        return parse(String.valueOf(value)
                           .trim());
    }

    /** An ISO date or date-time as a database row or a JSON round trip spells it. */
    private TemporalAccessor parse(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException notADate) {
            // fall through to the date-time shapes
        }
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException notALocalDateTime) {
            // fall through to the offset shape
        }
        try {
            return OffsetDateTime.parse(text)
                                 .atZoneSameInstant(ZoneId.systemDefault());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("field [" + name + "]: [" + text + "] is not a date", ex);
        }
    }
}
