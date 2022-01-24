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
exports.getTemplate = function (parameters) {
    return {
        name: "Application - DAO - MongoDB",
        description: "Application with DAO for MongoDB",
        extension: "model",
        sources: [{
            location: "/template-application-dao-mongodb/data/dao/entity.js.template",
            action: "generate",
            rename: "data/dao/{{perspectiveName}}/{{fileName}}.js",
            engine: "velocity",
            collection: "models"
        }, {
            location: "/template-application-dao-mongodb/data/utils/EntityUtils.js.template",
            action: "copy",
            rename: "data/utils/EntityUtils.js"
        }],
        parameters: []
    };
};