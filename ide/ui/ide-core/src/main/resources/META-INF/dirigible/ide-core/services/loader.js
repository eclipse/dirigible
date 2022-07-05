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
const request = require("http/v4/request");
const response = require("http/v4/response");
const registry = require("platform/v4/registry");
const uuid = require("utils/v4/uuid");

const COOKIE_PREFIX = "DIRIGIBLE.ide-core.loader.";

let scriptId = request.getParameter("id");
if (scriptId) {
    if (isCached(scriptId)) {
        responseNotModified();
    } else {
        processScriptRequest(scriptId);
    }
} else {
    responseBadRequest("Provide the 'id' parameter of the script");
}

response.flush();
response.close();

function setETag(scriptId) {
    let etag = uuid.random();
    response.addCookie({
        'name': getCacheKey(scriptId),
        'value': etag,
        'path': '/',
        'maxAge': 30 * 24 * 60 * 60
    });
    response.setHeader("ETag", etag);
    response.setHeader('Cache-Control', `public, must-revalidate, max-age=${30 * 24 * 60 * 60}`);
}

function getCacheKey(scriptId) {
    return COOKIE_PREFIX + scriptId;
}

function isCached(scriptId) {
    let cookie = null;
    let cookies = request.getCookies();
    if (cookies) {
        cookie = cookies.filter(e => e.name === getCacheKey(scriptId))[0];
    }
    if (cookie) {
        return cookie.value === request.getHeader("If-None-Match");
    }
    return false;
}

function processScriptRequest(scriptId) {
    let locations = getLocations(scriptId);
    if (locations) {
        let contentType = scriptId.endsWith('-js') ? "text/javascript;charset=UTF-8" : "text/css";
        response.setContentType(contentType);

        setETag(scriptId);
        locations.forEach(function (scriptLocation) {
            response.println(registry.getText(scriptLocation));
        });
    } else {
        responseBadRequest("Script with 'id': " + scriptId + " is not known.");
    }
}

function getLocations(scriptId) {
    switch (scriptId) {
        case "ide-view-js":
            return [
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
            ];
        case "ide-perspective-js":
            return [
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
                "/ide-core/ui/widgets.js",
                "/ide-core/ui/view.js",
                "/ide-core/ui/layout.js",
                "/ide-core/core/uri-builder.js",
            ];
        case "file-upload-js":
            return [
                "/es5-shim/4.6.7/es5-shim.min.js",
                "/angular-file-upload/2.6.1/dist/angular-file-upload.min.js",
            ];
        case "ide-view-css":
            return [
                "/fundamental-styles/0.24.0/dist/fundamental-styles.css",
                "/resources/styles/core.css",
                "/resources/styles/widgets.css",
            ];
        case "ide-perspective-css":
            return [
                "/fundamental-styles/0.24.0/dist/fundamental-styles.css",
                "/resources/styles/core.css",
                "/resources/styles/layout.css",
                "/resources/styles/widgets.css",
                "/resources/styles/perspective.css",
            ];
    }
}

function responseNotModified() {
    response.setStatus(response.NOT_MODIFIED);
}

function responseBadRequest(message) {
    response.setContentType("text/plain");
    response.setStatus(response.BAD_REQUEST);
    response.println(message);
}