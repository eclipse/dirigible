/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.jobs.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

/**
 * Dispatching a job's handler to the engine it declares. The Java branch is what the manual
 * trigger-now path was missing (#6305); the script branch is what an instance without the
 * JavaScript engine must refuse (#8006).
 */
class JobHandlerRunnerTest {

    @SuppressWarnings("unchecked")
    private static <T> ObjectProvider<T> provider(T executor) {
        ObjectProvider<T> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(executor);
        return provider;
    }

    @Test
    void aJavaJobIsRunByTheJavaExecutor() throws Exception {
        JavaJobExecutor executor = mock(JavaJobExecutor.class);

        new JobHandlerRunner(provider(executor), provider(null)).run("app.jobs.CleanupJob", JavaJobExecutor.ENGINE_JAVA);

        verify(executor).execute("app.jobs.CleanupJob");
    }

    @Test
    void aJavaJobWithoutAnExecutorFailsLoudly() {
        JobHandlerRunner runner = new JobHandlerRunner(provider(null), provider(null));

        // Rather than silently doing nothing - the caller logs the run as failed, so the Jobs
        // perspective shows it instead of reporting a run that never happened.
        assertThrows(IllegalStateException.class, () -> runner.run("app.jobs.CleanupJob", JavaJobExecutor.ENGINE_JAVA));
    }

    @Test
    void aJavaScriptJobIsRunByTheScriptExecutorAndNeverReachesTheJavaExecutor() throws Exception {
        JavaJobExecutor javaExecutor = mock(JavaJobExecutor.class);
        JobScriptExecutor scriptExecutor = mock(JobScriptExecutor.class);

        // No engine (a plain .job artefact) means the script executor.
        new JobHandlerRunner(provider(javaExecutor), provider(scriptExecutor)).run("project/job.mjs", null);

        verify(scriptExecutor).execute("project/job.mjs");
        verifyNoInteractions(javaExecutor);
    }

    @Test
    void aJavaScriptJobWithoutAScriptEngineIsRefusedNamingTheEngine() {
        JobHandlerRunner runner = new JobHandlerRunner(provider(mock(JavaJobExecutor.class)), provider(null));

        ScriptEngineUnavailableException refused =
                assertThrows(ScriptEngineUnavailableException.class, () -> runner.run("project/job.mjs", null));

        assertThat(refused.getMessage()).contains("project/job.mjs")
                                        .contains("JavaScript engine");
    }

    @Test
    void onlyAScriptJobNeedsTheScriptEngineToBeScheduled() {
        JobHandlerRunner runner = new JobHandlerRunner(provider(mock(JavaJobExecutor.class)), provider(null));

        assertThrows(ScriptEngineUnavailableException.class, () -> runner.verifyRunnable("project/job.mjs", null));
        assertDoesNotThrow(() -> runner.verifyRunnable("app.jobs.CleanupJob", JavaJobExecutor.ENGINE_JAVA));
    }
}
