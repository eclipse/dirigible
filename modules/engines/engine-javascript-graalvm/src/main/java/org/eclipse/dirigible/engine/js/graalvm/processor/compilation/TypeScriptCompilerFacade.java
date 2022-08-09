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
package org.eclipse.dirigible.engine.js.graalvm.processor.compilation;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.repository.api.CaffeineRepositoryCache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * The Class TypeScriptCompilerFacade.
 */
class TypeScriptCompilerFacade {

    /**
     * Handle type script file.
     *
     * @param relativeProjectFilePath the relative project file path
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    void handleTypeScriptFile(String relativeProjectFilePath) throws IOException, InterruptedException {
        String projectPath = "/Users/xxxxxx/target/dirigible/repository/root/registry/public/";
        String filePath = projectPath + relativeProjectFilePath;
        String fileDirectoryPath = StringUtils.substringBeforeLast(filePath, "/");

        createIndexDtsFile(fileDirectoryPath);
        compileTypeScriptFile(filePath);
        invalidateCache(fileDirectoryPath);
    }

    /**
     * Creates the index dts file.
     *
     * @param fileDirectoryPath the file directory path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void createIndexDtsFile(String fileDirectoryPath) throws IOException {
        File indexDtsFile = new File(fileDirectoryPath + "/index.d.ts");
        FileUtils.writeStringToFile(indexDtsFile, "declare const require: any;", StandardCharsets.UTF_8);
    }

    /**
     * Compile type script file.
     *
     * @param filePath the file path
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void compileTypeScriptFile(String filePath) throws InterruptedException, IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] command = new String[]{"tsc index.d.ts ", filePath};
        Process pr = runtime.exec(command);
        pr.waitFor();
    }

    /**
     * Invalidate cache.
     *
     * @param fileDirectoryPath the file directory path
     */
    private void invalidateCache(String fileDirectoryPath) {
        Cache<String, byte[]> cache = CaffeineRepositoryCache.getInternalCache();
        List<String> keys = cache.asMap().keySet().stream()
                .filter(key -> key.startsWith(fileDirectoryPath))
                .collect(toList());

        cache.invalidateAll(keys);
    }
}
