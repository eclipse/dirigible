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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * The run-time half of the intent {@code exports:} construct: what a declared record layout writes
 * for a value, and what it refuses to write.
 */
class ExportFileTest {

    private static final Charset WINDOWS_1251 = Charset.forName("windows-1251");

    private static final ExportField[] LINE = {ExportField.named("VatNumber")
                                                          .width(10),
            ExportField.named("DocumentDate")
                       .width(8)
                       .pattern("ddMMyyyy"),
            ExportField.named("TaxBase")
                       .width(9)
                       .scale(2)
                       .alignRight()};

    @Test
    void aPositionalRecordIsPaddedToItsWidthsInTheDeclaredEncoding() {
        ExportFile file = ExportFile.fixedWidth("windows-1251", "\r\n");
        file.record("line", LINE, "BG1234", LocalDate.of(2026, 3, 9), new BigDecimal("1234.5"));
        file.record("line", LINE, "Фирма", "2026-03-31", 7);

        String expected = "BG1234    09032026  1234.50\r\n" + "Фирма     31032026     7.00\r\n";
        assertArrayEquals(expected.getBytes(WINDOWS_1251), file.content());
        assertEquals("text/plain; charset=windows-1251", file.getContentType());
        assertEquals(2, file.getRecords());
    }

    @Test
    void aValueWiderThanItsFieldIsRefusedNamingTheRecordAndTheField() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");
        file.record("line", LINE, "BG1", null, 1);

