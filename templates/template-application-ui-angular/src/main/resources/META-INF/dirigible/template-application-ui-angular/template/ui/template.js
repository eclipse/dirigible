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
var listTemplate = require("template-application-ui-angular/template/ui/list");
var manageTemplate = require("template-application-ui-angular/template/ui/manage");
var masterDetailsListTemplate = require("template-application-ui-angular/template/ui/masterDetailsList");
var masterDetailsManageTemplate = require("template-application-ui-angular/template/ui/masterDetailsManage");
var reportBarTemplate = require("template-application-ui-angular/template/ui/reportBar");
var reportLineTemplate = require("template-application-ui-angular/template/ui/reportLine");
var reportPieTemplate = require("template-application-ui-angular/template/ui/reportPie");
var reportTableTemplate = require("template-application-ui-angular/template/ui/reportTable");
var perspective = require("template-application-ui-angular/template/ui/perspective");
var launchpad = require("template-application-ui-angular/template/ui/launchpad");
var tiles = require("template-application-ui-angular/template/ui/tiles");
var menu = require("template-application-ui-angular/template/ui/menu");

exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(listTemplate.getSources(parameters));
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsListTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsManageTemplate.getSources(parameters));
    sources = sources.concat(reportBarTemplate.getSources(parameters));
    sources = sources.concat(reportLineTemplate.getSources(parameters));
    sources = sources.concat(reportPieTemplate.getSources(parameters));
    sources = sources.concat(reportTableTemplate.getSources(parameters));
    sources = sources.concat(perspective.getSources(parameters));
    sources = sources.concat(launchpad.getSources(parameters));
    sources = sources.concat(tiles.getSources(parameters));
    sources = sources.concat(menu.getSources(parameters));
    return sources;
};
