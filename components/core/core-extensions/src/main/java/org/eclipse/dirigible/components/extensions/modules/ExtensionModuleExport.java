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
 * What a script extension module's export returned.
 *
 * @param name the export that was called, e.g. {@code getPerspective}
 * @param value the object it returned, as JSON-shaped Java values
 */
public record ExtensionModuleExport(String name, Map<String, Object> value) {
}
