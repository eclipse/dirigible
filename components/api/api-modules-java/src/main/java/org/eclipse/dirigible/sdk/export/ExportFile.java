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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A file in an exacting format, written one record at a time: positional (fixed-width), delimited
 * or XML, in a declared character encoding.
 *
 * <p>
 * Nothing is ever written approximately. A value wider than its positional field, a delimited value
 * that cannot be quoted, a character the encoding cannot represent or one XML does not allow is
 * refused with the record and the field it belongs to - a filing with a silently replaced character
 * or a shifted column is rejected by its recipient long after the run that produced it reported
 * success.
 */
public final class ExportFile {

    private enum Format {
        FIXED_WIDTH, DELIMITED, XML
    }

    private final Format format;
    private final Charset charset;
    private final CharsetEncoder encoder;
    private final String lineEnding;
    private final String delimiter;
    private final String quote;
    private final boolean quoteAll;
    private final StringBuilder text = new StringBuilder();
    private final StringWriter xmlText;
    private final XMLStreamWriter xml;
    private int records;
    private byte[] content;

    private ExportFile(Format format, String encoding, String lineEnding, String delimiter, String quote, boolean quoteAll,
            String rootElement) {
        this.format = format;
        this.charset = charset(encoding);
        this.encoder = charset.newEncoder()
                              .onMalformedInput(CodingErrorAction.REPORT)
                              .onUnmappableCharacter(CodingErrorAction.REPORT);
        this.lineEnding = lineEnding == null ? "\r\n" : lineEnding;
        this.delimiter = delimiter;
        this.quote = quote == null ? "" : quote;
        this.quoteAll = quoteAll;
        if (format == Format.XML) {
            this.xmlText = new StringWriter();
            try {
                // The JDK's own writer, never a provider found on the classpath: an exact format must not
                // change shape (quote style, empty-element form) with whichever XML library is deployed.
                this.xml = XMLOutputFactory.newDefaultFactory()
                                           .createXMLStreamWriter(xmlText);
                xml.writeStartDocument(charset.name(), "1.0");
                xml.writeCharacters("\n");
                xml.writeStartElement(rootElement);
            } catch (XMLStreamException ex) {
                throw new IllegalStateException("The XML export could not be started", ex);
            }
        } else {
            this.xmlText = null;
            this.xml = null;
        }
    }

    /**
     * A positional file: every field occupies its declared width, records are separated by
     * {@code lineEnding}.
     *
     * @param encoding the character encoding, e.g. {@code windows-1251}
     * @param lineEnding the record terminator, e.g. {@code "\r\n"}
     * @return the file
     */
    public static ExportFile fixedWidth(String encoding, String lineEnding) {
        return new ExportFile(Format.FIXED_WIDTH, encoding, lineEnding, null, null, false, null);
    }

