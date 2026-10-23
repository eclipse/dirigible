/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.debug.java.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.debug.java.JavaDebugManager;
import org.eclipse.dirigible.components.ide.debug.java.endpoint.JavaDebugConfigEndpoint;
import org.eclipse.dirigible.components.ide.debug.java.handler.JavaDebugWebSocketHandler;
import org.eclipse.dirigible.components.ide.lsp.java.process.JdtLsManager;
import org.eclipse.dirigible.engine.java.runtime.ClassPathIndex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Guards the {@code DIRIGIBLE_JAVA_LSP_ENABLED} switch from the debugger's side: with it off the
 * language server's manager is absent, and the context must still start, with the debugger
 * answering 503 instead of failing on the manager it runs on (#8056).
 */
class JavaLspSwitchTest {

    private final WebApplicationContextRunner contextRunner =
            new WebApplicationContextRunner().withBean(ClassPathIndex.class, () -> mock(ClassPathIndex.class))
                                             .withUserConfiguration(JdtLsManager.class, JavaDebugManager.class,
                                                     JavaDebugWebSocketHandler.class, JavaDebugWebSocketConfig.class,
                                                     JavaDebugConfigEndpoint.class);

    @Test
    void startsWithTheDebuggerUnavailableWhenTheSwitchIsOff() {
        contextRunner.withPropertyValues("java.lsp.enabled=false")
                     .run(context -> {
                         assertThat(context).hasNotFailed();
                         assertThat(context).doesNotHaveBean(JdtLsManager.class);
                         assertThat(context.getBean(JavaDebugManager.class)
                                           .isAvailable()).isFalse();
                     });
    }

    @Test
    void answersTheConfigWith503WhenTheSwitchIsOff() {
        contextRunner.withPropertyValues("java.lsp.enabled=false")
                     .run(context -> {
                         JavaDebugConfigEndpoint endpoint = context.getBean(JavaDebugConfigEndpoint.class);

                         assertThatThrownBy(endpoint::getConfig).isInstanceOf(ResponseStatusException.class)
                                                                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.SERVICE_UNAVAILABLE)
                                                                .hasFieldOrPropertyWithValue("reason", JavaDebugManager.UNAVAILABLE_REASON);
                     });
    }

    @Test
    void refusesTheDebugHandshakeWith503WhenTheSwitchIsOff() {
        contextRunner.withPropertyValues("java.lsp.enabled=false")
                     .run(context -> {
                         MockHttpServletResponse response = handshake(context);

                         assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
                         assertThat(response.getContentAsString()).isEqualTo(JavaDebugManager.UNAVAILABLE_REASON);
                     });
    }

    @Test
    void makesTheDebuggerAvailableByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(JdtLsManager.class);
            assertThat(context.getBean(JavaDebugManager.class)
                              .isAvailable()).isTrue();
            assertThat(context.getBean(JavaDebugConfigEndpoint.class)
                              .getConfig()
                              .getStatusCode()).isEqualTo(HttpStatus.OK);
        });
    }

    /**
     * Sends a WebSocket upgrade for the debugger through the handler mapping the configuration
     * registered.
     */
    private static MockHttpServletResponse handshake(AssertableWebApplicationContext context) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", BaseEndpoint.PREFIX_ENDPOINT_WEBSOCKETS + "ide/java-debug");
        request.addHeader("Upgrade", "websocket");
        request.addHeader("Connection", "Upgrade");
        request.addHeader("Sec-WebSocket-Version", "13");
        request.addHeader("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestHandler handler = (HttpRequestHandler) context.getBean(HandlerMapping.class)
                                                                 .getHandler(request)
                                                                 .getHandler();
        handler.handleRequest(request, response);
        return response;
    }
}
