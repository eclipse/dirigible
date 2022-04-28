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
    if (parameters && parameters.includeLaunchpad) {
        sources = [{
            location: "/template-application-ui-angular/shell/launchpad/menu.js.template",
            action: "generate",
            rename: "gen/shell/launchpad/menu.js"
        }, {
            location: "/template-application-ui-angular/shell/launchpad/perspectives.js.template",
            action: "generate",
            rename: "gen/shell/launchpad/perspectives.js"
        }, {
            location: "/template-application-ui-angular/shell/launchpad/tiles.js.template",
            action: "generate",
            rename: "gen/shell/launchpad/tiles.js"
        }, {
            location: "/template-application-ui-angular/shell/launchpad/views.js.template",
            action: "generate",
            rename: "gen/shell/launchpad/views.js"
        }];
    }
    return sources;
};
