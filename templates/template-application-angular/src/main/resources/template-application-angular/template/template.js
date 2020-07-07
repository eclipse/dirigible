/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

var schemaTemplateManager = require("template-application-schema/template/template");
var feedTemplateManager = require("template-application-feed/template/template");
var uiAngularjsTemplateManager = require("template-application-ui-angular/template/template");

exports.getTemplate = function (parameters) {
	let schemaTemplate = schemaTemplateManager.getTemplate(parameters);
    let feedTemplate = feedTemplateManager.getTemplate(parameters);
    let uiAngularjsTemplate = uiAngularjsTemplateManager.getTemplate(parameters);

    let templateSources = [];
	templateSources = templateSources.concat(schemaTemplate.sources);
    templateSources = templateSources.concat(feedTemplate.sources);
    templateSources = templateSources.concat(uiAngularjsTemplate.sources);

    let templateParameters = [];
	templateParameters = templateParameters.concat(schemaTemplate.parameters);
    templateParameters = templateParameters.concat(feedTemplate.parameters);
    templateParameters = templateParameters.concat(uiAngularjsTemplate.parameters);

    return {
        name: "Full-stack Application (AngularJS)",
        description: "Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};