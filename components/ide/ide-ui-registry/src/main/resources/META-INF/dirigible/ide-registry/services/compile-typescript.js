/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import { workspace as workspaceManager } from "sdk/platform"
import { command, os } from "sdk/platform";

const workspace = __context.get('workspace');
const project = __context.get('project');
const path = __context.get('path');

if (path && path.endsWith(".ts")) {
    const file = workspaceManager
        .getWorkspace(workspace)
        .getProject(project)
        .getFile("tsconfig.json");
    console.log(`On Save for "${path}\n\n${file.getText()}`);
    const filePath = path.substring(1);
    const compileCommand = `tsc --module ESNext --target ES6 --moduleResolution Node --baseUrl ../ --lib ESNext,DOM --types ../modules/types ${filePath}`;
    const workingDirectory = `target/dirigible/repository/root/registry/public/${project}`;
    console.error(`Command: "${compileCommand}"`);
    const commandResult = command.execute(compileCommand, {
        workingDirectory: workingDirectory
    });
    console.error(`Command Result: "${commandResult}"`);
}
