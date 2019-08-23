/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

exports.getSources = function(parameters) {
	return [{
		location: "/template-application-angular/ui/perspectives/views/report/bar/index.html.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/index.html",
		engine: "velocity",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-angular/ui/perspectives/views/report/bar/controller.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/controller.js",
		engine: "velocity",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-angular/ui/perspectives/views/report/bar/extensions/view.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-angular/ui/perspectives/views/report/bar/extensions/view.extension.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-angular/ui/perspectives/views/report/bar/extensions/menu/item.extension.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension",
		collection: "uiReportBarsModels"
	}, {
		location: "/template-application-angular/ui/perspectives/views/report/bar/extensions/menu/item.js.template", 
		action: "generate",
		rename: "ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js",
		collection: "uiReportBarsModels"
	}];
};
