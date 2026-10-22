/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.java.scheduled;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.dirigible.components.jobs.handler.JavaJobExecutor;
import org.eclipse.dirigible.engine.java.component.ComponentContainer;
import org.eclipse.dirigible.sdk.job.JobHandler;
import org.springframework.stereotype.Component;

/**
 * The {@link JavaJobExecutor} SPI implementation: invoked by the jobs engine (through the Quartz
 * {@code JobHandler} + {@code JobExecutionService}) when a scheduled job's engine is
 * {@value JavaJobExecutor#ENGINE_JAVA}. It resolves the CURRENT client bean by the handler FQN from
 * the {@link ComponentContainer} (so a hot-reloaded class is picked up) and runs it - either
 * {@link JobHandler#run()} for the interface style, or the named method for the method style
 * (handler {@code <fqn>#<method>}): a {@code @Scheduled} method, or the bean method a {@code .job}
 * artefact names. A named method declaring a single {@link Map} parameter receives the parameters
 * the job's definition declares, at their values for the run; a method declaring none runs without
 * them. Failures propagate so the jobs engine records the run as FAILED in the job log.
 */
@Component
public class JavaJobExecutorImpl implements JavaJobExecutor {

    private final ComponentContainer componentContainer;

    JavaJobExecutorImpl(ComponentContainer componentContainer) {
        this.componentContainer = componentContainer;
    }

    @Override
    public void execute(String handler, Map<String, String> parameters) throws Exception {
        int hash = handler.indexOf('#');
        String fqn = hash < 0 ? handler : handler.substring(0, hash);
        String methodName = hash < 0 ? null : handler.substring(hash + 1);

        Object bean = componentContainer.instanceOfClassName(fqn)
                                        .orElseThrow(() -> new IllegalStateException(
                                                "No loaded client bean [" + fqn + "] for scheduled Java job [" + handler + "]"));

        if (methodName == null) {
            if (!(bean instanceof JobHandler job)) {
                throw new IllegalStateException("Scheduled Java job bean [" + fqn + "] does not implement JobHandler");
            }
            job.run();
            return;
        }
        Method method = jobMethod(bean.getClass(), methodName, handler);
        method.setAccessible(true);
        if (method.getParameterCount() == 0) {
            method.invoke(bean);
        } else {
            method.invoke(bean, parameters);
        }
    }

    /**
     * The method a {@code <fqn>#<method>} handler names: the variant receiving the job's parameters
     * when the bean declares one, else the no-arg variant.
     */
    private static Method jobMethod(Class<?> type, String name, String handler) {
        Method withoutParameters = null;
        for (Method method : type.getDeclaredMethods()) {
            if (!method.getName()
                       .equals(name)) {
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0] == Map.class) {
                return method;
            }
            if (parameterTypes.length == 0) {
                withoutParameters = method;
            }
        }
        if (withoutParameters == null) {
            throw new IllegalStateException("Scheduled Java job bean [" + type.getName() + "] declares neither " + name + "() nor " + name
                    + "(Map<String, String>) for job [" + handler + "]");
        }
        return withoutParameters;
    }
}
