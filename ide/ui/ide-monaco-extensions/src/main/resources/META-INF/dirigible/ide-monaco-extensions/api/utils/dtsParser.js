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
const registry = require("platform/v4/registry");
const extensions = require("core/v4/extensions");

// Returns the path to d.ts foreach modules.json entry where isPackageDescription = true
exports.getDtsPaths = function () {
    let dtsPaths = [];

    let apiModulesExtensions = extensions.getExtensions("api-modules");
    let extModulesExtensions = extensions.getExtensions("ext-modules");
    let apis = apiModulesExtensions.concat(extModulesExtensions);

    apis.forEach(function (apiModule) {
        let module = require(apiModule);
        let content = module.getContent();

        for (let [property, value] of Object.entries(content)) {
            let isPackageDescription = value["isPackageDescription"];
            let shouldBeUnexposedToESM = value["shouldBeUnexposedToESM"];
            if(shouldBeUnexposedToESM){
                continue;
            }
            if(typeof isPackageDescription === 'boolean' && isPackageDescription === true)
            {
                dtsPaths.push(value["dtsPath"])
            }
        }
    });

    return dtsPaths;
}

// Returns the concatenated file content.
exports.getDtsFileContents = function (paths){
    let result = ""
    for(const path of paths) {
        result = result.concat(registry.getText(path) + "\n");
    }

    return result;
}
