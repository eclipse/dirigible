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
        name: "OpenUI5 - Planning Calendar",
        description: "OpenUI5 Template - Planning Calendar",
        sources: [
            {
                location: "/template-openui5-planning-calendar/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/controller/Page.controller.js",
                rename: "/controller/Page.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/resources/Donna_Moore.jpg",
                rename: "/resources/Donna_Moore.jpg",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/resources/John_Miller.png",
                rename: "/resources/John_Miller.png",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/view/Create.fragment.xml",
                rename: "/view/Create.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/view/Details.fragment.xml",
                rename: "/view/Details.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-planning-calendar/ui/view/Page.view.xml",
                rename: "/view/Page.view.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};