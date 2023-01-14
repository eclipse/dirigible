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
let transformer = require("ide-entity/template/transform-edm");
let workspaceManager = require("platform/workspace");

let workspace = __context.get('workspace');
let project = __context.get('project');
let path = __context.get('path');

let modelPath = path.replace(".edm", ".model");
let content = transformer.transform(workspace, project, path);

if (content !== null) {
    let bytes = require("io/bytes");
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