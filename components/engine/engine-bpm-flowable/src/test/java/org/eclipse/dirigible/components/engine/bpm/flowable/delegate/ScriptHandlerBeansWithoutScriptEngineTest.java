/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.delegate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.components.base.callable.CallableResultAndException;
import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.open.telemetry.OpenTelemetryProvider;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.opentelemetry.api.OpenTelemetry;

/**
 * An instance without the JavaScript engine (#8006): the {@code JSTask} and {@code JSListener}
 * beans still resolve under the names deployed process definitions use, and a step naming a script
 * handler fails with the missing engine as its reason instead of an unresolved expression.
 */
class ScriptHandlerBeansWithoutScriptEngineTest {

    private static final String HANDLER = "/project/task.js";

    private static final String TENANT_ID = "tenant";

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withBean(TenantContext.class, ScriptHandlerBeansWithoutScriptEngineTest::tenantContext)
                                          .withUserConfiguration(DirigibleCallDelegate.class, DirigibleTaskListener.class);

    @Test
    void theBeansResolveUnderTheirDeployedNames() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean("JSTask")).isInstanceOf(DirigibleCallDelegate.class);
            assertThat(context.getBean("JSListener")).isInstanceOf(DirigibleTaskListener.class);
        });
    }

    @Test
    void aScriptTaskFailsNamingTheMissingEngine() {
        contextRunner.run(context -> {
            DirigibleCallDelegate task = context.getBean(DirigibleCallDelegate.class);
            task.setHandler(new FixedValue(HANDLER));
            DelegateExecution execution = mock(DelegateExecution.class);
            when(execution.getTenantId()).thenReturn(TENANT_ID);

            try (MockedStatic<OpenTelemetryProvider> openTelemetry = mockStatic(OpenTelemetryProvider.class)) {
                openTelemetry.when(OpenTelemetryProvider::get)
                             .thenReturn(OpenTelemetry.noop());

                assertThatThrownBy(() -> task.execute(execution)).isInstanceOf(ScriptEngineUnavailableException.class)
                                                                 .hasMessageContaining(HANDLER)
                                                                 .hasMessageContaining("JavaScript engine");
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static TenantContext tenantContext() {
        TenantContext tenantContext = mock(TenantContext.class);
        Tenant tenant = mock(Tenant.class);
        when(tenant.getId()).thenReturn(TENANT_ID);
        when(tenantContext.getCurrentTenant()).thenReturn(tenant);
        try {
            when(tenantContext.execute(eq(TENANT_ID), any(CallableResultAndException.class))).thenAnswer(
                    invocation -> invocation.getArgument(1, CallableResultAndException.class)
                                            .call());
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        return tenantContext;
    }
}
