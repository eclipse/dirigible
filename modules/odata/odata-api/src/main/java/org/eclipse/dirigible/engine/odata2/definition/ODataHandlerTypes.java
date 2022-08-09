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
package org.eclipse.dirigible.engine.odata2.definition;

/**
 * The Enum ODataHandlerTypes.
 */
public enum ODataHandlerTypes {

    /** The before. */
    before, /** The after. */
 after, /** The on. */
 on, /** The forbid. */
 forbid;

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the o data handler types
     */
    public static ODataHandlerTypes fromValue(String v) {
        return valueOf(v);
    }

}
