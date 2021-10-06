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
package org.eclipse.dirigible.engine.js.graalvm.processor;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExportGenerator {

    private final IScriptEngineExecutor executor;
    private final String exportPattern = "export const <name_placeholder> = dirigibleRequire('<path_placeholder>');";
    private final String exportDefaultPattern = "export default { <names_list_placeholder> }";
    private final Path apiModuleJsonPath = Paths.get("extensions", "modules.json");

    public ExportGenerator(IScriptEngineExecutor executor) {
        this.executor = executor;
    }

    public String generate(Path path) throws IOException {
        path = resolveApiModuleJson(path);
        ApiModule[] modules = readApiModuleJson(path);

        StringBuilder source = new StringBuilder();
        StringBuilder moduleNames = new StringBuilder();
        for(ApiModule module : modules) {
            source.append(exportPattern
                    .replace("<name_placeholder>", module.getApiName())
                    .replace("<path_placeholder>", module.getName()));
            source.append(System.lineSeparator());
            moduleNames.append(module.getApiName());
            moduleNames.append(',');
        }

        if (moduleNames.length() > 0) {
            moduleNames.setLength(moduleNames.length() - 1);
        }

        source.append(exportDefaultPattern.replace("<names_list_placeholder>", moduleNames.toString()));
        source.append(System.lineSeparator());
        return source.toString();
    }

    private ApiModule[] readApiModuleJson(Path path) throws IOException {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        var module = executor.retrieveModule("/registry/public",
                path.toString().replace(".json", ""), ".json");
        String apiModuleJson = new String(module.getContent(), StandardCharsets.UTF_8);
        return gson.fromJson(apiModuleJson, ApiModule[].class);
    }

    private Path resolveApiModuleJson(Path path) {
        if(path.endsWith("v4")) {
            path = path.getParent();
        }
        return path.resolve(apiModuleJsonPath);
    }
}
