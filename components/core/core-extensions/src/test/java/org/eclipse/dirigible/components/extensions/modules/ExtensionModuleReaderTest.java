/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.service.ExtensionService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * JSON modules are read without a script engine; script modules go through the evaluator, one
 * session per read, and are skipped with a warning where no evaluator is present.
 */
@ExtendWith(MockitoExtension.class)
class ExtensionModuleReaderTest {

    private static final String POINT = "test-point";

    private static final List<String> EXPORTS = List.of("getPerspective");

    @Mock
    private ExtensionService extensionService;

    @Mock
    private IRepository repository;

    @Mock
    private ObjectProvider<ExtensionModuleEvaluator> evaluatorProvider;

    @Mock
    private ExtensionModuleEvaluator evaluator;

    @Mock
    private ExtensionModuleEvaluator.Session session;

    private final ListAppender<ILoggingEvent> log = new ListAppender<>();

    private final Logger logger = (Logger) LoggerFactory.getLogger(ExtensionModuleReader.class);

    @BeforeEach
    void captureLog() {
        log.start();
        logger.addAppender(log);
        logger.setLevel(Level.DEBUG);
    }

    @AfterEach
    void releaseLog() {
        logger.detachAppender(log);
        logger.setLevel(null);
    }

    @Test
    void reads_a_json_module_from_the_registry_without_opening_a_session() {
        extensions("/app/perspective.json");
        resource("app/perspective.json", "{\"id\": \"orders\", \"order\": 10}");

        List<ExtensionModule> modules = reader().read(List.of(POINT), EXPORTS);

        assertEquals(1, modules.size());
        assertEquals(Map.of("id", "orders", "order", 10), modules.get(0)
                                                                 .content());
        assertNull(modules.get(0)
                          .exportName());
        verify(evaluatorProvider, never()).getIfAvailable();
    }

    @Test
    void skips_a_missing_or_malformed_json_module_with_a_warning() {
        extensions("app/missing.json", "app/array.json");
        IResource missing = mock(IResource.class);
        when(repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/app/missing.json")).thenReturn(missing);
        resource("app/array.json", "[1, 2]");

        assertEquals(List.of(), reader().read(List.of(POINT), EXPORTS));
        assertEquals(2, warnings().size());
    }

    @Test
    void evaluates_every_script_module_of_a_read_in_one_session() {
        extensions("app/a.js", "app/b.js", "app/c.js");
        when(evaluatorProvider.getIfAvailable()).thenReturn(evaluator);
        when(evaluator.openSession()).thenReturn(session);
        when(session.evaluate("app/a.js", EXPORTS)).thenReturn(Optional.of(new ExtensionModuleExport("getPerspective", Map.of("id", "a"))));
        when(session.evaluate("app/b.js", EXPORTS)).thenThrow(new IllegalStateException("syntax error"));
        when(session.evaluate("app/c.js", EXPORTS)).thenReturn(Optional.empty());

        List<ExtensionModule> modules = reader().read(List.of(POINT), EXPORTS);

        assertEquals(List.of(new ExtensionModule("app/a.js", "getPerspective", Map.of("id", "a"))), modules);
        verify(evaluator, times(1)).openSession();
        verify(session).close();
        // The failing module carries its throwable; the one without the export is named.
        assertEquals(2, warnings().size());
        assertEquals("syntax error", warnings().get(0)
                                               .getThrowableProxy()
                                               .getMessage());
    }

    @Test
    void skips_a_script_module_where_no_evaluator_is_present_and_warns_once() {
        extensions("app/legacy.js");
        when(evaluatorProvider.getIfAvailable()).thenReturn(null);
        ExtensionModuleReader reader = reader();

        reader.read(List.of(POINT), EXPORTS);
        reader.read(List.of(POINT), EXPORTS);

        assertEquals(1, warnings().size());
        assertTrue(warnings().get(0)
                             .getFormattedMessage()
                             .contains("Convert it to a .json module"));
        assertEquals(1, log.list.stream()
                                .filter(event -> event.getLevel() == Level.DEBUG)
                                .count());
    }

    private ExtensionModuleReader reader() {
        return new ExtensionModuleReader(extensionService, repository, evaluatorProvider);
    }

    private void extensions(String... modules) {
        List<Extension> extensions = Arrays.stream(modules)
                                           .map(module -> {
                                               Extension extension = mock(Extension.class);
                                               when(extension.getModule()).thenReturn(module);
                                               return extension;
                                           })
                                           .toList();
        when(extensionService.findByExtensionPoint(POINT)).thenReturn(extensions);
    }

    private void resource(String module, String content) {
        IResource resource = mock(IResource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getContent()).thenReturn(content.getBytes(StandardCharsets.UTF_8));
        when(repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + module)).thenReturn(resource);
    }

    private List<ILoggingEvent> warnings() {
        return log.list.stream()
                       .filter(event -> event.getLevel() == Level.WARN)
                       .toList();
    }

}
