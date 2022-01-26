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
    var sources = [];
    if (parameters && parameters.includeLaunchpad) {
        sources = [{
			location: "/template-application-ui-angular/index.html.template", 
			action: "generate",
			rename: "index.html",
		}, {
			location: "/template-application-ui-angular/ui/resources/templates/menu.html.template", 
			action: "generate",
			start : "[[",
			end : "]]",
			rename: "ui/resources/templates/menu.html"
		}, {
			location: "/template-application-ui-angular/ui/resources/templates/sidebar.html.template", 
			action: "copy",
			rename: "ui/resources/templates/sidebar.html"
		}, {
			location: "/template-application-ui-angular/ui/resources/templates/tiles.html.template", 
			action: "copy",
			rename: "ui/resources/templates/tiles.html"
		}, {
			location: "/template-application-ui-angular/ui/resources/js/message-hub.js.template", 
			action: "copy",
			rename: "ui/resources/js/message-hub.js"
		}, {
			location: "/template-application-ui-angular/ui/resources/js/ui-bootstrap-tpls-0.14.3.min.js.template", 
			action: "copy",
			rename: "ui/resources/js/ui-bootstrap-tpls-0.14.3.min.js"
		}, {
			location: "/template-application-ui-angular/ui/resources/js/ui-core-ng-modules.js.template", 
			action: "generate",
			rename: "ui/resources/js/ui-core-ng-modules.js"
		}, {
			location: "/template-application-ui-angular/ui/resources/js/ui-layout.js.template", 
			action: "generate",
			rename: "ui/resources/js/ui-layout.js"
		}, {
			location: "/template-application-ui-angular/ui/extensions/perspective.extensionpoint.template", 
			action: "generate",
			rename: "ui/extensions/perspective.extensionpoint"
		}, {
			location: "/template-application-ui-angular/ui/extensions/perspective.extension.template", 
			action: "generate",
			rename: "ui/extensions/perspective.extension"
		}, {
			location: "/template-application-ui-angular/ui/extensions/perspective.js.template", 
			action: "generate",
			rename: "ui/extensions/perspective.js"
		}, {
			location: "/template-application-ui-angular/ui/extensions/tiles.extensionpoint.template", 
			action: "generate",
			rename: "ui/extensions/tiles.extensionpoint"
		}];
    }
    return sources;
};
