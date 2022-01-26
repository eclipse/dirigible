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
package org.eclipse.dirigible.engine.js.graalvm.processor.truffle;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.api.script.Module;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

class StandardPathHandler {

    private final String project;
    private final String root;
    private final IScriptEngineExecutor executor;

    StandardPathHandler(String project, String root, IScriptEngineExecutor executor) {
        this.project = Constants.PATH_SEPARATOR + project;
        this.root = root;
        this.executor = executor;
    }

    Path handlePossibleRepositoryPath(String path) {
        return handlePossibleRepositoryPath(Paths.get(path));
    }

    Path handlePossibleRepositoryPath(Path path) {
        String pathString = path.toString();
        pathString = pathString.replace("\\", "/");

        String maybeDirigibleScope = tryExtractDirigibleScope(pathString);
        if (maybeDirigibleScope != null) {
            return Paths.get(maybeDirigibleScope);
        }

        if (pathString.startsWith(Constants.CURRENT_DIRECTORY)
                || pathString.startsWith(Constants.PARENT_DIRECTORY)) {
            return Path.of(project, pathString);
        }

        if (pathString.startsWith(Constants.PATH_SEPARATOR) || pathString.charAt(1) == ':') {
            return path;
        }

        return Path.of(Constants.PATH_SEPARATOR + pathString);
    }

    String resolve(String pathString) {
        pathString = trimPathExtension(pathString);
        Module module = getModuleFromRepository(root, pathString, executor);
        return new String(module.getContent(), StandardCharsets.UTF_8);
    }

    private String trimPathExtension(String pathString) {
        if(hasExtension(pathString, Constants.JS_EXTENSION)) {
            pathString = pathString.replace(Constants.JS_EXTENSION, "");
        }

        if(hasExtension(pathString, Constants.MJS_EXTENSION)) {
            pathString =  pathString.replace(Constants.MJS_EXTENSION, "");
        }

        return pathString;
    }

    private boolean hasExtension(String path, String extension) {
        return path.toLowerCase().endsWith(extension);
    }

    private Module getModuleFromRepository(String root, String pathString, IScriptEngineExecutor executor) {
        return executor.retrieveModule(root, pathString, Constants.MJS_EXTENSION);
    }

    private String tryExtractDirigibleScope(String pathString) {
        int maybeDirigibleScopeIndex = pathString.indexOf("/@dirigible/");
        if (maybeDirigibleScopeIndex == -1) {
            return null;
        }

        return pathString.substring(maybeDirigibleScopeIndex);
    }
}
