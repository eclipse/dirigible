/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

/** How badly the reporter is affected. */
public enum IssueSeverity {

    /** A question or a cosmetic problem. */
    LOW,

    /** Something does not work, and there is a way around it. */
    MEDIUM,

    /** Something does not work, and there is no way around it. */
    HIGH,

    /** The reporter cannot work at all. */
    CRITICAL
}
