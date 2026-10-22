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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The {@code .model} the Java transformation derives from an {@code .edm} is byte for byte the one
 * the JavaScript transformation it replaces wrote (#8024).
 *
 * <p>
 * Each {@code edm-model/<name>.model} was captured from the JavaScript transformation: the
 * {@code <name>.edm} beside it saved through the workspace API of an instance still running
 * {@code editor-entity/template/transform-edm.js}, and the {@code .model} it wrote copied as it
 * was. The fixtures are the {@code .edm} files of the integration tests, the {@code .edm} the
 * intent generator writes for the intents of the integration tests (those of
 * {@code EdmModelRoundTripIT} among them), the multi-model billing intents and
 * {@code dirigiblelabs/sample-intent-model}, and hand-written ones for the edges: numbers in their
 * ECMAScript form, key order, string escapes, structured values that are not JSON, relations
 * restating a property, unresolvable business keys, empty and partial models. The three
 * {@code fail-*} fixtures are those the JavaScript wrote no {@code .model} for.
 */
class EdmModelTransformationTest {

    private static final String FIXTURES = "/edm-model";

    private static final Set<String> FAILING = Set.of("fail-not-xml", "fail-properties-attribute", "fail-unique-constraints-attribute");

    @ParameterizedTest
    @MethodSource("transformedFixtures")
    void theModelIsTheOneTheScriptWrote(String name) throws IOException {
        byte[] expected = Files.readAllBytes(fixture(name + ".model"));

        String model = EdmModelTransformation.transform(Files.readString(fixture(name + ".edm"), StandardCharsets.UTF_8));

        assertEquals(new String(expected, StandardCharsets.UTF_8), model);
        assertArrayEquals(expected, model.getBytes(StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @ValueSource(strings = {"fail-not-xml", "fail-properties-attribute", "fail-unique-constraints-attribute"})
    void anEdmTheScriptWroteNoModelForIsRefused(String name) throws IOException {
        String edm = Files.readString(fixture(name + ".edm"), StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> EdmModelTransformation.transform(edm));
    }

    @Test
    void everyFixtureIsCompared() throws IOException {
        List<String> edms = fixtureNames(".edm");

        assertEquals(30, edms.size(), "the captured fixtures");
        for (String name : edms) {
            assertTrue(FAILING.contains(name) || Files.exists(fixture(name + ".model")), "no captured .model for " + name);
        }
    }

    static Stream<String> transformedFixtures() throws IOException {
        return fixtureNames(".model").stream();
    }

    private static List<String> fixtureNames(String extension) throws IOException {
        try (Stream<Path> files = Files.list(fixture(""))) {
            return files.map(path -> path.getFileName()
                                         .toString())
                        .filter(fileName -> fileName.endsWith(extension))
                        .map(fileName -> fileName.substring(0, fileName.length() - extension.length()))
                        .sorted()
                        .toList();
        }
    }

    private static Path fixture(String fileName) {
        try {
            return Path.of(EdmModelTransformationTest.class.getResource(FIXTURES)
                                                           .toURI())
                       .resolve(fileName);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("The fixture folder " + FIXTURES + " has no file system path", e);
        }
    }

}
