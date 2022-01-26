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
var daoTemplateManager = require("template-application-dao/template/template");

exports.getTemplate = function (parameters) {
    let schemaTemplate = schemaTemplateManager.getTemplate(parameters);
    let daoTemplate = daoTemplateManager.getTemplate(parameters);

    let templateSources = [];
    templateSources = templateSources.concat(schemaTemplate.sources);
    templateSources = templateSources.concat(daoTemplate.sources);

    let templateParameters = [];
    templateParameters = templateParameters.concat(schemaTemplate.parameters);
    templateParameters = templateParameters.concat(daoTemplate.parameters);

    return {
        name: "Application - Data",
        description: "Application with a Database Schema and DAO",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};