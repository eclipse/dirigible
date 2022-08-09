/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.process.execution.ProcessExecutor;
import org.eclipse.dirigible.commons.process.execution.output.ProcessResult;
import org.eclipse.dirigible.commons.process.execution.output.OutputsPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The ExecFacade is used to execute command line code.
 */
public class ExecFacade implements IScriptingFacade {

    /**
     * Execute service module.
     *
     * @param command     the command line code
     * @param forAdding   variables to be declared
     * @param forRemoving variables to be removed
     * @return the output of the command
     * @throws ExecutionException the execution exception
     * @throws InterruptedException the interrupted exception
     */

    public static String exec(String command, Map<String, String> forAdding, List<String> forRemoving) throws ExecutionException, InterruptedException {
        Map<String, String> environmentVariablesToUse = createEnvironmentVariables(forAdding, forRemoving);
        ProcessExecutor<OutputsPair> processExecutor = ProcessExecutor.create();
        Future<ProcessResult<OutputsPair>> outputFuture = processExecutor.executeProcess(command, environmentVariablesToUse);
        ProcessResult<OutputsPair> output = outputFuture.get();
        return output.getProcessOutputs().getStandardOutput();
    }

    /**
     * Creates the environment variables.
     *
     * @param forAdding the for adding
     * @param forRemoving the for removing
     * @return the map
     */
    private static Map<String, String> createEnvironmentVariables(Map<String, String> forAdding, List<String> forRemoving) {
        if (forAdding == null) {
            return new ProcessBuilder().environment();
        }

        Map<String, String> environmentVariables = new HashMap<>(forAdding);
        if (forRemoving != null) {
            forRemoving.forEach(environmentVariables.keySet()::remove);
        }

        return environmentVariables;
    }

}
