/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * The JavaScript engine counts as deployed exactly when a script executor is (#8025).
 */
class ScriptEngineAvailabilityTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(ScriptEngineAvailability.class);

    @Test
    void isAvailableWhereAScriptExecutorIsDeployed() {
        contextRunner.withBean(ScriptExecutor.class, () -> mock(ScriptExecutor.class))
                     .run(context -> assertThat(context.getBean(ScriptEngineAvailability.class)
                                                       .isAvailable()).isTrue());
    }

    @Test
    void isNotAvailableWithoutAScriptExecutor() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(ScriptEngineAvailability.class)
                              .isAvailable()).isFalse();
        });
    }
}
