/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.edm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The ECMAScript rules the {@code .model} is written by, against what a JavaScript engine answers.
 */
class EcmaValuesTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|',
            value = {"1|1", "1.5|1.5", "-42.125|-42.125", "-0.0|0", "100|100", "0.1|0.1", "0.30000000000000004|0.30000000000000004",
                    "0.000001|0.000001", "1e-7|1e-7", "1.23e-18|1.23e-18", "1e21|1e+21", "999999999999999900000|999999999999999900000",
                    "9007199254740992|9007199254740992", "12345678901234567890|12345678901234567000", "1.5e300|1.5e+300", "5e-324|5e-324",
                    "1.5e-323|1.5e-323", "1.7976931348623157e308|1.7976931348623157e+308", "12345678.5|12345678.5",
                    "1e15|1000000000000000"})
    void aNumberIsWrittenAsJavaScriptWritesIt(String literal, String expected) {
        assertEquals(expected, EcmaValues.numberToText(Double.parseDouble(literal)));
    }

    @Test
    void theArrayIndicesComeFirstInAscendingOrder() {
        Map<String, Object> object = new LinkedHashMap<>();
        for (String key : List.of("b", "10", "2", "a", "01", "-1", "4294967295", "4294967294", "0")) {
            object.put(key, key);
        }

        assertEquals(List.of("0", "2", "10", "4294967294", "b", "a", "01", "-1", "4294967295"), EcmaValues.ownKeys(object));
    }

    @Test
    void trimRemovesTheJavaScriptWhiteSpaceOnly() {
        assertEquals("Code", EcmaValues.trim(" \t Code ﻿"));
        assertEquals("\u0001Code", EcmaValues.trim(" \u0001Code "));
    }

    @Test
    void truthinessAndStrictEqualityFollowJavaScript() {
        assertFalse(EcmaValues.isTruthy(""));
        assertFalse(EcmaValues.isTruthy(0));
        assertFalse(EcmaValues.isTruthy(EcmaValues.UNDEFINED));
        assertTrue(EcmaValues.isTruthy(Map.of()));
        assertTrue(EcmaValues.strictlyEquals(EcmaValues.UNDEFINED, EcmaValues.UNDEFINED));
        assertFalse(EcmaValues.strictlyEquals(null, EcmaValues.UNDEFINED));
        assertTrue(EcmaValues.strictlyEquals(1, 1.0));
        assertFalse(EcmaValues.strictlyEquals("1", 1));
    }

}
