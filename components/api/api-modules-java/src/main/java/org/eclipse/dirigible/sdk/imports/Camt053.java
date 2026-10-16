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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Reads an ISO 20022 camt.053 bank-to-customer statement into one record per entry ({@code Ntry}).
 *
 * <p>
 * Every record carries the keys of {@link #KEYS} - the vocabulary {@link Mt940} uses, plus the
 * counterparty a camt entry names. Values are canonical: an amount is a plain decimal, negative for
 * a debit ({@code DBIT}), and a date is ISO {@code yyyy-MM-dd}. A key the entry does not carry is
 * absent from its record.
 *
 * <p>
 * Elements are matched by their local name, so every published version of the message
 * ({@code camt.053.001.02} through {@code .001.08} and later) reads alike. The file's own XML
 * declaration decides its encoding. A DOCTYPE is refused outright: a statement has no use for one,
 * and an external entity in an uploaded file must never be resolved.
 */
public final class Camt053 {

    /** Every key a record may carry. */
    public static final Set<String> KEYS =
            Set.of("statementId", "sequence", "account", "currency", "lineNumber", "valueDate", "bookingDate", "mark", "amount",
                    "transactionType", "reference", "bankReference", "details", "counterpartyName", "counterpartyAccount");

    private static final String NOT_PROVIDED = "NOTPROVIDED";

    private Camt053() {}

    /**
     * Read the entries of a camt.053 document.
     *
     * @param content the file's bytes
     * @return one record per entry, in document order
     * @throws IllegalArgumentException when the file is not a well-formed camt.053 statement or an
     *         entry lacks its amount or direction
     */
    public static List<Map<String, String>> read(byte[] content) {
        Element root = parse(content).getDocumentElement();
        Element message = child(root, "BkToCstmrStmt");
        if (message == null) {
            throw new IllegalArgumentException(
                    "the file is not a camt.053 bank-to-customer statement (no BkToCstmrStmt under " + root.getLocalName() + ")");
        }
        List<Map<String, String>> records = new ArrayList<>();
        for (Element statement : children(message, "Stmt")) {
            Map<String, String> header = new LinkedHashMap<>();
            putIfPresent(header, "statementId", text(child(statement, "Id")));
            putIfPresent(header, "sequence", text(child(statement, "ElctrncSeqNb")));
            Element account = child(statement, "Acct");
            putIfPresent(header, "account", accountId(child(account, "Id")));
            putIfPresent(header, "currency", text(child(account, "Ccy")));
            int lineNumber = 0;
            for (Element entry : children(statement, "Ntry")) {
                lineNumber++;
                Map<String, String> record = new LinkedHashMap<>(header);
                record.put("lineNumber", Integer.toString(lineNumber));
                readEntry(entry, record, records.size() + 1);
                records.add(record);
            }
        }
        if (records.isEmpty()) {
            throw new IllegalArgumentException("the file holds no camt.053 statement entry (Ntry)");
        }
        return records;
    }

    private static void readEntry(Element entry, Map<String, String> record, int number) {
        Element amount = child(entry, "Amt");
        String direction = text(child(entry, "CdtDbtInd"));
        if (amount == null || text(amount) == null || direction == null) {
            throw new IllegalArgumentException("entry " + number + " has no Amt or no CdtDbtInd");
        }
        boolean debit = "DBIT".equals(direction);
        if (!debit && !"CRDT".equals(direction)) {
            throw new IllegalArgumentException("entry " + number + " has an unknown CdtDbtInd [" + direction + "]");
        }
        BigDecimal value;
        try {
            value = new BigDecimal(text(amount));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("entry " + number + " has an invalid amount [" + text(amount) + "]", e);
        }
        record.put("amount", (debit ? value.negate() : value).toPlainString());
        record.put("mark", debit ? "D" : "C");
        String currency = amount.getAttribute("Ccy");
        if (!currency.isBlank()) {
            record.put("currency", currency);
        }
        putIfPresent(record, "bookingDate", date(child(entry, "BookgDt")));
        putIfPresent(record, "valueDate", date(child(entry, "ValDt")));
        putIfPresent(record, "bankReference", text(child(entry, "AcctSvcrRef")));
        putIfPresent(record, "transactionType", transactionType(child(entry, "BkTxCd")));
        Element details = first(entry, "TxDtls");
        putIfPresent(record, "reference", reference(entry, details));
        putIfPresent(record, "details", remittance(entry));
        if (details != null) {
            Element parties = child(details, "RltdPties");
            // The counterparty is whoever is on the other side: the debtor of a credit, the creditor of a
            // debit.
            String party = debit ? "Cdtr" : "Dbtr";
            putIfPresent(record, "counterpartyName", text(first(child(parties, party), "Nm")));
            Element partyAccount = child(parties, party + "Acct");
            putIfPresent(record, "counterpartyAccount", accountId(child(partyAccount, "Id")));
        }
    }

    private static String reference(Element entry, Element details) {
        String endToEnd = details == null ? null : text(first(details, "EndToEndId"));
        if (endToEnd != null && !NOT_PROVIDED.equals(endToEnd)) {
            return endToEnd;
        }
        return text(child(entry, "NtryRef"));
    }

    /**
     * The unstructured remittance lines of the entry, or its additional information when it has none.
     */
    private static String remittance(Element entry) {
        List<String> lines = new ArrayList<>();
        NodeList unstructured = entry.getElementsByTagNameNS("*", "Ustrd");
        for (int i = 0; i < unstructured.getLength(); i++) {
            String line = text((Element) unstructured.item(i));
            if (line != null) {
                lines.add(line);
            }
        }
        if (!lines.isEmpty()) {
            return String.join(" ", lines);
        }
        String additional = text(child(entry, "AddtlNtryInf"));
        return additional != null ? additional : text(first(entry, "AddtlTxInf"));
    }

    /**
     * A proprietary code when the bank gives one, otherwise the ISO domain/family/sub-family triple.
     */
    private static String transactionType(Element code) {
        if (code == null) {
            return null;
        }
        String proprietary = text(child(child(code, "Prtry"), "Cd"));
        if (proprietary != null) {
            return proprietary;
        }
        Element domain = child(code, "Domn");
        String domainCode = text(child(domain, "Cd"));
        if (domainCode == null) {
            return null;
        }
        Element family = child(domain, "Fmly");
        String familyCode = text(child(family, "Cd"));
        String subFamily = text(child(family, "SubFmlyCd"));
        StringBuilder type = new StringBuilder(domainCode);
        if (familyCode != null) {
            type.append('/')
                .append(familyCode);
        }
        if (subFamily != null) {
            type.append('/')
                .append(subFamily);
        }
        return type.toString();
    }

    /** {@code Dt}, or the date part of {@code DtTm}. */
    private static String date(Element holder) {
        String date = text(child(holder, "Dt"));
        if (date != null) {
            return date.length() > 10 ? date.substring(0, 10) : date;
        }
        String dateTime = text(child(holder, "DtTm"));
        return dateTime != null && dateTime.length() >= 10 ? dateTime.substring(0, 10) : null;
    }

    private static String accountId(Element id) {
        String iban = text(child(id, "IBAN"));
        return iban != null ? iban : text(child(child(id, "Othr"), "Id"));
    }

    private static Document parse(byte[] content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            // The default handler prints every report to stderr; here each one rejects the file instead.
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });
            return builder.parse(new ByteArrayInputStream(content));
        } catch (SAXException e) {
            throw new IllegalArgumentException("the file is not well-formed XML: " + e.getMessage(), e);
        } catch (ParserConfigurationException | IOException e) {
            throw new IllegalStateException("cannot read the XML of the file", e);
        }
    }

    private static void putIfPresent(Map<String, String> record, String key, String value) {
        if (value != null) {
            record.put(key, value);
        }
    }

    /** The first direct child element with the local name, or {@code null} (also for a null parent). */
    private static Element child(Element parent, String localName) {
        if (parent == null) {
            return null;
        }
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && localName.equals(element.getLocalName())) {
                return element;
            }
        }
        return null;
    }

    private static List<Element> children(Element parent, String localName) {
        List<Element> elements = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && localName.equals(element.getLocalName())) {
                elements.add(element);
            }
        }
        return elements;
    }

    /** The first descendant element with the local name, or {@code null} (also for a null parent). */
    private static Element first(Element parent, String localName) {
        if (parent == null) {
            return null;
        }
        NodeList found = parent.getElementsByTagNameNS("*", localName);
        return found.getLength() == 0 ? null : (Element) found.item(0);
    }

    private static String text(Element element) {
        if (element == null) {
            return null;
        }
        String text = element.getTextContent()
                             .strip();
        return text.isEmpty() ? null : text;
    }
}
