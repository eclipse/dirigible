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
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/perspective.extension",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/perspective.extension",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/perspective.js",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/perspective.js",
			collection: "uiListModels"
		},
		// Location: "gen/ui/perspective/list"
		{
			location: "/template-application-ui-angular/ui/perspective/list/dialog-window/controller.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/controller.js",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/dialog-window/index.html.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/index.html",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/dialog-window/view.extension",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/view.extension",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/dialog-window/view.js",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/dialog-window/view.js",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/controller.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/controller.js",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/index.html.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/index.html",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/tile.extension",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.extension",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/tile.js",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.js",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/view.extension",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/view.extension",
			collection: "uiListModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/list/view.js",
			action: "generate",
			rename: "gen/ui/{{perspectiveName}}/{{name}}/view.js",
			collection: "uiListModels"
		}];
};
