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
var daoTemplateManager = require("template-application-dao-mongodb/template/template");

exports.getTemplate = function (parameters) {
    let daoTemplate = daoTemplateManager.getTemplate(parameters);

    let templateSources = [{
        location: "/template-application-rest/api/http.js.template",
        action: "copy",
        rename: "api/http.js",
    }, {
        location: "/template-application-rest/api/entity.js.template",
        action: "generate",
        rename: "api/{{perspectiveName}}/{{fileName}}.js",
        engine: "velocity",
        collection: "models"
    }];
    templateSources = templateSources.concat(daoTemplate.sources);

    let templateParameters = [];
    templateParameters = templateParameters.concat(daoTemplate.parameters);

    return {
        name: "Application - REST - MongoDB",
        description: "Application with REST APIs",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};