/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import { registry } from "@dirigible/platform";
import { extensions } from "@dirigible/extensions";

// Returns the path to d.ts foreach modules.json entry where isPackageDescription = true
let dtsPaths = [];

let apiModulesExtensions = extensions.getExtensions("api-modules");
let extModulesExtensions = extensions.getExtensions("ext-modules");
let apis = apiModulesExtensions.concat(extModulesExtensions);

await apis.forEach(async function (apiModule) {
    let content;
    try {
        let module = await import(`../../../${apiModule}`);
        content = module.getContent();
    } catch (e) {
        // Fallback for not migrated extensions
        let module = require(apiModule);
        content = module.getContent();
    }

    for (let [property, value] of Object.entries(content)) {
        let isPackageDescription = value["isPackageDescription"];
        let shouldBeUnexposedToESM = value["shouldBeUnexposedToESM"];
        if (shouldBeUnexposedToESM) {
            continue;
        }
        if (typeof isPackageDescription === 'boolean' && isPackageDescription === true) {
            dtsPaths.push(value["dtsPath"])
        }
    }
});

export const getDtsPaths = () => {
    return dtsPaths;
}

// Returns the concatenated file content.
export const getDtsFileContents = (paths) => {
    let result = ""
    for (const path of paths) {
        result = result.concat(registry.getText(path) + "\n");
    }

    return result;
}
