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
var workspaceManager = require("platform/v4/workspace");
var bytes = require("io/v4/bytes");
var templateEngines = require("platform/v4/template-engines");
var repositoryContent = require("platform/v4/registry");

exports.generateView = function(workspaceName, projectName, filePath) {

    if (!filePath.endsWith('.form')) {
        return null;
    }
    
    var contents = workspaceManager.getWorkspace(workspaceName)
        .getProject(projectName).getFile(filePath).getContent();

    contents = bytes.byteArrayToText(contents);

    var form = JSON.parse(contents);

    var template = repositoryContent.getText("template-form-builder-angularjs/template/angularjs.html.template");
    var segments = filePath.split();
    form.controller = segments[segments.length-1].replace(".form", ".js");
    var result = templateEngines.generate(template, form);

    return result;
}

exports.generateController = function(workspaceName, projectName, filePath) {

    if (!filePath.endsWith('.form')) {
        return null;
    }

    var contents = workspaceManager.getWorkspace(workspaceName)
        .getProject(projectName).getFile(filePath).getContent();

    contents = bytes.byteArrayToText(contents);

    var form = JSON.parse(contents);

    var template = repositoryContent.getText("template-form-builder-angularjs/template/angularjs.js.template");

    var result = templateEngines.generate(template, form);

    return result;
}