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
const restTemplateManager = require("template-application-rest/template/template");
const uiTemplate = require("template-application-ui-angular/template/ui/template");
const generateUtils = require("ide-generate-service/template/generateUtils");
const parameterUtils = require("ide-generate-service/template/parameterUtils");

exports.generate = function (model, parameters) {
    model = JSON.parse(model).model;
    let templateSources = exports.getTemplate(parameters).sources;
    parameterUtils.process(model, parameters)
    return generateUtils.generateFiles(model, parameters, templateSources);
};

exports.getTemplate = function (parameters) {
    let restTemplate = restTemplateManager.getTemplate(parameters);

    let templateSources = [];
    templateSources = templateSources.concat(restTemplate.sources);
    templateSources = templateSources.concat(uiTemplate.getSources(parameters));

    let templateParameters = getTemplateParameters();
    templateParameters = templateParameters.concat(restTemplate.parameters);

    return {
        name: "Application - UI (AngularJS)",
        description: "Application with UI, REST APIs and DAOs",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};

function getTemplateParameters() {
    return [
        {
            name: "brand",
            label: "Brand",
            placeholder: "Enter Brand"
        },
        {
            name: "brandUrl",
            label: "Brand URL",
            placeholder: "Enter Brand URL"
        },
        {
            name: "title",
            label: "Title",
            placeholder: "Enter Title"
        },
        {
            name: "description",
            label: "Description",
            placeholder: "Enter Description"
        }
    ];
}