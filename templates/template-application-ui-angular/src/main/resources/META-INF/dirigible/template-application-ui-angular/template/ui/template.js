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
const listTemplate = require("template-application-ui-angular/template/ui/list");
const manageTemplate = require("template-application-ui-angular/template/ui/manage");
const masterDetailsListTemplate = require("template-application-ui-angular/template/ui/masterDetailsList");
const masterDetailsManageTemplate = require("template-application-ui-angular/template/ui/masterDetailsManage");
// const reportBarTemplate = require("template-application-ui-angular/template/ui/reportBar");
// const reportLineTemplate = require("template-application-ui-angular/template/ui/reportLine");
// const reportPieTemplate = require("template-application-ui-angular/template/ui/reportPie");
// const reportTableTemplate = require("template-application-ui-angular/template/ui/reportTable");
// const perspective = require("template-application-ui-angular/template/ui/perspective");
const launchpad = require("template-application-ui-angular/template/ui/launchpad");
// const tiles = require("template-application-ui-angular/template/ui/tiles");
// const menu = require("template-application-ui-angular/template/ui/menu");

exports.getSources = function (parameters) {
    var sources = [];
    sources = sources.concat(launchpad.getSources(parameters));
    sources = sources.concat(listTemplate.getSources(parameters));
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsListTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsManageTemplate.getSources(parameters));
    // sources = sources.concat(reportBarTemplate.getSources(parameters));
    // sources = sources.concat(reportLineTemplate.getSources(parameters));
    // sources = sources.concat(reportPieTemplate.getSources(parameters));
    // sources = sources.concat(reportTableTemplate.getSources(parameters));
    // sources = sources.concat(perspective.getSources(parameters));
    // sources = sources.concat(tiles.getSources(parameters));
    // sources = sources.concat(menu.getSources(parameters));
    return sources;
};
