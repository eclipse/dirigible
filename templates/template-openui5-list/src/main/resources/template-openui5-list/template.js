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

exports.getTemplate = function () {
    return {
        name: "OpenUI5 - List",
        description: "OpenUI5 Template - List",
        sources: [
            {
                location: "/template-openui5-list/ui/Component.js",
                rename: "/Component.js",
                action: "copy"
            },
            {
                location: "/template-openui5-list/ui/index.html",
                rename: "/index.html",
                action: "copy"
            },
            {
                location: "/template-openui5-list/ui/manifest.json",
                rename: "/manifest.json",
                action: "copy"
            },
            {
                location: "/template-openui5-list/ui/controller/List.controller.js",
                rename: "/controller/List.controller.js",
                action: "copy"
            },
            {
                location: "/template-openui5-list/ui/mockdata/products.json",
                rename: "/mockdata/products.json",
                action: "copy"
            },
            {
                location: "/template-openui5-list/ui/view/List.view.xml",
                rename: "/view/List.view.xml",
                action: "copy"
            }
        ],
        parameters: []
    };
};