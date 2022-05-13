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
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/index.html.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/index.html",
		engine: "velocity",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/controller.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/controller.js",
		engine: "velocity",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/extensions/view.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/extensions/view.extension.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/extensions/menu/item.extension.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/bar/extensions/menu/item.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js",
		collection: "uiReportBarsModels"
	}];
};
