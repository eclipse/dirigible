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
let request = require("http/v4/request");
let response = require("http/v4/response");
let registry = require("platform/v4/registry");

let id = request.getParameter("id");
if (id) {
    let namedScripts = new Map();

    namedScripts.set(
        "ide-view-js",
        [
            "/jquery/3.6.0/jquery.min.js",
            "/angularjs/1.8.2/angular.min.js",
            "/angularjs/1.8.2/angular-resource.min.js",
            "/angular-aria/1.8.2/angular-aria.min.js",
            "/ide-core/core/message-hub.js",
            "/ide-core/core/ide-message-hub.js",
            "/ide-core/ui/theming.js",
            "/ide-core/ui/widgets.js",
            "/ide-core/ui/view.js",
            "/ide-core/core/uri-builder.js",
        ]
    );

    namedScripts.set(
        "ide-perspective-js",
        [
            "/jquery/3.6.0/jquery.min.js",
            "/angularjs/1.8.2/angular.min.js",
            "/angularjs/1.8.2/angular-resource.min.js",
            "/angular-aria/1.8.2/angular-aria.min.js",
            "/ide-core/core/message-hub.js",
            "/ide-core/core/ide-message-hub.js",
            "/ide-branding/branding.js",
            "/split.js/1.6.5/dist/split.min.js",
            "/ide-core/ui/editors.js",
            "/ide-core/ui/core-modules.js",
            "/ide-core/ui/theming.js",
            "/ide-core/ui/layout.js",
            "/ide-core/ui/widgets.js",
            "/ide-core/core/uri-builder.js",
        ]
    );

    namedScripts.set(
        "file-upload-js",
        [
            "/es5-shim/4.6.7/es5-shim.min.js",
            "/angular-file-upload/2.6.1/dist/angular-file-upload.min.js",
        ]
    );

    namedScripts.set(
        "ide-view-css",
        [
            "/fundamental-styles/0.24.0/dist/fundamental-styles.css",
            "/resources/styles/core.css",
            "/resources/styles/widgets.css",
        ]
    );

    namedScripts.set(
        "ide-perspective-css",
        [
            "/fundamental-styles/0.24.0/dist/fundamental-styles.css",
            "/resources/styles/core.css",
            "/resources/styles/layout.css",
            "/resources/styles/widgets.css",
            "/resources/styles/perspective.css",
        ]
    );

    let namedScript = namedScripts.get(id);
    if (id.endsWith('-js')) response.setContentType("text/javascript;charset=UTF-8");
    else response.setContentType("text/css");
    if (namedScript) {
        namedScript.forEach(function (item) {
            response.println(registry.getText(item));
        });
    } else {
        response.setContentType("text/plain");
        response.println("Script with 'id': " + id + " is not known.");
    }

} else {
    response.setContentType("text/plain");
    response.println("Provide the 'id' parameter of the script");
}

response.flush();
response.close();