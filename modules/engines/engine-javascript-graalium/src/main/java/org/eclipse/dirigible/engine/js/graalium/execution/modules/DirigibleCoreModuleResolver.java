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
package org.eclipse.dirigible.engine.js.graalium.execution.modules;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirigibleCoreModuleResolver {

    private static final String DIRIGIBLE_CORE_MODULE_SIGNATURE = "@dirigible";
    private static final Pattern DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN = Pattern.compile("(@dirigible)(\\/)(\\w+)"); // e.g. @dirigible/core  => $1=dirigible $2=/ $3=core

    private final DirigibleCoreModuleESMProxyGenerator dirigibleCoreModuleESMProxyGenerator;
    private final Path cacheDirectoryPath;

    public DirigibleCoreModuleResolver(Path cacheDirectoryPath) {
        dirigibleCoreModuleESMProxyGenerator = new DirigibleCoreModuleESMProxyGenerator();
        this.cacheDirectoryPath = cacheDirectoryPath;
    }

    public boolean isCoreModule(String moduleToResolve) {
        return moduleToResolve.contains(DIRIGIBLE_CORE_MODULE_SIGNATURE);
    }

    public Path resolveCoreModulePath(String pathString) {
        Matcher modulePathMatcher = DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN.matcher(pathString);
        if (!modulePathMatcher.matches()) {
            throw new RuntimeException("Found invalid Dirigible core modules path!");
        }

        String coreModuleName = modulePathMatcher.group(3);

        Path coreModuleGeneratedPath = cacheDirectoryPath.resolve(coreModuleName + ".mjs");
        File coreModuleGeneratedFile = coreModuleGeneratedPath.toFile();

        if (coreModuleGeneratedFile.exists()) {
            return coreModuleGeneratedFile.toPath();
        }

        String coreModuleContent = dirigibleCoreModuleESMProxyGenerator.generate(coreModuleName, "");
        try {
            FileUtils.write(coreModuleGeneratedFile, coreModuleContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return coreModuleGeneratedFile.toPath();
    }
}
