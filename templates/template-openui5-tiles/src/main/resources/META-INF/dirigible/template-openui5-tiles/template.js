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
exports.getTemplate = function () {
    return {
        name: "OpenUI5 - Tiles",
        description: "OpenUI5 Template - Tiles",
        sources: [
            {
                location: "/template-openui5-tiles/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/tiles.json",
                rename: "/tiles.json",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/controller/Page.controller.js",
                rename: "/controller/Page.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/css/style.css",
                rename: "/css/style.css",
                action: "copy"
            },
            {
                location: "/template-openui5-tiles/ui/view/Page.view.xml",
                rename: "/view/Page.view.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};