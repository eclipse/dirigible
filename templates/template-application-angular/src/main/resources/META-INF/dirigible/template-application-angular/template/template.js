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
var schemaTemplateManager = require("template-application-schema/template/template");
var feedTemplateManager = require("template-application-feed/template/template");
var uiAngularjsTemplateManager = require("template-application-ui-angular/template/template");
var odataTemplateManager = require("template-application-odata/template/template");

exports.getTemplate = function (parameters) {
    let schemaTemplate = schemaTemplateManager.getTemplate(parameters);
    let feedTemplate = feedTemplateManager.getTemplate(parameters);
    let uiAngularjsTemplate = uiAngularjsTemplateManager.getTemplate(parameters);
    let odataTemplate = odataTemplateManager.getTemplate(parameters);

    let templateSources = [];
    templateSources = templateSources.concat(schemaTemplate.sources);
    templateSources = templateSources.concat(feedTemplate.sources);
    templateSources = templateSources.concat(uiAngularjsTemplate.sources);
    templateSources = templateSources.concat(odataTemplate.sources);

    let templateParameters = [];
    templateParameters = templateParameters.concat(schemaTemplate.parameters);
    templateParameters = templateParameters.concat(feedTemplate.parameters);
    templateParameters = templateParameters.concat(uiAngularjsTemplate.parameters);
    templateParameters.push({
        name: "generateOData",
        label: "OData",
        type: "checkbox"
    });
    odataTemplate.parameters.forEach(e => {
        if (e.name === "odataNamespace") {
            e.ui = {
                hide: {
                    property: "generateOData",
                    value: false
                }
            };
        }
    });
    templateParameters = templateParameters.concat(odataTemplate.parameters);

    return {
        name: "Application - Full Stack",
        description: "Application - full stack with a Database Schema, a set of REST Services and an AngularJS User Interfaces",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};