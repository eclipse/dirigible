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
    return [
        // Location: "gen/ui/perspective"
        {
            location: "/template-application-ui-angular/ui/perspective/index.html",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/index.html",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.extension",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.js",
            collection: "uiManageModels"
        },
        // Location: "gen/ui/perspective/manage"
        {
            location: "/template-application-ui-angular/ui/perspective/manage/dialog-window/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/controller.js",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/dialog-window/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/index.html",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/dialog-window/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/view.extension",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/dialog-window/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/view.js",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/controller.js",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/index.html",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/tile.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.extension",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/tile.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.js",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.extension",
            collection: "uiManageModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/manage/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.js",
            collection: "uiManageModels"
        }];
};
