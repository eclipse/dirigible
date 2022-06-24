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

import java.lang.UnsupportedOperationException;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.afterburner.core.JavaScriptCodeRunner;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;

public class TypeScriptCodeRunner implements CodeRunner {

    private final JavaScriptCodeRunner codeRunner;

    private TypeScriptCodeRunner(JavaScriptCodeRunner codeRunner) {
        this.codeRunner = codeRunner;
    }

    public static TypeScriptCodeRunner fromExistingCodeRunner(JavaScriptCodeRunner codeRunner) {
        return new TypeScriptCodeRunner(codeRunner);
    }

    @Override
    public Value run(Path codeFilePath) {
        var codeFilePathString = codeFilePath.toString();
        var typescriptCompiler = new TypescriptCompiler(codeRunner.getCurrentWorkingDirectoryPath());
        typescriptCompiler.compile(codeFilePathString);
        var compiledCodeFilePathString = codeFilePathString.replace(".ts", ".js");
        var compiledCodeFilePath = Path.of(compiledCodeFilePathString);
        return codeRunner.run(compiledCodeFilePath);
    }

    @Override
    public Value run(Source codeSource) {
        throw new UnsupportedOperationException("Running Source objects is currently not supported");
    }

    @Override
    public void close() {
        codeRunner.close();
    }
}