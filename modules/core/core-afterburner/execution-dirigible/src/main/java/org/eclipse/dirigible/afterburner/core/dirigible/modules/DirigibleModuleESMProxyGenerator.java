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
package org.eclipse.dirigible.afterburner.core.dirigible.modules;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirigibleModuleESMProxyGenerator {

    private static final String API_MODULES_JSON_PATH = "/extensions/modules.json";

    private static final String NAME_PLACEHOLDER = "<name_placeholder>";
    private static final String PATH_PLACEHOLDER = "<path_placeholder>";
    private static final String NAMES_LIST_PLACEHOLDER = "<names_list_placeholder>";

    private static final String DEFAULT_EXPORT_PATTERN = "export default { " + NAMES_LIST_PLACEHOLDER + " }";
    private static final String EXPORT_PATTERN =
            "export const " + NAME_PLACEHOLDER + " = dirigibleRequire('" + PATH_PLACEHOLDER + "');";
    private final Gson gson = new Gson();
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    public String generate(String path, String apiVersion) {
        DirigibleModule[] modules = readApiModuleJson(path + API_MODULES_JSON_PATH);
        StringBuilder source = new StringBuilder();
        StringBuilder moduleNames = new StringBuilder();

        for (DirigibleModule module : modules) {
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

    private DirigibleModule[] readApiModuleJson(String path) {
        String apiModuleJson = dirigibleSourceProvider.getSource(path);
        return gson.fromJson(apiModuleJson, DirigibleModule[].class);
    }

    private String resolvePath(DirigibleModule module, String apiVersion) {
        if (apiVersion.isEmpty()) {
            return module.getPathDefault();
        }

        List<String> foundPaths = Arrays.stream(module.getVersionedPaths())
                .filter(p -> p.contains(apiVersion))
                .collect(Collectors.toList());

        if (foundPaths.size() != 1) {
            StringBuilder message = new StringBuilder();
            message.append("Searching for single api path containing '");
            message.append(apiVersion);
            message.append("' but found: ");
            for (String foundPath : foundPaths) {
                message.append("'");
                message.append(foundPath);
                message.append("' ");
            }
            throw new RuntimeException(message.toString());
        }

        return foundPaths.get(0);
    }
}
