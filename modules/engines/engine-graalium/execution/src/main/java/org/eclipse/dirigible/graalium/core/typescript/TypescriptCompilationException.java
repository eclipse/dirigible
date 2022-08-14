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
package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.RuntimeException;

/**
 * The Class TypescriptCompilationException.
 */
class TypescriptCompilationException extends RuntimeException {
    
    /**
     * Instantiates a new typescript compilation exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public TypescriptCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}