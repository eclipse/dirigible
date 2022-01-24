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
        name: "OpenUI5 - Simple Form",
        description: "OpenUI5 Template - Simple Form",
        sources: [
            {
                location: "/template-openui5-simple-form/ui/view/Change.fragment.xml",
                rename: "/view/Change.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/view/Display.fragment.xml",
                rename: "/view/Display.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/controller/Page.controller.js",
                rename: "/controller/Page.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/view/Page.view.xml",
                rename: "/view/Page.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-simple-form/ui/mockdata/supplier.json",
                rename: "/mockdata/supplier.json",
                action: "copy"
            }
        ],
        parameters: [],
        order: 40
    };
};