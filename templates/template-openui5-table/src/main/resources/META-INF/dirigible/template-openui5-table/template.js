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
        name: "OpenUI5 - Table",
        description: "OpenUI5 Template - Table",
        sources: [
            {
                location: "/template-openui5-table/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/controller/Formatter.js",
                rename: "/controller/Formatter.js",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/controller/SettingsDialogController.controller.js",
                rename: "/controller/SettingsDialogController.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/mockdata/products.json",
                rename: "/mockdata/products.json",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/view/FilterDialog.fragment.xml",
                rename: "/view/FilterDialog.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/view/GroupDialog.fragment.xml",
                rename: "/view/GroupDialog.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/view/SettingsDialogView.view.xml",
                rename: "/view/SettingsDialogView.view.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-table/ui/view/SortDialog.fragment.xml",
                rename: "/view/SortDialog.fragment.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};