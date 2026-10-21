/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.dirigible.components.ide.workspace.handlers.ExtensionsPublisherHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the workspace service and the publisher's
 * extension hooks start - both used to need the JavaScript service to be constructed at all, which
 * took the Git, template and intent services down with them. A script hook they would run fails
 * with the missing engine as the reason, logged like any failed hook.
 */
class WorkspaceWithoutScriptEngineTest {

    @Test
    void startsWithoutAScriptExecutor() {
        new ApplicationContextRunner().withBean(IRepository.class, () -> mock(IRepository.class))
                                      .withUserConfiguration(WorkspaceService.class, ExtensionsPublisherHandler.class)
                                      .run(context -> {
                                          assertThat(context).hasNotFailed();
                                          assertThat(context).hasSingleBean(WorkspaceService.class);
                                          assertThat(context).hasSingleBean(ExtensionsPublisherHandler.class);
                                      });
    }
}
