/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible;

import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
class DirigibleConfiguration {

    @Bean
    ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        builder.corePoolSize(8);
        builder.maxPoolSize(10);
        builder.queueCapacity(100);

        ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();

        return executor;
    }

}
