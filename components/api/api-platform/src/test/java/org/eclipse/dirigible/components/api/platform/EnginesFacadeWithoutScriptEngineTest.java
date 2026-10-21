/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the engines facade starts - it used to need
 * the JavaScript service to be constructed at all - and running a script through it is refused with
 * the missing engine as the reason.
 */
class EnginesFacadeWithoutScriptEngineTest {

    @Test
    void runningAScriptIsRefusedNamingTheMissingEngine() {
        new ApplicationContextRunner().withUserConfiguration(EnginesFacade.class)
                                      .run(context -> {
                                          assertThat(context).hasNotFailed();

                                          assertThatThrownBy(() -> EnginesFacade.execute("javascript", "project", "script.js", null, null,
                                                  false)).isInstanceOf(ScriptEngineUnavailableException.class)
                                                         .hasMessageContaining("project/script.js")
                                                         .hasMessageContaining("JavaScript engine");
                                      });
    }
}
