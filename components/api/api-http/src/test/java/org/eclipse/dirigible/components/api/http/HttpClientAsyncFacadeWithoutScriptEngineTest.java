/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the asynchronous HTTP client facade starts -
 * it used to need the JavaScript service to be constructed at all. A callback script it would run
 * fails with the missing engine as the reason, which the facade logs like any failed callback.
 */
class HttpClientAsyncFacadeWithoutScriptEngineTest {

    @Test
    void startsWithoutAScriptExecutor() {
        new ApplicationContextRunner().withUserConfiguration(HttpClientAsyncFacade.class)
                                      .run(context -> {
                                          assertThat(context).hasNotFailed();
                                          assertThat(context).hasSingleBean(HttpClientAsyncFacade.class);
                                      });
    }
}
