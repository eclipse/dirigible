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

import * as registry from "sdk/platform/registry";
import * as xml from "sdk/utils/xml";
import { TemplateEngines } from "sdk/template";
const PDFFacade = Java.type("org.eclipse.dirigible.components.api.pdf.PDFFacade");

const TEMPLATE_PATH_TABLE = "pdf/templates/table.xml";

export function generateTable(data, config) {
    let defaultTemplateParameters = {
        pageWidth: "210",
        pageHeight: "297",
        alignColumns: "center",
        alignRows: "center"
    };
    let templateParameters = {
        ...defaultTemplateParameters,
        ...data
    }
    setTemplateParameters(templateParameters, config);
    let template = registry.getText(TEMPLATE_PATH_TABLE);
    let pdfTemplate = TemplateEngines.generate(template, templateParameters);

    let xmlData = xml.fromJson({
        content: data
    });
    return PDFFacade.generate(pdfTemplate, xmlData);
};

export function generate(templatePath, data) {
    let template = registry.getText(templatePath);

    let xmlData = xml.fromJson({
        content: data
    });
    return PDFFacade.generate(template, xmlData);
};

function setTemplateParameters(templateParameters, config) {
    setDocumentSize(templateParameters, config);
    setDocumentAlign(templateParameters, config);
}

function setDocumentAlign(templateParameters, config) {
    if (config && config.alignColumns) {
        templateParameters.alignColumns = config.alignColumns;
    }
    if (config && config.alignRows) {
        templateParameters.alignRows = config.alignRows;
    }
}

function setDocumentSize(templateParameters, config) {
    if (config && config.size) {
        switch (config.size.toLowerCase()) {
            case "a0":
                templateParameters.pageWidth = "841";
                templateParameters.pageHeight = "1189";
                break;
            case "a1":
                templateParameters.pageWidth = "594";
                templateParameters.pageHeight = "841";
                break;
            case "a2":
                templateParameters.pageWidth = "420";
                templateParameters.pageHeight = "594";
                break;
            case "a3":
                templateParameters.pageWidth = "297";
                templateParameters.pageHeight = "420";
                break;
            case "a4":
                templateParameters.pageWidth = "210";
                templateParameters.pageHeight = "297";
                break;
            case "a5":
                templateParameters.pageWidth = "148";
                templateParameters.pageHeight = "210";
                break;
            case "a6":
                templateParameters.pageWidth = "105";
                templateParameters.pageHeight = "148";
                break;
            case "a7":
                templateParameters.pageWidth = "74";
                templateParameters.pageHeight = "105";
                break;
            case "a8":
                templateParameters.pageWidth = "52";
                templateParameters.pageHeight = "74";
                break;
            case "a9":
                templateParameters.pageWidth = "37";
                templateParameters.pageHeight = "52";
                break;
            case "a10":
                templateParameters.pageWidth = "26";
                templateParameters.pageHeight = "37";
                break;
        }
    }
}