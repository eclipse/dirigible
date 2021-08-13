/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import { URI } from '../../../base/common/uri.js';
export var WORKSPACE_EXTENSION = 'code-workspace';
export function isSingleFolderWorkspaceIdentifier(obj) {
    return obj instanceof URI;
}
export function toWorkspaceIdentifier(workspace) {
    if (workspace.configuration) {
        return {
            configPath: workspace.configuration,
            id: workspace.id
        };
    }
    if (workspace.folders.length === 1) {
        return workspace.folders[0].uri;
    }
    // Empty workspace
    return undefined;
}
//#endregion
