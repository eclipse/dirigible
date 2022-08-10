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
 * The Enum ExecutableFileType.
 */
public enum ExecutableFileType {
    
    /** The javascript esm. */
    JAVASCRIPT_ESM,
    
    /** The javascript node cjs. */
    JAVASCRIPT_NODE_CJS,
    
    /** The javascript dirigible cjs. */
    JAVASCRIPT_DIRIGIBLE_CJS,
    
    /** The typescript. */
    TYPESCRIPT
}
