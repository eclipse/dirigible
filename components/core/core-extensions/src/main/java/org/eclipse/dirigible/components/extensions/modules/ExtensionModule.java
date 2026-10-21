/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import java.util.Map;

/**
 * The content of one extension module, read from a {@code .json} module or returned by a script
 * module's export.
 *
 * @param module the module path, as the extension declares it
 * @param exportName the export of a script module that produced the content, or {@code null} for a
 *        JSON module
 * @param content the content, a mutable JSON-shaped map
 */
public record ExtensionModule(String module, String exportName, Map<String, Object> content) {
}
