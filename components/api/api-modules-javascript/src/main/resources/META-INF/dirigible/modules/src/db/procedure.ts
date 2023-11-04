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
/**
 * API Procedure
 *
 */
import * as update from "./update";
import * as database from "./database";

export function create(sql, datasourceName) {
    let parameters = [];
    update.execute(sql, parameters, datasourceName);
};

export function execute(sql, parameters, datasourceName) {
    let result = [];

    let connection = null;
    let callableStatement = null;
    let resultSet = null;

    if (parameters === undefined || parameters === null) {
        parameters = [];
    }
    try {
        let hasMoreResults = false;

        connection = database.getConnection(datasourceName);
        callableStatement = connection.prepareCall(sql);
        let mappedParameters = parameters.map((parameter) => {
            let mappedParameter = {
                value: parameter,
                type: ""
            };
            let parameterType = typeof parameter;
            if (parameterType === "object") {
                mappedParameter = parameter;
            } else if (parameterType === "string") {
                mappedParameter.type = "string";
            } else if (parameterType === "number") {
                mappedParameter.type = parameter % 1 === 0 ? "int" : "double";
            } else {
                throw new Error(`Procedure Call - Unsupported parameter type [${parameterType}]`);
            }
            return mappedParameter;
        });
        for (let i = 0; i < mappedParameters.length; i++) {
            switch (mappedParameters[i].type) {
                case "string":
                    callableStatement.setString(i + 1, mappedParameters[i].value);
                    break;
                case "int":
                case "integer":
                case "number":
                    callableStatement.setInt(i + 1, mappedParameters[i].value);
                    break;
                case "float":
                    callableStatement.setFloat(i + 1, mappedParameters[i].value);
                    break;
                case "double":
                    callableStatement.setDouble(i + 1, mappedParameters[i].value);
                    break;
            }
        }
        resultSet = callableStatement.executeQuery();

        do {
            result.push(JSON.parse(resultSet.toJson()));
            hasMoreResults = callableStatement.getMoreResults();
            if (hasMoreResults) {
                resultSet.close();
                resultSet = callableStatement.getResultSet();
            }
        } while (hasMoreResults)

        callableStatement.close();
    } finally {
        if (resultSet != null) {
            resultSet.close();
        }
        if (callableStatement != null) {
            callableStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
    return result;
};
