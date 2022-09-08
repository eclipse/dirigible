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
const generateUtils = require("ide-generate-service/template/generateUtils");
const parameterUtils = require("ide-generate-service/template/parameterUtils");

exports.generate = function (model, parameters) {
    model = JSON.parse(model).model;
    let templateSources = exports.getTemplate(parameters).sources;
    parameterUtils.process(model, parameters)
    return generateUtils.generateFiles(model, parameters, templateSources);
};

exports.getTemplate = function (parameters) {

    return {
        name: "Application - OData",
        description: "Application with a OData",
        extension: "model",
        sources: [{
            location: "/template-application-odata/odata/application.odata.template",
            action: "generate",
            rename: "gen/odata/{{projectName}}.odata",
            engine: "velocity"
        }],
        parameters: [{
            name: "odataNamespace",
            label: "Namespace",
            placeholder: "Namespace for OData API"
        }]
    };
};