/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var registry = require("platform/v4/registry");

exports.getModules = function() {
    let modules = [];
    let javaScriptFiles = registry.find("/", "*.js");

    for (let i = 0; i < javaScriptFiles.length; i ++) {
        let path = javaScriptFiles[i];
        if (isModule(path)) {
            modules.push({
                name: path.replace(".js", "")
            });
        }
    }
    return modules;
}

function isModule(path) {
    let isModule = false;
    if (!isIgnoredModulePath(path)) {
        let code = registry.getText(path);
        isModule = code.search(/exports\.[a-zA-Z0-9_]+ =/g) >= 0;
    }
    return isModule;
}

function isIgnoredModulePath(path) {
    return path.startsWith("ide-") // Ignore IDE packages

        || path.startsWith("indexing/") // Ignore system JavaScript API packages
        || path.startsWith("io/") // Ignore system JavaScript API packages
        || path.startsWith("net/") // Ignore system JavaScript API packages
        || path.startsWith("cms/") // Ignore system JavaScript API packages
        || path.startsWith("mail/") // Ignore system JavaScript API packages
        || path.startsWith("platform/") // Ignore system JavaScript API packages
        || path.startsWith("messaging/") // Ignore system JavaScript API packages
        || path.startsWith("core/") // Ignore system JavaScript API packages
        || path.startsWith("http/") // Ignore system JavaScript API packages
        || path.startsWith("utils/") // Ignore system JavaScript API packages
        || path.startsWith("bpm/") // Ignore system JavaScript API packages
        || path.startsWith("bpm/") // Ignore system JavaScript API packages
        || path.startsWith("log/") // Ignore system JavaScript API packages
        || path.startsWith("db/") // Ignore system JavaScript API packages
        || path.startsWith("security/") // Ignore system JavaScript API packages
        || path.startsWith("security/") // Ignore system JavaScript API packages

        || path.startsWith("template-application-") // Ignore application templates

        || path.endsWith("/extensions/perspective.js") // Ignore generated application UI related components
        || path.endsWith("/extensions/view.js") // Ignore generated application UI related components
        || path.endsWith("/extensions/tile/tile.js") // Ignore generated application UI related components
        || path.endsWith("/extensions/menu/item.js") // Ignore generated application UI related components
        || path.endsWith("/extensions/perspective/perspective.js") // Ignore generated application UI related components
        || path.endsWith("/data/utils/EntityUtils.js") // Ignore generated application Data EntityUtils component
        || path.endsWith("/api/http.js"); // Ignore generated application API http component
}