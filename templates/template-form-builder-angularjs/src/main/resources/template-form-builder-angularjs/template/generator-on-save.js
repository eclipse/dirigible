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
var generator = require("template-form-builder-angularjs/template/generate-form-angularjs");
var workspaceManager = require("platform/v4/workspace");

var workspace = __context.get('workspace');
var project = __context.get('project');
var path = __context.get('path');

var modelPath = path.replace(".form", ".html");
var content = generator.generate(workspace, project, path);

if (content !== null) {
    var bytes = require("io/v4/bytes");
    input = bytes.textToByteArray(content);

    if (workspaceManager.getWorkspace(workspace)
        .getProject(project).getFile(path).exists()) {
            workspaceManager.getWorkspace(workspace)
                .getProject(project).createFile(modelPath, input);
    } else {
        workspaceManager.getWorkspace(workspace)
            .getProject(project).getFile(modelPath).setContent(input);
    }
}
