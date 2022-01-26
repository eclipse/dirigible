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
        name: "OpenUI5 - Master Detail",
        description: "OpenUI5 Template - Master Detail",
        sources: [
            {
                location: "/template-openui5-master-detail/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/controller/App.controller.js",
                rename: "/controller/App.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/controller/Detail.controller.js",
                rename: "/controller/Detail.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/controller/DetailDetail.controller.js",
                rename: "/controller/DetailDetail.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/controller/Master.controller.js",
                rename: "/controller/Master.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/mockdata/products.json",
                rename: "/mockdata/products.json",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/view/AboutPage.view.xml",
                rename: "/view/AboutPage.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/view/App.view.xml",
                rename: "/view/App.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/view/Detail.view.xml",
                rename: "/view/Detail.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/view/DetailDetail.view.xml",
                rename: "/view/DetailDetail.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-master-detail/ui/view/Master.view.xml",
                rename: "/view/Master.view.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};