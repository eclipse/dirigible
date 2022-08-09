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
package org.eclipse.dirigible.engine.js.graalvm.processor;

/**
 * The Class ExecutableFileTypeResolver.
 */
public class ExecutableFileTypeResolver {
    
    /**
     * Resolve file type.
     *
     * @param moduleOrCode the module or code
     * @param isCommonJsModule the is common js module
     * @return the executable file type
     */
    ExecutableFileType resolveFileType(String moduleOrCode, boolean isCommonJsModule) {
        if (moduleOrCode.endsWith(".mjs")) {
            return ExecutableFileType.JAVASCRIPT_ESM;
        } else if (moduleOrCode.endsWith(".ts")) {
            return ExecutableFileType.TYPESCRIPT;
        } else if (isCommonJsModule) {
            return ExecutableFileType.JAVASCRIPT_NODE_CJS;
        }

        return ExecutableFileType.JAVASCRIPT_DIRIGIBLE_CJS;
    }
}
