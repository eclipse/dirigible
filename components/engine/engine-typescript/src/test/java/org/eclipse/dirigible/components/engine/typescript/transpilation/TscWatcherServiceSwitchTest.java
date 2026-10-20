/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.typescript.transpilation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Guards the {@code DIRIGIBLE_TSC_WATCH_SERVICE_ENABLED} switch: with it off the watcher bean is
 * absent, and the context must still start rather than fail on the publisher handler that restarts
 * the watcher after a publish (#7970).
 */
class TscWatcherServiceSwitchTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withBean(IRepository.class, () -> mock(IRepository.class))
                                          .withUserConfiguration(TscWatcherService.class, TscWatcherServicePublisherHandler.class);

    @Test
    void startsWithoutTheWatcherWhenTheSwitchIsOff() {
        contextRunner.withPropertyValues("DIRIGIBLE_TSC_WATCH_SERVICE_ENABLED=false")
                     .run(context -> {
                         assertThat(context).hasNotFailed();
                         assertThat(context).doesNotHaveBean(TscWatcherService.class);
                         assertThat(context).doesNotHaveBean(TscWatcherServicePublisherHandler.class);
                     });
    }

    @Test
    void registersTheWatcherAndItsPublisherHandlerByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(TscWatcherService.class);
            assertThat(context).hasSingleBean(TscWatcherServicePublisherHandler.class);
        });
    }
}
