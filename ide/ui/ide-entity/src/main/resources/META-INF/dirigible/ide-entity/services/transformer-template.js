/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const transformer = require("ide-entity/template/transform-edm");

exports.generate = function (model, parameters) {
    let workspaceName = parameters.workspaceName;
    let projectName = parameters.projectName;
    let filePath = parameters.filePath;
    let fileName = filePath.substring(0, filePath.indexOf(".edm"));
    return [{
        path: `${fileName}.model`,
        content: transformer.transform(workspaceName, projectName, filePath)
    }]
};

exports.getTemplate = function () {
    let template = {
        "name": "Entity Data to JSON Model Transformer",
        "description": "Model transformer template",
        "extension": "edm",
        "sources": [],
        "parameters": []
    };
    return template;
}