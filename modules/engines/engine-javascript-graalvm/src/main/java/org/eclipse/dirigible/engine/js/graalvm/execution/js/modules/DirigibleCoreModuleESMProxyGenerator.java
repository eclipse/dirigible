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
package org.eclipse.dirigible.engine.js.graalvm.execution.js.modules;

import com.google.gson.Gson;
import org.eclipse.dirigible.engine.js.graalvm.processor.generation.ApiModule;
import org.eclipse.dirigible.engine.js.graalvm.processor.generation.MultipleMatchingApiPathsException;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirigibleCoreModuleESMProxyGenerator {

    private static final String apiModuleJsonPath = "/extensions/modules.json";

    private static final String NAME_PLACEHOLDER = "<name_placeholder>";
    private static final String PATH_PLACEHOLDER = "<path_placeholder>";
    private static final String NAMES_LIST_PLACEHOLDER = "<names_list_placeholder>";

    private static final String DEFAULT_EXPORT_PATTERN = "export default { " + NAMES_LIST_PLACEHOLDER + " }";
    private static final String EXPORT_PATTERN =
            "export const " + NAME_PLACEHOLDER + " = dirigibleRequire('" + PATH_PLACEHOLDER + "');";

    private final DirigibleCoreModuleProvider dirigibleCoreModuleProvider;

    public DirigibleCoreModuleESMProxyGenerator() {
        dirigibleCoreModuleProvider = new DirigibleCoreModuleProvider();
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
        byte[] apiModuleJsonBytes = dirigibleCoreModuleProvider.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC,
                path.replace(".json", ""), ".json");

        String apiModuleJson = new String(apiModuleJsonBytes, StandardCharsets.UTF_8);
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
