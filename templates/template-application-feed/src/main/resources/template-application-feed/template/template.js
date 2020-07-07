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

var daoTemplateManager = require("template-application-dao/template/template");

exports.getTemplate = function (parameters) {
    let daoTemplate = daoTemplateManager.getTemplate(parameters);

    let templateSources = [{
        location: "/template-application-feed/feed/entityFeedSynchronizer.js.template",
        action: "generate",
        rename: "feed/{{perspectiveName}}/{{fileName}}FeedSynchronizer.js",
        engine: "velocity",
        collection: "models"
    }, {
        location: "/template-application-feed/feed/entityFeed.job.template",
        action: "generate",
        rename: "feed/{{perspectiveName}}/{{fileName}}Feed.job",
        engine: "velocity",
        collection: "models"
    }];
    templateSources = templateSources.concat(daoTemplate.sources);

    let templateParameters = [];
    templateParameters = templateParameters.concat(daoTemplate.parameters);

    return {
        name: "Application - Feed",
        description: "Application with Feed Entities",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};