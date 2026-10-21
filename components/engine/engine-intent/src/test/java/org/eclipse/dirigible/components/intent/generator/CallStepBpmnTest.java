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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.dirigible.components.intent.generator.bpmn.BpmnIntentGenerator;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * A {@code call:} step in the emitted BPMN - dirigible #8009: a {@code call:} naming a Java class
 * is bound exactly as {@code delegate:} binds it ({@code flowable:class}, built and injected by the
 * client bean container), so a Java application needs no JavaScript engine for it; only a
 * {@code call:} naming a TypeScript/JavaScript handler still emits the {@code ${JSTask}} task, and
 * it is reported to the author as deprecated in favour of {@code delegate:}.
 */
class CallStepBpmnTest {

    private static final String YAML = """
            name: invoicing
            entities:
              - name: Invoice
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: number, type: string }
            processes:
              - name: ArchiveInvoice
                trigger: { onCreate: Invoice }
                steps:
                  - { name: archive, kind: serviceTask, args: { %s } }
                  - { name: done, kind: end }
            """;

    private record Generated(String bpmn, List<String> issues, List<String> advisories) {
    }

    private static Generated generate(String stepArgs) {
        IntentModel model = IntentParser.parse(YAML.formatted(stepArgs));
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
                     .endsWith("/ArchiveInvoice.bpmn")) {
                return new Generated(new String(contents.getAllValues()
                                                        .get(i),
                        StandardCharsets.UTF_8), context.getIssues(), context.getAdvisories());
            }
        }
        throw new AssertionError("the process BPMN was not written; wrote " + paths.getAllValues());
    }

    @Test
    void aCallToAJavaSourceIsBoundLikeADelegate() {
        Generated generated = generate("call: custom/ArchiveInvoiceDelegate.java");

        assertTrue(generated.bpmn()
                            .contains("<serviceTask id=\"archive\" name=\"Archive\" flowable:async=\"true\""
                                    + " flowable:class=\"custom.ArchiveInvoiceDelegate\">"),
                "a Java call must bind the class through flowable:class, got:\n" + generated.bpmn());
        assertFalse(generated.bpmn()
                             .contains("${JSTask}"),
                "a Java call must never emit a JavaScript task, got:\n" + generated.bpmn());
        assertEquals(List.of(), generated.advisories(), "a Java call is not deprecated");
    }

    @Test
    void aCallToAJavaSourceEmitsExactlyWhatTheDelegateEmits() {
        assertEquals(generate("delegate: custom.ArchiveInvoiceDelegate").bpmn(),
                generate("call: custom/ArchiveInvoiceDelegate.java").bpmn(),
                "a Java call must take the delegate path, not a look-alike of it");
        assertEquals(generate("delegate: custom.ArchiveInvoiceDelegate").bpmn(), generate("call: /custom/ArchiveInvoiceDelegate").bpmn(),
                "the leading slash and the .java extension are optional on a Java call");
    }

    @Test
    void aCallToAQualifiedJavaClassIsBoundLikeADelegate() {
        assertEquals(generate("delegate: custom.ArchiveInvoiceDelegate").bpmn(), generate("call: custom.ArchiveInvoiceDelegate").bpmn(),
                "a Java call may name the class the way delegate: does");
    }

    @Test
    void aCallToATypeScriptHandlerKeepsItsJavaScriptTaskAndIsReportedAsDeprecated() {
        Generated generated = generate("call: custom/archive-invoice.ts");

        assertTrue(generated.bpmn()
                            .contains("flowable:delegateExpression=\"${JSTask}\">"),
                "an existing TypeScript call must keep working, got:\n" + generated.bpmn());
        assertTrue(generated.bpmn()
                            .contains("<![CDATA[proj/custom/archive-invoice.ts]]>"),
                "the handler path stays qualified with the project name, got:\n" + generated.bpmn());
        assertEquals(1, generated.advisories()
                                 .size(),
                "the TypeScript call must be reported, got: " + generated.advisories());
        String advisory = generated.advisories()
                                   .get(0);
        assertTrue(
                advisory.contains("[ArchiveInvoice]") && advisory.contains("[archive]") && advisory.contains("custom/archive-invoice.ts")
                        && advisory.contains("deprecated") && advisory.contains("delegate:"),
                "the advisory must name the step and its handler and steer to delegate:, got: " + advisory);
        assertEquals(List.of(), generated.issues(),
                "a deprecation is not an issue: the assistant's repair loop cannot write the Java class that replaces it");
    }

    @Test
    void everyJavaScriptHandlerShapeTheTaskAcceptsIsReportedAsDeprecated() {
        for (String handler : List.of("custom/archive.js", "custom/archive.mjs", "custom/archive.ts/archive",
                "custom/archive.ts/Archiver/run")) {
            Generated generated = generate("call: " + handler);
            assertTrue(generated.bpmn()
                                .contains("${JSTask}"),
                    handler + " is a JavaScript handler and must keep its JavaScript task");
            assertEquals(1, generated.advisories()
                                     .size(),
                    handler + " must be reported as deprecated, got: " + generated.advisories());
        }
    }
}
