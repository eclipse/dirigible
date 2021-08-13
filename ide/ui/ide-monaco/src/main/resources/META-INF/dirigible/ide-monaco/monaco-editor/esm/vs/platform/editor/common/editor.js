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
export var EditorOpenContext;
(function (EditorOpenContext) {
    /**
     * Default: the editor is opening via a programmatic call
     * to the editor service API.
     */
    EditorOpenContext[EditorOpenContext["API"] = 0] = "API";
    /**
     * Indicates that a user action triggered the opening, e.g.
     * via mouse or keyboard use.
     */
    EditorOpenContext[EditorOpenContext["USER"] = 1] = "USER";
})(EditorOpenContext || (EditorOpenContext = {}));
