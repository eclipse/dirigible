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
package org.eclipse.dirigible.engine.js.graalvm.processor.generation;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.api.script.Module;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import com.google.gson.Gson;

public class ExportGenerator {

    private final IScriptEngineExecutor executor;
    private static final String apiModuleJsonPath = "/extensions/modules.json";

    private static final String NAME_PLACEHOLDER = "<name_placeholder>";
    private static final String PATH_PLACEHOLDER = "<path_placeholder>";
    private static final String NAMES_LIST_PLACEHOLDER = "<names_list_placeholder>";

    private static final String DEFAULT_EXPORT_PATTERN = "export default { " + NAMES_LIST_PLACEHOLDER + " }";
    private static final String EXPORT_PATTERN =
            "export const " + NAME_PLACEHOLDER + " = dirigibleRequire('" + PATH_PLACEHOLDER + "');";


    public ExportGenerator(IScriptEngineExecutor executor) {
        this.executor = executor;
    }

    public String generate(String path, String apiVersion) {
        path += apiModuleJsonPath;
        ApiModule[] modules = readApiModuleJson(path);
        StringBuilder source = new StringBuilder();
        StringBuilder moduleNames = new StringBuilder();

        for (ApiModule module : modules) {
            if (module.isPackageDescription() || module.getShouldBeUnexposedToESM()) {
                continue;
            }

            String api = module.getApi();
            String dir = resolvePath(module, apiVersion);

            source.append(EXPORT_PATTERN
                    .replace(NAME_PLACEHOLDER, api)
                    .replace(PATH_PLACEHOLDER, dir));
            source.append(System.lineSeparator());
            moduleNames.append(api);
            moduleNames.append(',');
        }

        if (moduleNames.length() > 0) {
            moduleNames.setLength(moduleNames.length() - 1);
        }

        source.append(DEFAULT_EXPORT_PATTERN.replace(NAMES_LIST_PLACEHOLDER, moduleNames.toString()));
        source.append(System.lineSeparator());
        return source.toString();
    }

    private ApiModule[] readApiModuleJson(String path) {
        Gson gson = new Gson();
        Module module = executor.retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC,
                path.replace(".json", ""), ".json");
        String apiModuleJson = new String(module.getContent(), StandardCharsets.UTF_8);
        return gson.fromJson(apiModuleJson, ApiModule[].class);
    }

    private String resolvePath(ApiModule module, String apiVersion) {
        if (apiVersion.isEmpty()) {
            return module.getPathDefault();
        }

        List<String> foundPaths = Arrays.stream(module.getVersionedPaths())
                .filter(p -> p.contains(apiVersion))
                .collect(Collectors.toList());

        if (foundPaths.size() == 1) {
            return foundPaths.get(0);
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Searching for single api path containing '");
            message.append(apiVersion);
            message.append("' but found: ");
            for (String item : foundPaths) {
                message.append("'");
                message.append(item);
                message.append("' ");
            }
            throw new MultipleMatchingApiPathsException(message.toString());
        }
    }
}
