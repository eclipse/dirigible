/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.websockets.synchronizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.service.WebsocketProcessor;
import org.eclipse.dirigible.components.websockets.service.WebsocketService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the websocket processor - which the STOMP
 * configuration and controller need to start - and the synchronizer start; a script websocket is
 * registered {@code FAILED} with the missing engine as the reason, and an event for one that is
 * already registered is refused with the same reason.
 */
class WebsocketsWithoutScriptEngineTest {

    private static final String ENDPOINT = "chat";

    private static final String HANDLER = "project/chat.js";

    private final WebsocketService websocketService = mock(WebsocketService.class);

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withBean(WebsocketService.class, () -> websocketService)
                                          .withUserConfiguration(WebsocketProcessor.class, WebsocketsSynchronizer.class);

    @Test
    void aScriptWebsocketIsRegisteredFailedNamingTheMissingEngine() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            WebsocketsSynchronizer synchronizer = context.getBean(WebsocketsSynchronizer.class);
            SynchronizerCallback callback = mock(SynchronizerCallback.class);
            synchronizer.setCallback(callback);
            TopologyWrapper<Websocket> wrapper = new TopologyWrapper<>(websocket(), new HashMap<>(), synchronizer);

            assertThat(synchronizer.completeImpl(wrapper, ArtefactPhase.CREATE)).as("not completed - the pass retries it")
                                                                                .isFalse();
            verify(callback).registerState(eq(synchronizer), eq(wrapper), eq(ArtefactLifecycle.FAILED),
                    argThat((Throwable e) -> e instanceof ScriptEngineUnavailableException && e.getMessage()
                                                                                               .contains(HANDLER)
                            && e.getMessage()
                                .contains("JavaScript engine")));
        });
    }

    @Test
    void anEventForAScriptWebsocketIsRefusedNamingTheMissingEngine() {
        contextRunner.run(context -> {
            when(websocketService.findByEndpoint(ENDPOINT)).thenReturn(websocket());
            Map<Object, Object> event = new HashMap<>();
            event.put("method", "onmessage");

            assertThatThrownBy(() -> context.getBean(WebsocketProcessor.class)
                                            .processEvent(ENDPOINT, event)).hasRootCauseInstanceOf(ScriptEngineUnavailableException.class)
                                                                           .rootCause()
                                                                           .hasMessageContaining(HANDLER)
                                                                           .hasMessageContaining("JavaScript engine");
        });
    }

    private static Websocket websocket() {
        return new Websocket("/project/chat.websocket", "chat", "", ENDPOINT, HANDLER, null);
    }
}
