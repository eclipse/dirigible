/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.websockets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): a websocket client whose handler is a script
 * is refused with the missing engine as the reason, before it connects - where it used to
 * dereference the absent JavaScript service on the first event.
 */
class WebsocketsFacadeWithoutScriptEngineTest {

    private static final String HANDLER = "project/client.js";

    @Test
    void aClientWithAScriptHandlerIsRefusedBeforeItConnects() {
        new ApplicationContextRunner().withUserConfiguration(BeanProvider.class)
                                      .run(context -> assertThatThrownBy(() -> WebsocketsFacade.createWebsocket("ws://localhost:1/stomp",
                                              HANDLER)).isInstanceOf(ScriptEngineUnavailableException.class)
                                                       .hasMessageContaining(HANDLER)
                                                       .hasMessageContaining("JavaScript engine"));
    }
}
