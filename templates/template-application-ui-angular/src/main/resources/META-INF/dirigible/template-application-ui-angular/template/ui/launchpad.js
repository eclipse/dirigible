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
		// Location: "gen/index.html"
		{
			location: "/template-application-ui-angular/index.html",
			action: "generate",
			rename: "gen/index.html"
		},
		// Location: "gen/ui/launchpad/Home"
		{
			location: "/template-application-ui-angular/ui/launchpad/Home/controller.js",
			action: "generate",
			rename: "gen/ui/launchpad/Home/controller.js"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/Home/index.html",
			action: "generate",
			rename: "gen/ui/launchpad/Home/index.html",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/Home/tiles.js",
			action: "generate",
			rename: "gen/ui/launchpad/Home/tiles.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/Home/view.extension",
			action: "generate",
			rename: "gen/ui/launchpad/Home/view.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/Home/view.js",
			action: "generate",
			rename: "gen/ui/launchpad/Home/view.js",
			engine: "velocity"
		},
		// Location: "gen/ui/launchpad"
		{
			location: "/template-application-ui-angular/ui/launchpad/dialog-window.extensionpoint",
			action: "generate",
			rename: "gen/ui/launchpad/dialog-window.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/menu-help.extension",
			action: "generate",
			rename: "gen/ui/launchpad/menu-help.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/menu-help.js",
			action: "copy",
			rename: "gen/ui/launchpad/menu-help.js"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/menu.extensionpoint",
			action: "generate",
			rename: "gen/ui/launchpad/menu.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/perspective.extension",
			action: "generate",
			rename: "gen/ui/launchpad/perspective.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/perspective.extensionpoint",
			action: "generate",
			rename: "gen/ui/launchpad/perspective.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/perspective.js",
			action: "generate",
			rename: "gen/ui/launchpad/perspective.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/tile.extensionpoint",
			action: "generate",
			rename: "gen/ui/launchpad/tile.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/launchpad/view.extensionpoint",
			action: "generate",
			rename: "gen/ui/launchpad/view.extensionpoint",
			engine: "velocity"
		}];
};
