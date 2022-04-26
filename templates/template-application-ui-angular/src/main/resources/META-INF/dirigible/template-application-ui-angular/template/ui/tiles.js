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
		location: "/template-application-ui-angular/ui/perspectives/extensions/tile/tile.extension.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension",
		collection: "uiPrimaryModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/tile/tile.extension.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension",
		collection: "uiReportModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/tile/tile.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js",
		engine: "velocity",
		collection: "uiPrimaryModels"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/tile/tile.js.template",
		action: "generate",
		rename: "gen/ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js",
		engine: "velocity",
		collection: "uiReportModels"
	}];
};
