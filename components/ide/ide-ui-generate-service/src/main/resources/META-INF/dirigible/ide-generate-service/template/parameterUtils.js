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
exports.process = function (model, parameters) {
    model.entities.forEach(e => {
        if (parameters.dataSource && !e.dataSource) {
            e.dataSource = parameters.dataSource;
        }
        let tablePrefix = parameters.tablePrefix ? parameters.tablePrefix : '';
        if (tablePrefix !== '' && !tablePrefix.endsWith("_")) {
            tablePrefix = `${tablePrefix}_`;
        }
        parameters.tablePrefix = tablePrefix;
        if (e.dataCount) {
            e.dataCount = e.dataCount.replaceAll("${tablePrefix}", parameters.tablePrefix);
        }
        if (e.dataQuery) {
            e.dataQuery = e.dataQuery.replaceAll("${tablePrefix}", parameters.tablePrefix);
        }

        if (e.type === "DEPENDENT" && (e.layoutType === "LIST_DETAILS" || e.layoutType === "MANAGE_DETAILS")) {
            const relationshipEntityName = e.properties.filter(p => p.relationshipType === "COMPOSITION" && p.relationshipCardinality === "1_n").map(p => p.relationshipEntityName)[0];
            if (relationshipEntityName) {
                const projectionEntity = model.entities.filter(entity => entity.name === relationshipEntityName && entity.type === "PROJECTION")[0];
                if (projectionEntity) {
                    e.hasReferencedProjection = true;
                    e.referencedProjectionProjectName = projectionEntity.projectionReferencedModel.split('/')[2];
                    e.referencedProjectionPerspectiveName = projectionEntity.perspectiveName;
                }
            }
        }

        e.properties.forEach(p => {
            p.dataNotNull = p.dataNullable === "false";
            p.dataAutoIncrement = p.dataAutoIncrement === "true";
            p.dataNullable = p.dataNullable === "true";
            p.dataPrimaryKey = p.dataPrimaryKey === "true";
            p.dataUnique = p.dataUnique === "true";
            p.isCalculatedProperty = p.isCalculatedProperty === "true";
            p.widgetIsMajor = p.widgetIsMajor === "true";
            p.widgetLabel = p.widgetLabel ? p.widgetLabel : p.name;

            switch (p.dataType.toUpperCase()) {
                case "TINYINT":
                case "INT1":
                case "SMALLINT":
                case "INT2":
                case "SMALLSERIAL":
                    p.dataTypeJava = "short";
                    p.dataTypeTypescript = "number";
                    break;
                case "MEDIUMINT":
                case "INT3":
                case "INT":
                case "INT4":
                case "INTEGER":
                case "SERIAL":
                    p.dataTypeJava = "int";
                    p.dataTypeTypescript = "number";
                    break;
                case "BIGINT":
                case "INT8":
                case "BIGSERIAL":
                    p.dataTypeJava = "long";
                    p.dataTypeTypescript = "number";
                    break;
                case "DECIMAL":
                case "DEC":
                case "NUMERIC":
                case "FIXED":
                case "DOUBLE":
                case "DOUBLE PRECISION":
                case "REAL":
                    p.dataTypeJava = "double";
                    p.dataTypeTypescript = "number";
                    break;
                case "FLOAT":
                case "MONEY":
                    p.dataTypeJava = "float";
                    p.dataTypeTypescript = "number";
                    break;
                case "CHAR":
                case "ENUM":
                case "INET4":
                case "INET6":
                case "TEXT":
                case "TINYTEXT":
                case "MEDIUMTEXT":
                case "LONGTEXT":
                case "VARCHAR":
                case "LONG VARCHAR":
                case "CHARACTER VARYING":
                case "CHARACTER":
                case "BPCHAR":
                    p.dataTypeJava = "string";
                    p.dataTypeTypescript = "string";
                    break;
                case "DATE":
                    p.dataTypeJava = "date";
                    p.dataTypeTypescript = "Date";
                    break;
                case "TIME":
                case "TIME WITH TIME ZONE":
                    p.dataTypeJava = "time";
                    p.dataTypeTypescript = "Date";
                    break;
                case "DATETIME":
                case "TIMESTAMP":
                case "TIMESTAMP WITH TIME ZONE":
                    p.dataTypeJava = "timestamp";
                    p.dataTypeTypescript = "Date";
                    break;
                case "BOOLEAN":
                    p.dataTypeJava = "boolean";
                    p.dataTypeTypescript = "boolean";
                    break;
                case "NULL":
                    p.dataTypeJava = "null";
                    p.dataTypeTypescript = "null";
                    break;
                default:
                    p.dataTypeTypescript = "unknown";

            }

            if (p.dataPrimaryKey) {
                if (e.primaryKeys === undefined) {
                    e.primaryKeys = [];
                }
                e.primaryKeys.push(p.name);
                e.primaryKeysString = e.primaryKeys.join(", ");
            }
            if (p.relationshipType === "COMPOSITION" && p.relationshipCardinality === "1_n") {
                e.masterEntity = p.relationshipEntityName;
                e.masterEntityId = p.name;
                p.widgetIsMajor = false;
                // e.masterEntityPrimaryKey = model.entities.filter(m => m.name === e.masterEntity)[0].properties.filter(k => k.dataPrimaryKey)[0].name;
            }

            if (p.widgetType == "DROPDOWN") {
                e.hasDropdowns = true;
            }
            if (p.dataTypeTypescript === "string") {
                // TODO minLength is not available in the model and can't be determined
                p.minLength = 0;
                p.maxLength = -1;
                let widgetLength = parseInt(p.widgetLength);
                let dataLength = parseInt(p.dataLength)
                p.maxLength = dataLength > widgetLength ? widgetLength : dataLength;
            } else if (p.dataTypeTypescript === "Date") {
                p.isDateType = true;
                e.hasDates = true;
            }
            p.inputRule = p.widgetPattern ? p.widgetPattern : "";

            if ((e.layoutType === "MANAGE_MASTER" || e.layoutType === "LIST_MASTER") && p.widgetIsMajor) {
                if (e.masterProperties == null) {
                    e.masterProperties = {
                        title: null,
                        properties: []
                    };
                }
                if (!p.dataAutoIncrement) {
                    if (e.masterProperties.title == null) {
                        e.masterProperties.title = p;
                    } else {
                        e.masterProperties.properties.push(p);
                    }
                }
            }
        });
    });

    parameters.perspectives = {};

    model.entities.forEach(e => {
        if (e.perspectiveName) {
            if (parameters.perspectives[e.perspectiveName] == null) {
                parameters.perspectives[e.perspectiveName] = {
                    views: []
                };
            }
            parameters.perspectives[e.perspectiveName].name = e.perspectiveName;
            parameters.perspectives[e.perspectiveName].label = e.perspectiveName;
            parameters.perspectives[e.perspectiveName].order = e.perspectiveOrder;
            parameters.perspectives[e.perspectiveName].icon = e.perspectiveIcon;
            parameters.perspectives[e.perspectiveName].views.push(e.name);
        }
    });
}

exports.getUniqueParameters = function (...parameters) {
    const uniqueTemplateParameters = [];
    const parametersMap = new Map();

    for (const templateParameters of parameters) {
        for (const parameter of templateParameters) {
            parametersMap.set(parameter.name, parameter);
        }
    }

    for (const next of parametersMap.values()) {
        uniqueTemplateParameters.push(next);
    }
    return uniqueTemplateParameters;
}