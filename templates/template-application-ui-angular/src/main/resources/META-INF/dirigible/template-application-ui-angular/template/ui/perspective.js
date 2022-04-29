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
	return [{
		location: "/template-application-ui-angular/ui/perspectives/index.html.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/index.html",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/perspective/perspective.extension.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/extensions/perspective/perspective.extension",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/perspective/perspective.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/extensions/perspective/perspective.js",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/view.extensionpoint.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/extensions/view.extensionpoint",
		engine: "velocity",
		collection: "uiPerspectives"
	}];
};
