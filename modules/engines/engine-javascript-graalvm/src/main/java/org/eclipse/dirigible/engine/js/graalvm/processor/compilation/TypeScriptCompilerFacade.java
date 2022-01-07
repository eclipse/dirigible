/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.processor.compilation;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.repository.api.CaffeineRepositoryCache;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

class TypeScriptCompilerFacade {
    private static final String typescriptPrettyErrorRegex = ":\\d+:\\d+ \\- error TS\\d+: ";
    private static final String typescriptErrorRegex = "\\(\\d+,\\d+\\): error TS\\d+: ";
    private static final String compilationCompleteWithErrorRegex = "Found [^0][0-9]* error[s]?\\. Watching for file changes\\.";
    private static final String compilationCompleteRegex = "(Compilation complete\\. Watching for file changes\\.|Found \\d+ error[s]?\\. Watching for file changes\\.)";
    private static final String compilationStartedRegex = "(Starting compilation in watch mode\\.\\.\\.|File change detected\\. Starting incremental compilation\\.\\.\\.)";
    private static final Map<String, Process> TYPESCRIPT_COMPILER_WATCHER_PROCESSES = new HashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    Future<TypeScriptCompilationState> compileProject(String projectPath) {
        CompletableFuture<TypeScriptCompilationState> compilationStateFuture = new CompletableFuture<>();
        Consumer<TypeScriptCompilationState> onCompilationStateChange = compilationStateFuture::complete;
        compileProject(projectPath, onCompilationStateChange);
        return compilationStateFuture;
    }

    void compileProject(String projectPath, Consumer<TypeScriptCompilationState> onCompilationStateChange) {
        Runnable r = () -> {
            try {
                prepareTypeScriptProject(projectPath);
                startWatcherIfNotStarted(projectPath, onCompilationStateChange);
            } catch (IOException e) {
                e.printStackTrace();
                onCompilationStateChange.accept(TypeScriptCompilationState.FINISHED_WITH_ERROR);
            }
        };

        executor.submit(r);
    }

    private void prepareTypeScriptProject(String fileDirectoryPath) throws IOException {
        createIndexDtsFileIfNotCreated(fileDirectoryPath);
        createTsConfigFileIfNotCreated(fileDirectoryPath);
    }

    private void startWatcherIfNotStarted(String projectPath, Consumer<TypeScriptCompilationState> onCompilationStateChange) throws IOException {
        if (TYPESCRIPT_COMPILER_WATCHER_PROCESSES.containsKey(projectPath)) {
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder("tsc", "index.d.ts", "-w");
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(projectPath));

        Process process = processBuilder.start();
        TYPESCRIPT_COMPILER_WATCHER_PROCESSES.put(projectPath, process);
        InputStream stdout = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

        String line;
        while ((line = reader.readLine()) != null) {
            InternalTypeScriptCompilationState compilationState = tryExtractCompilationState(line);
            if (InternalTypeScriptCompilationState.FINISHED_SUCCESSFULLY.equals(compilationState)) {
                invalidateCache(projectPath);
                onCompilationStateChange.accept(TypeScriptCompilationState.FINISHED_SUCCESSFULLY);
            } else if (InternalTypeScriptCompilationState.FINISHED_WITH_ERROR.equals(compilationState)) {
                onCompilationStateChange.accept(TypeScriptCompilationState.FINISHED_WITH_ERROR);
            }
        }
    }

    private InternalTypeScriptCompilationState tryExtractCompilationState(String input) {
        boolean compilationStarted = input.matches(compilationStartedRegex);

        boolean compilationError = input.matches(compilationCompleteWithErrorRegex) ||
                input.matches(typescriptErrorRegex) ||
                input.matches(typescriptPrettyErrorRegex);

        boolean compilationComplete = input.matches(compilationCompleteRegex);

        if (compilationComplete) {
            return compilationError ? InternalTypeScriptCompilationState.FINISHED_WITH_ERROR : InternalTypeScriptCompilationState.FINISHED_SUCCESSFULLY;
        } else if (compilationStarted) {
            return InternalTypeScriptCompilationState.STARTED;
        } else {
            return InternalTypeScriptCompilationState.UNKNOWN;
        }
    }

    private void createIndexDtsFileIfNotCreated(String fileDirectoryPath) throws IOException {
        File indexDtsFile = new File(fileDirectoryPath + "/index.d.ts");
        if (indexDtsFile.exists()) {
            return;
        }

        String defaultContent = "declare const require: any;";
        FileUtils.writeStringToFile(indexDtsFile, defaultContent, StandardCharsets.UTF_8);
    }

    private void createTsConfigFileIfNotCreated(String fileDirectoryPath) throws IOException {
        File tsConfigFile = new File(fileDirectoryPath + "/tsconfig.json");
        if (tsConfigFile.exists()) {
            return;
        }

        String defaultContent = "{\n" +
                "  \"compilerOptions\": {\n" +
                "    \"target\": \"ES6\",\n" +
                "    \"module\": \"ES2020\",\n" +
                "    \"esModuleInterop\": true,\n" +
                "    \"forceConsistentCasingInFileNames\": true,\n" +
                "    \"strict\": true,\n" +
                "    \"skipLibCheck\": true\n" +
                "  }\n" +
                "}\n";
        FileUtils.writeStringToFile(tsConfigFile, defaultContent, StandardCharsets.UTF_8);
    }

    private void invalidateCache(String fileDirectoryPath) {
        Cache<String, byte[]> cache = CaffeineRepositoryCache.getInternalCache();
        List<String> keys = cache.asMap().keySet().stream()
                .filter(key -> key.startsWith(fileDirectoryPath))
                .collect(toList());

        cache.invalidateAll(keys);
    }

    enum InternalTypeScriptCompilationState {
        STARTED,
        FINISHED_WITH_ERROR,
        FINISHED_SUCCESSFULLY,
        COMPILER_EXITED,
        UNKNOWN
    }

    enum TypeScriptCompilationState {
        FINISHED_SUCCESSFULLY,
        FINISHED_WITH_ERROR
    }
}
