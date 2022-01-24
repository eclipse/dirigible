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
        name: "OpenUI5 - Wizard",
        description: "OpenUI5 Template - Wizard",
        sources: [
            {
                location: "/template-openui5-wizard/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-wizard/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-wizard/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-wizard/ui/controller/Wizard.controller.js",
                rename: "/controller/Wizard.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-wizard/ui/view/ReviewPage.fragment.xml",
                rename: "/view/ReviewPage.fragment.xml",
                action: "copy"
            },
            {
                location: "/template-openui5-wizard/ui/view/Wizard.view.xml",
                rename: "/view/Wizard.view.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};