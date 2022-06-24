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
package org.eclipse.dirigible.afterburner.core.typescript;

import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.lang.InterruptedException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

class TypescriptCompiler {

    private final Path cwd;

    public TypescriptCompiler(Path cwd) {
        this.cwd = cwd;
    }

    void compile(String filePattern) {
        compileTypeScriptFile(filePattern);
    }

    private void compileTypeScriptFile(String filePattern) {
//        var tscFilesPattern = filePath != null ? filePath.toString() : "**/*.ts";
        var processBuilder = new ProcessBuilder(
                "tsc",
                "--target",
                "es2022",
                "--module",
                "esnext",
                "--strict",
                "--skipLibCheck",
                filePattern
        )
                .directory(cwd.toFile());
//                .redirectErrorStream(true);

        try {
            var process = processBuilder.start();
            var in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new TypescriptCompilationException("Could not run tsc", e);
        }
    }
}