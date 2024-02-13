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
const registry = dirigibleRequire("platform/registry");
const templateEngines = dirigibleRequire("template/engines");

exports.generateFiles = function (model, parameters, templateSources) {
    let generatedFiles = [];

    const models = model.entities.filter(e => e.type !== "REPORT" && e.type !== "FILTER" && e.disableGeneration !== "true");
    const feedModels = model.entities.filter(e => e.feedUrl);

    const reportModels = model.entities.filter(e => e.type === "REPORT");
    const reportFilterModels = model.entities.filter(e => e.type === "FILTER");
    for (const filter of reportFilterModels) {
        const reportModelName = filter.properties.filter(e => e.relationshipType === "ASSOCIATION" && e.relationshipCardinality === "1_1").map(e => e.relationshipEntityName)[0];
        if (reportModelName) {
            for (const model of reportModels) {
                if (model.name === reportModelName) {
                    model.filter = filter;
                    break;
                }
            }
        }
    }

    // UI Basic
    const uiManageModels = model.entities.filter(e => e.layoutType === "MANAGE" && (e.type === "PRIMARY" || e.type === "SETTING"));
    const uiListModels = model.entities.filter(e => e.layoutType === "LIST" && (e.type === "PRIMARY" || e.type === "SETTING"));

    // UI Master-Details
    const uiManageMasterModels = model.entities.filter(e => e.layoutType === "MANAGE_MASTER" && (e.type === "PRIMARY" || e.type === "SETTING"));
    const uiListMasterModels = model.entities.filter(e => e.layoutType === "LIST_MASTER" && (e.type === "PRIMARY" || e.type === "SETTING"));
    const uiManageDetailsModels = model.entities.filter(e => e.layoutType === "MANAGE_DETAILS" && e.type === "DEPENDENT");
    const uiListDetailsModels = model.entities.filter(e => e.layoutType === "LIST_DETAILS" && e.type === "DEPENDENT");

    // UI Reports
    const uiReportTableModels = reportModels.filter(e => e.layoutType === "REPORT_TABLE");
    const uiReportBarsModels = reportModels.filter(e => e.layoutType === "REPORT_BAR");
    const uiReportLinesModels = reportModels.filter(e => e.layoutType === "REPORT_LINE");
    const uiReportPieModels = reportModels.filter(e => e.layoutType === "REPORT_PIE");

    for (let i = 0; i < templateSources.length; i++) {
        const template = templateSources[i];
        const content = registry.getText(template.location);
        if (content == null) {
            throw new Error(`Template file at location '${templateSources[i].location}' does not exists.`)
        }

        if (template.action === "copy") {
            generatedFiles.push({
                content: content,
                path: templateEngines.getMustacheEngine().generate(template.rename, parameters)
            });
        } else if (template.action === "generate") {
            switch (template.collection) {
                case "models":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, models, parameters));
                    break;
                case "reportModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, reportModels, parameters));
                    break;
                case "feedModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, feedModels, parameters));
                    break;
                case "uiManageModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiManageModels, parameters));
                    break;
                case "uiListModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiListModels, parameters));
                    break;
                case "uiManageMasterModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiManageMasterModels, parameters));
                    break;
                case "uiListMasterModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiListMasterModels, parameters));
                    break;
                case "uiManageDetailsModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiManageDetailsModels, parameters));
                    break;
                case "uiListDetailsModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiListDetailsModels, parameters));
                    break;
                case "uiReportTableModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiReportTableModels, parameters));
                    break;
                case "uiReportBarsModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiReportBarsModels, parameters));
                    break;
                case "uiReportLinesModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiReportLinesModels, parameters));
                    break;
                case "uiReportPieModels":
                    generatedFiles = generatedFiles.concat(generateCollection(content, template, uiReportPieModels, parameters));
                    break;
                default:
                    // No collection
                    parameters.models = model.entities;
                    generatedFiles.push({
                        content: getGenerationEngine(template).generate(content, parameters),
                        path: templateEngines.getMustacheEngine().generate(template.rename, parameters)
                    });
                    break;
            }
        }
    }
    return generatedFiles;
}

function generateCollection(content, template, collection, parameters) {
    try {
        const generationEngine = getGenerationEngine(template);
        const generatedFiles = [];
        for (let i = 0; i < collection.length; i++) {
            const templateParameters = {};
            Object.assign(templateParameters, collection[i], parameters);
            // TODO Move this to the more generic "generate()" function, with layoutType === "MANAGE_MASTER" check
            templateParameters.perspectiveViews = templateParameters.perspectives[collection[i].perspectiveName].views;
            if (template.collection === "uiManageMasterModels" || template.collection === "uiListMasterModels") {
                collection.filter(e => e.perspectiveName === collection[i].perspectiveName).forEach(e => templateParameters.perspectiveViews.push(e.name + "-details"));
            }

            generatedFiles.push({
                content: generationEngine.generate(content, templateParameters),
                path: templateEngines.getMustacheEngine().generate(template.rename, templateParameters)
            });
        }
        return generatedFiles;
    } catch (e) {
        const message = `Error occurred while generating template:\n\nError: ${e.message}\n\nTemplate:\n${JSON.stringify(template, null, 2)}\n`;
        console.error(message);
        throw e;
    }
}

function getGenerationEngine(template) {
    let generationEngine = null;
    if (template.engine === "velocity") {
        generationEngine = templateEngines.getVelocityEngine();
    } else {
        generationEngine = templateEngines.getMustacheEngine();
    }

    if (template.sm) {
        generationEngine.setSm(template.sm);
    }
    if (template.em) {
        generationEngine.setEm(template.em);
    }
    return generationEngine;
}