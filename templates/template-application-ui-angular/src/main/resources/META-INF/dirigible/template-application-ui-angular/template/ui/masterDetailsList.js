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
exports.getSources = function (parameters) {
    var sources = [];
    sources = sources.concat(getMaster(parameters));
    sources = sources.concat(getDetails(parameters));
    return sources;
};

function getMaster(parameters) {
    return [
        // Location: "gen/ui/perspective"
        {
            location: "/template-application-ui-angular/ui/perspective/index.html",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/controller.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/tile.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/tile.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/controller.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/view.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/view.js",
            collection: "uiListMasterModels"
        },
    ];
}

function getDetails(parameters) {
    return [
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/controller.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/index.html",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/view.extension",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/view.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/controller.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/index.html",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/view.extension",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/view.js",
            collection: "uiListDetailsModels"
        }
    ];
}
