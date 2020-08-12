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
exports.generate = function(workspaceName, projectName, filePath) {

    if (!filePath.endsWith('.form')) {
        return null;
    }

    var workspaceManager = require("platform/v4/workspace");
    var contents = workspaceManager.getWorkspace(workspaceName)
        .getProject(projectName).getFile(filePath).getContent();

    var bytes = require("io/v4/bytes");
    contents = bytes.byteArrayToText(contents);

    var form = JSON.parse(contents);

    var root = {"test": "test"};

    return JSON.stringify(root);
}