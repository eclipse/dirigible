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
package org.eclipse.dirigible.commons.process.execution;

import org.apache.commons.exec.*;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.dirigible.commons.process.Commandline;
import org.eclipse.dirigible.commons.process.execution.output.OutputsPair;
import org.eclipse.dirigible.commons.process.execution.output.ProcessResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

public abstract class ProcessExecutor<TOut> {

    public static ProcessExecutor<String> createWithErrorsRedirect() {
        return new ErrorsRedirectProcessExecutor();
    }

    public static ProcessExecutor<OutputsPair> create() {
        return new DefaultProcessExecutor();
    }

    public Future<ProcessResult<TOut>> executeProcess(String path, Map<String, String> environmentVariables) {
        try {
            Pair<String, String[]> executableAndArgs = toExecutableAndArgs(path);
            CommandLine commandLine = new CommandLine(executableAndArgs.getLeft());
            commandLine.addArguments(executableAndArgs.getRight(), false);
            DefaultExecutor executor = new DefaultExecutor();
            return executeProcess(commandLine, executor, environmentVariables);
        } catch (Throwable t) {
            throw new ProcessExecutionException(t);
        }
    }

    private static Pair<String, String[]> toExecutableAndArgs(String path) {
        String[] parts = Commandline.translateCommandline(path);
        String executable = parts[0];
        String[] arguments = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[]{};
        return Pair.of(executable, arguments);
    }

    public abstract Future<ProcessResult<TOut>> executeProcess(CommandLine commandLine, DefaultExecutor executor, Map<String, String> environmentVariables) throws IOException;

    protected ProcessExecutionFuture execute(DefaultExecutor executor, CommandLine commandLine, Map<String, String> environmentVariables) throws IOException {
        ProcessExecutionFuture processExecutionFuture = new ProcessExecutionFuture();
        executor.execute(commandLine, environmentVariables, processExecutionFuture);
        return processExecutionFuture;
    }
}
