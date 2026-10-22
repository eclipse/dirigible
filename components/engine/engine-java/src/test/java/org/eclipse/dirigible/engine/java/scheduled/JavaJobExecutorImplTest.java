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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.engine.java.component.ComponentContainer;
import org.eclipse.dirigible.sdk.job.JobHandler;
import org.junit.jupiter.api.Test;

/**
 * Running a client-Java job bean: a method a {@code .job} artefact names receives the parameters
 * the job declares when it asks for them (#8017), and every other shape runs as it did.
 */
class JavaJobExecutorImplTest {

    private static final Map<String, String> PARAMETERS = Map.of("region", "EU");

    private final ComponentContainer componentContainer = mock(ComponentContainer.class);

    private final JavaJobExecutorImpl executor = new JavaJobExecutorImpl(componentContainer);

    @Test
    void aMethodDeclaringAMapReceivesTheParameters() throws Exception {
        Reports reports = register(new Reports());

        executor.execute(Reports.class.getName() + "#daily", PARAMETERS);

        assertThat(reports.received).isEqualTo(PARAMETERS);
    }

    @Test
    void aNoArgMethodStillRuns() throws Exception {
        Reports reports = register(new Reports());

        executor.execute(Reports.class.getName() + "#cleanup", PARAMETERS);

        assertThat(reports.cleanedUp).isTrue();
    }

    @Test
    void theVariantReceivingTheParametersWinsOverTheNoArgOne() throws Exception {
        Overloaded overloaded = register(new Overloaded());

        executor.execute(Overloaded.class.getName() + "#run", PARAMETERS);

        assertThat(overloaded.received).isEqualTo(PARAMETERS);
    }

    @Test
    void aJobHandlerRunsAsBefore() throws Exception {
        Cleanup cleanup = register(new Cleanup());

        executor.execute(Cleanup.class.getName(), Map.of());

        assertThat(cleanup.ran).isTrue();
    }

    @Test
    void aMethodOfAnotherShapeIsRefusedNamingBothAcceptedOnes() {
        register(new Reports());

        assertThatThrownBy(() -> executor.execute(Reports.class.getName() + "#weekly", PARAMETERS))
                                                                                                   .isInstanceOf(
                                                                                                           IllegalStateException.class)
                                                                                                   .hasMessageContaining("weekly()")
                                                                                                   .hasMessageContaining(
                                                                                                           "weekly(Map<String, String>)");
    }

    private <T> T register(T bean) {
        when(componentContainer.instanceOfClassName(bean.getClass()
                                                        .getName())).thenReturn(Optional.of(bean));
        return bean;
    }

    static class Reports {

        Map<String, String> received;

        boolean cleanedUp;

        public void daily(Map<String, String> parameters) {
            received = parameters;
        }

        public void cleanup() {
            cleanedUp = true;
        }

        public void weekly(String region) {
            // a shape a job method cannot have
        }
    }

    static class Overloaded {

        Map<String, String> received;

        public void run() {
            received = Map.of();
        }

        public void run(Map<String, String> parameters) {
            received = parameters;
        }
    }

    static class Cleanup implements JobHandler {

        boolean ran;

        @Override
        public String cron() {
            return "0 0 3 * * ?";
        }

        @Override
        public void run() {
            ran = true;
        }
    }
}
