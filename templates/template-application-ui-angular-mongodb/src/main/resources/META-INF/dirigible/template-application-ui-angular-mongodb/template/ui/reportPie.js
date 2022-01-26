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
exports.getSources = function(parameters) {
	return [{
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/index.html.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/index.html",
		engine: "velocity",
		collection: "uiReportPieModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/controller.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/controller.js",
		engine: "velocity",
		collection: "uiReportPieModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/extensions/view.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js",
		collection: "uiReportPieModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/extensions/view.extension.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension",
		collection: "uiReportPieModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/extensions/menu/item.extension.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension",
		collection: "uiReportPieModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/views/report/pie/extensions/menu/item.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js",
		collection: "uiReportPieModels"
	}];
};
