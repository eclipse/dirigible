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
package org.eclipse.dirigible.afterburner.core.dirigible.globals;

import org.eclipse.dirigible.afterburner.core.engine.globals.JSGlobalObject;

import java.util.HashMap;
import java.util.Map;

public class DirigibleContextGlobalObject implements JSGlobalObject {

    private final Map<Object, Object> dirigibleContextValue;

    public DirigibleContextGlobalObject(Map<Object, Object> dirigibleContextValue) {
        this.dirigibleContextValue = dirigibleContextValue != null ? dirigibleContextValue : new HashMap<>();
    }

    @Override
    public String getName() {
        return "__context";
    }

    @Override
    public Object getValue() {
        return dirigibleContextValue;
    }
}