    /**
     * A delimited file (CSV and its relatives).
     *
     * @param encoding the character encoding
     * @param lineEnding the record terminator
     * @param delimiter the field separator, e.g. {@code ,} or {@code ;}
     * @param quote the quote character, empty for none - a value that then needs quoting is refused
     * @param quoteAll whether every value is quoted, rather than only those that must be
     * @return the file
     */
    public static ExportFile delimited(String encoding, String lineEnding, String delimiter, String quote, boolean quoteAll) {
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("A delimited export needs a delimiter");
        }
        return new ExportFile(Format.DELIMITED, encoding, lineEnding, delimiter, quote, quoteAll, null);
    }

    /**
     * An XML document: one {@code rootElement}, one child element per record, one element (or
     * attribute) per field.
     *
     * @param encoding the character encoding, declared in the XML prolog
     * @param rootElement the document element
     * @return the file
     */
    public static ExportFile xml(String encoding, String rootElement) {
        return new ExportFile(Format.XML, encoding, null, null, null, false, rootElement);
    }

    /**
     * The SHA-256 digest of a file's content, as lower-case hex.
     *
     * @param content the bytes
     * @return the digest
     */
    public static String checksum(byte[] content) {
        try {
            return HexFormat.of()
                            .formatHex(MessageDigest.getInstance("SHA-256")
                                                    .digest(content));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    /**
     * Adds a row's value to a running total - the {@code sum(...)} of a header or trailer. An absent
     * value adds nothing; a value that is not a number is refused, since a total that skips it would
     * misstate the file.
     *
     * @param total the total so far
     * @param value the row's value: a number, or its text
     * @return the new total
     */
    public static BigDecimal add(BigDecimal total, Object value) {
        if (value == null) {
            return total;
        }
        if (value instanceof BigDecimal decimal) {
            return total.add(decimal);
        }
        if (value instanceof Double || value instanceof Float) {
            return total.add(BigDecimal.valueOf(((Number) value).doubleValue()));
        }
        try {
            return total.add(new BigDecimal(String.valueOf(value)
                                                  .trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("[" + value + "] is not a number, so it cannot be totalled", ex);
        }
    }

    /**
     * Writes one record.
     *
     * @param element the record's type - the XML element name, and how errors refer to the record
     * @param fields the record layout
     * @param values one value per field, in the same order
     * @throws IllegalArgumentException when a value cannot be written exactly
     */
    public void record(String element, ExportField[] fields, Object... values) {
        if (content != null) {
            throw new IllegalStateException("The export is already complete");
        }
        if (fields.length != values.length) {
            throw new IllegalArgumentException(
                    "Record [" + element + "] has " + fields.length + " fields but " + values.length + " values");
        }
        records++;
        List<String> texts = new ArrayList<>(fields.length);
        for (int index = 0; index < fields.length; index++) {
            texts.add(checked(element, fields[index], values[index]));
        }
        switch (format) {
            case FIXED_WIDTH -> fixedWidth(element, fields, texts);
            case DELIMITED -> delimited(element, fields, texts, values);
            case XML -> xml(element, fields, texts, values);
        }
    }

    /**
     * The number of records written so far.
     *
     * @return the count
     */
    public int getRecords() {
        return records;
    }

    /**
     * The media type of the file.
     *
     * @return the content type, with its charset where the format does not declare one itself
     */
    public String getContentType() {
        return switch (format) {
            case FIXED_WIDTH -> "text/plain; charset=" + charset.name();
            case DELIMITED -> "text/csv; charset=" + charset.name();
            case XML -> "application/xml";
        };
    }

    /**
     * Completes the file and returns it, encoded. Calling it again returns the same bytes.
     *
     * @return the content
     */
    public byte[] content() {
        if (content == null) {
            if (format == Format.XML) {
                try {
                    xml.writeCharacters("\n");
                    xml.writeEndElement();
                    xml.writeEndDocument();
                    xml.close();
                } catch (XMLStreamException ex) {
                    throw new IllegalStateException("The XML export could not be completed", ex);
                }
                text.append(xmlText)
                    .append('\n');
            }
            content = encode(text);
        }
        return content.clone();
    }

    private void fixedWidth(String element, ExportField[] fields, List<String> texts) {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < fields.length; index++) {
            try {
                line.append(fields[index].fit(texts.get(index)));
            } catch (IllegalArgumentException ex) {
                throw refused(element, ex.getMessage(), ex);
            }
        }
        text.append(line)
            .append(lineEnding);
    }

    private void delimited(String element, ExportField[] fields, List<String> texts, Object[] values) {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < fields.length; index++) {
            if (index > 0) {
                line.append(delimiter);
            }
            line.append(quoted(element, fields[index], texts.get(index), values[index] == null));
        }
        text.append(line)
            .append(lineEnding);
    }

    private String quoted(String element, ExportField field, String value, boolean absent) {
        boolean needsQuotes = value.contains(delimiter) || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0
                || (!quote.isEmpty() && value.contains(quote));
        if (quote.isEmpty()) {
            if (needsQuotes) {
                throw refused(element, "field [" + field.getName() + "]: [" + value
                        + "] carries the delimiter or a line break and the format quotes nothing", null);
            }
            return value;
        }
        // An absent value stays empty even under quoteAll: "" would read as an empty string, not as none.
        if (needsQuotes || (quoteAll && !absent)) {
            return quote + value.replace(quote, quote + quote) + quote;
        }
        return value;
    }

    private void xml(String element, ExportField[] fields, List<String> texts, Object[] values) {
        try {
            xml.writeCharacters("\n  ");
            xml.writeStartElement(element);
            // StAX takes attributes only before the element's content, so they go first whatever the
            // declared order - an attribute has no position among its element's children anyway.
            for (int index = 0; index < fields.length; index++) {
                if (fields[index].isAttribute() && values[index] != null) {
                    xml.writeAttribute(fields[index].getName(), texts.get(index));
                }
            }
            boolean children = false;
            for (int index = 0; index < fields.length; index++) {
                if (!fields[index].isAttribute() && values[index] != null) {
                    children = true;
                    xml.writeCharacters("\n    ");
                    xml.writeStartElement(fields[index].getName());
                    xml.writeCharacters(texts.get(index));
                    xml.writeEndElement();
                }
            }
            if (children) {
                xml.writeCharacters("\n  ");
            }
            xml.writeEndElement();
        } catch (XMLStreamException ex) {
            throw refused(element, ex.getMessage(), ex);
        }
    }

    /** The field's text, refused when the encoding or XML cannot carry it exactly. */
    private String checked(String element, ExportField field, Object value) {
        String formatted;
        try {
            formatted = field.text(value);
        } catch (IllegalArgumentException ex) {
            throw refused(element, ex.getMessage(), ex);
        }
        for (int offset = 0; offset < formatted.length();) {
            int codePoint = formatted.codePointAt(offset);
            if (format == Format.XML && !allowedInXml(codePoint)) {
                throw refused(element,
                        "field [" + field.getName() + "] carries character U+" + hex(codePoint) + ", which XML does not allow", null);
            }
            if (!encoder.canEncode(new String(Character.toChars(codePoint)))) {
                throw refused(element, "field [" + field.getName() + "]: [" + formatted + "] carries ["
                        + new String(Character.toChars(codePoint)) + "], which " + charset.name() + " cannot represent", null);
            }
            offset += Character.charCount(codePoint);
        }
        return formatted;
    }

    private IllegalArgumentException refused(String element, String reason, Exception cause) {
        return new IllegalArgumentException("record " + records + " [" + element + "], " + reason, cause);
    }

    private byte[] encode(CharSequence chars) {
        try {
            ByteBuffer bytes = encoder.reset()
                                      .encode(CharBuffer.wrap(chars));
            byte[] encoded = new byte[bytes.remaining()];
            bytes.get(encoded);
            return encoded;
        } catch (CharacterCodingException ex) {
            throw new IllegalArgumentException("The export cannot be written in " + charset.name(), ex);
        }
    }

    private static boolean allowedInXml(int codePoint) {
        return codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD || (codePoint >= 0x20 && codePoint <= 0xD7FF)
                || (codePoint >= 0xE000 && codePoint <= 0xFFFD) || (codePoint >= 0x10000 && codePoint <= 0x10FFFF);
    }

    private static String hex(int codePoint) {
        return String.format("%04X", codePoint);
    }

    private static Charset charset(String encoding) {
        try {
            return Charset.forName(encoding == null || encoding.isBlank() ? "UTF-8" : encoding.trim());
        } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
            throw new IllegalArgumentException("Unknown export encoding [" + encoding + "]", ex);
        }
    }
}
