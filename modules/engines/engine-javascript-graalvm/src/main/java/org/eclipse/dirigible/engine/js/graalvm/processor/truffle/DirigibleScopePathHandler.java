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
package org.eclipse.dirigible.engine.js.graalvm.processor.truffle;

import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.js.graalvm.processor.generation.ExportGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;

class DirigibleScopePathHandler {

    private final ExportGenerator generator;

    DirigibleScopePathHandler(IScriptEngineExecutor executor) {
        this.generator = new ExportGenerator(executor);
    }

    String resolve(Path path) {
        var pathString = path.toString();

        if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_VERSIONED)) {
            return resolveVersionedScopePath(pathString);
        } else if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_DEFAULT)) {
            return resoveDefaultScopePath(pathString);
        }

        return "";  // Not a dirigible scope path, generate nothing!
    }

    private String resolveVersionedScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_VERSIONED.length() - 1);
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        String apiVersion = pathString.split(Constants.PATH_SEPARATOR)[1];
        String apiVersionPath = Constants.PATH_SEPARATOR + apiVersion;
        pathString = pathString.replace(apiVersionPath, "");
        return generator.generate(Paths.get(pathString), apiVersion);
    }

    private String resoveDefaultScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_DEFAULT.length());
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        return generator.generate(Paths.get(pathString), "");
    }
}