        IllegalArgumentException refused =
                assertThrows(IllegalArgumentException.class, () -> file.record("line", LINE, "BG123456789", null, 1));
        assertEquals("record 2 [line], field [VatNumber]: [BG123456789] is 11 characters, wider than its 10", refused.getMessage());
    }

    @Test
    void aTruncatingFieldIsCutToItsWidth() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");
        file.record("line", new ExportField[] {ExportField.named("Name")
                                                          .width(4)
                                                          .truncate()},
                "Acme Trading");

        assertEquals("Acme\n", new String(file.content(), StandardCharsets.UTF_8));
    }

    @Test
    void aZeroPaddedNegativeKeepsItsSignInFront() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");
        file.record("line", new ExportField[] {ExportField.named("Amount")
                                                          .width(8)
                                                          .scale(2)
                                                          .alignRight()
                                                          .pad('0')},
                new BigDecimal("-12.345"));

        assertEquals("-0012.35\n", new String(file.content(), StandardCharsets.UTF_8));
    }

    @Test
    void anEmptyDecimalSeparatorWritesAnImpliedPoint() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");
        file.record("line", new ExportField[] {ExportField.named("Amount")
                                                          .width(6)
                                                          .scale(2)
                                                          .decimalSeparator("")
                                                          .alignRight()
                                                          .pad('0')},
                12.3d);

        assertEquals("001230\n", new String(file.content(), StandardCharsets.UTF_8));
    }

    @Test
    void aCharacterTheEncodingCannotRepresentIsRefusedRatherThanReplaced() {
        ExportFile file = ExportFile.fixedWidth("windows-1251", "\r\n");

        IllegalArgumentException refused = assertThrows(IllegalArgumentException.class,
                () -> file.record("line", new ExportField[] {ExportField.named("Name")}, "Фирма € 中"));
        assertTrue(refused.getMessage()
                          .contains("[中], which windows-1251 cannot represent"),
                refused.getMessage());
    }

    @Test
    void aDelimitedValueIsQuotedOnlyWhenItMustBe() {
        ExportFile file = ExportFile.delimited("UTF-8", "\r\n", ";", "\"", false);
        ExportField[] fields = {ExportField.named("Name"), ExportField.named("Note"), ExportField.named("Amount")
                                                                                                 .scale(2)};
        file.record("line", fields, "Acme; Ltd", "say \"hi\"", 3);
        file.record("line", fields, "Plain", null, null);

        assertEquals("\"Acme; Ltd\";\"say \"\"hi\"\"\";3.00\r\nPlain;;\r\n", new String(file.content(), StandardCharsets.UTF_8));
        assertEquals("text/csv; charset=UTF-8", file.getContentType());
    }

    @Test
    void quoteAllQuotesEveryPresentValue() {
        ExportFile file = ExportFile.delimited("UTF-8", "\n", ",", "\"", true);
        file.record("line", new ExportField[] {ExportField.named("A"), ExportField.named("B")}, "x", null);

        assertEquals("\"x\",\n", new String(file.content(), StandardCharsets.UTF_8));
    }

    @Test
    void aValueNeedingQuotesIsRefusedWhenTheFormatQuotesNothing() {
        ExportFile file = ExportFile.delimited("UTF-8", "\n", "|", "", false);

        assertThrows(IllegalArgumentException.class, () -> file.record("line", new ExportField[] {ExportField.named("A")}, "a|b"));
    }

    @Test
    void anXmlRecordIsAnElementWithItsAttributesFirstAndItsAbsentFieldsOmitted() {
        ExportFile file = ExportFile.xml("windows-1251", "Ledger");
        ExportField[] header = {ExportField.named("Period")
                                           .attribute()};
        ExportField[] line = {ExportField.named("Name"), ExportField.named("Amount")
                                                                    .scale(2),
                ExportField.named("Code")
                           .attribute(),
                ExportField.named("Note")};
        file.record("Header", header, "2026-03");
        file.record("Line", line, "Фирма & Co <1>", 12, "A\"1", null);

        String expected = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n<Ledger>\n  <Header Period=\"2026-03\"></Header>\n"
                + "  <Line Code=\"A&quot;1\">\n    <Name>Фирма &amp; Co &lt;1&gt;</Name>\n    <Amount>12.00</Amount>\n  </Line>\n</Ledger>\n";
        assertEquals(expected, new String(file.content(), WINDOWS_1251));
        assertEquals("application/xml", file.getContentType());
    }

    @Test
    void aCharacterXmlDoesNotAllowIsRefused() {
        ExportFile file = ExportFile.xml("UTF-8", "Ledger");

        IllegalArgumentException refused = assertThrows(IllegalArgumentException.class,
                () -> file.record("Line", new ExportField[] {ExportField.named("Name")}, "a\u0001b"));
        assertTrue(refused.getMessage()
                          .contains("U+0001"),
                refused.getMessage());
    }

    @Test
    void aValueThatIsNotADateIsRefusedByAPatternedField() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");

        assertThrows(IllegalArgumentException.class, () -> file.record("line", new ExportField[] {ExportField.named("Day")
                                                                                                             .pattern("ddMMyyyy")},
                "yesterday"));
    }

    @Test
    void aWholeDoubleIsWrittenWithoutAFraction() {
        ExportFile file = ExportFile.delimited("UTF-8", "\n", ",", "\"", false);
        file.record("line", new ExportField[] {ExportField.named("Count"), ExportField.named("Share")}, 3.0d, 0.25d);

        assertEquals("3,0.25\n", new String(file.content(), StandardCharsets.UTF_8));
    }

    @Test
    void aTotalAddsEveryNumberAndSkipsAnAbsentValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (Object value : new Object[] {new BigDecimal("1.10"), 2, 0.2d, "3.5", null}) {
            total = ExportFile.add(total, value);
        }
        assertEquals(new BigDecimal("6.80"), total);
        assertThrows(IllegalArgumentException.class, () -> ExportFile.add(BigDecimal.ZERO, "n/a"));
    }

    @Test
    void theChecksumIsTheSha256OfTheContent() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                ExportFile.checksum("abc".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void anUnknownEncodingIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> ExportFile.fixedWidth("no-such-charset", "\n"));
    }

    /** A regulator field of uppercase Cyrillic, digits and hyphens (#7925). */
    private static final ExportField CYRILLIC_NAME = ExportField.named("Name")
                                                                .replace("№", "")
                                                                .replace("/", " ")
                                                                .upperCase()
                                                                .allowed("А-ЯЁ 0-9-")
                                                                .stripDisallowed()
                                                                .collapseSpaces()
                                                                .failOnLoss();

    @Test
    void aNameIsReplacedCasedHeldToItsCharactersAndItsSpacesCollapsed() {
        ExportFile file = ExportFile.delimited("UTF-8", "\n", ";", "\"", false);
        file.record("line", new ExportField[] {CYRILLIC_NAME}, "№ 5 Иван/Петров, ООД");
        file.record("line", new ExportField[] {CYRILLIC_NAME}, "ИВАН");

        assertEquals("5 ИВАН ПЕТРОВ ООД\nИВАН\n", new String(file.content(), StandardCharsets.UTF_8));
        // Only the value the normalisation changed is recorded, the data's spelling beside the file's.
        assertEquals(List.of("record 1 [line] field [Name]: [№ 5 Иван/Петров, ООД]" + " -> [5 ИВАН ПЕТРОВ ООД]"), file.getNormalised());
    }

    @Test
    void aLatinValueIsRefusedWhereOnlyCyrillicIsAllowedAndLossFails() {
        ExportFile file = ExportFile.delimited("UTF-8", "\n", ";", "\"", false);
        IllegalArgumentException refused =
                assertThrows(IllegalArgumentException.class, () -> file.record("line", new ExportField[] {CYRILLIC_NAME}, "Acme 5"));
        assertEquals("record 1 [line], field [Name]: [Acme 5] would be written as [5], which changes more than case, punctuation and"
                + " whitespace", refused.getMessage());
    }

    @Test
    void aDisallowedCharacterFailsTheRunUnlessTheFieldStripsOrSpacesIt() {
        ExportField digits = ExportField.named("Code")
                                        .allowed("0-9");
        ExportFile file = ExportFile.delimited("UTF-8", "\n", ";", "\"", false);
        IllegalArgumentException refused =
                assertThrows(IllegalArgumentException.class, () -> file.record("line", new ExportField[] {digits}, "12-34"));
        assertEquals("record 1 [line], field [Code]: [12-34] carries [-], which the field does not allow", refused.getMessage());

        ExportFile spaced = ExportFile.delimited("UTF-8", "\n", ";", "\"", false);
        spaced.record("line", new ExportField[] {digits.spaceDisallowed()}, "12-34");
        assertEquals("12 34\n", new String(spaced.content(), StandardCharsets.UTF_8));
    }

    @Test
    void aNormalisationSurvivesALaterRefinement() {
        ExportFile file = ExportFile.fixedWidth("UTF-8", "\n");
        file.record("line", new ExportField[] {ExportField.named("Name")
                                                          .lowerCase()
                                                          .width(6)},
                "ACME");

        assertEquals("acme  \n", new String(file.content(), StandardCharsets.UTF_8));
    }
}
