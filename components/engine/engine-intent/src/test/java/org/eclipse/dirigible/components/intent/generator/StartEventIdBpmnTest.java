/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dirigible.components.intent.generator.bpmn.BpmnIntentGenerator;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * The generated start event's id never collides with an element id of the process: a step named
 * {@code start} - a natural name - used to give the document two elements with the id
 * {@code start}, which the BPMN schema refuses at deployment (#8058).
 */
class StartEventIdBpmnTest {

    private static final Pattern ID_ATTRIBUTE = Pattern.compile("\\sid=\"([^\"]+)\"");

    private static String bpmn(String firstStep, String secondStep) {
        String yaml = """
                name: collide
                entities:
                  - name: Job
                    fields:
                      - { name: id,   type: integer, primaryKey: true, generated: true }
                      - { name: note, type: string }
                processes:
                  - name: RunJob
                    trigger: { onCreate: Job }
                    steps:
                      - { name: %s, kind: serviceTask, args: { setField: note, value: started } }
                      - { name: %s, kind: serviceTask, args: { setField: note, value: running, next: done } }
                      - { name: done, kind: end }
                """.formatted(firstStep, secondStep);
        IntentModel model = IntentParser.parse(yaml);
        IRepository repository = mock(IRepository.class);
        IResource missing = mock(IResource.class);
        when(repository.getResource(anyString())).thenReturn(missing);
        when(missing.exists()).thenReturn(false);
        IntentGenerationContext context = new IntentGenerationContext(model, "/proj", "proj", "workspace", "app", repository);
        context.setSettings(IntentSettings.scaffold(model));

        new BpmnIntentGenerator().generate(context);

        ArgumentCaptor<String> paths = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> contents = ArgumentCaptor.forClass(byte[].class);
        verify(repository, atLeastOnce()).createResource(paths.capture(), contents.capture());
        for (int i = 0; i < paths.getAllValues()
                                 .size(); i++) {
            if (paths.getAllValues()
                     .get(i)
                     .endsWith("/RunJob.bpmn")) {
                return new String(contents.getAllValues()
                                          .get(i),
                        StandardCharsets.UTF_8);
            }
        }
        throw new AssertionError("the process BPMN was not written; wrote " + paths.getAllValues());
    }

    private static void assertIdsUnique(String bpmn) {
        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        Matcher matcher = ID_ATTRIBUTE.matcher(bpmn);
        while (matcher.find()) {
            if (!seen.add(matcher.group(1))) {
                duplicates.add(matcher.group(1));
            }
        }
        assertEquals(List.of(), duplicates, "every id in the document must be unique:\n" + bpmn);
    }

    private static void assertFlow(String bpmn, String source, String target) {
        assertTrue(bpmn.contains("sourceRef=\"" + source + "\" targetRef=\"" + target + "\""),
                "expected a flow " + source + " -> " + target + " in:\n" + bpmn);
    }

    @Test
    void aStepNamedStartKeepsItsIdAndTheStartEventTakesAnother() {
        String bpmn = bpmn("start", "work");

        assertIdsUnique(bpmn);
        assertTrue(bpmn.contains("<startEvent id=\"startEvent\">"), "the start event must move aside:\n" + bpmn);
        assertTrue(bpmn.contains("<serviceTask id=\"start\""), "the authored step keeps its name as its id:\n" + bpmn);
        assertFlow(bpmn, "startEvent", "start");
        assertFlow(bpmn, "start", "work");
        assertTrue(bpmn.contains("<bpmndi:BPMNShape bpmnElement=\"start\" id=\"BPMNShape_start\">\n        <omgdc:Bounds height=\"80\""),
                "the step is drawn as a task, not as the start event's circle:\n" + bpmn);
    }

    @Test
    void theFallbackIdAlsoMovesAsideWhenAStepClaimsIt() {
        String bpmn = bpmn("start", "startEvent");

        assertIdsUnique(bpmn);
        assertTrue(bpmn.contains("<startEvent id=\"startEvent2\">"), bpmn);
        assertFlow(bpmn, "startEvent2", "start");
    }

    @Test
    void withoutACollisionTheStartEventKeepsItsId() {
        String bpmn = bpmn("begin", "work");

        assertIdsUnique(bpmn);
        assertTrue(bpmn.contains("<startEvent id=\"start\">"), "an unaffected process must stay byte-identical:\n" + bpmn);
        assertFlow(bpmn, "start", "begin");
    }
}
