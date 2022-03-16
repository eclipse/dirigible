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
package org.eclipse.dirigible.engine.js.graalium.execution.polyfills;

public class TimersPolyfill implements JSPolyfill {
    private static final String POLYFILL_PATH_IN_RESOURCES = "/polyfills/timers.js";

    @Override
    public String getSource() {
        return getPolyfillFromResources(POLYFILL_PATH_IN_RESOURCES);
    }

    @Override
    public String getFileName() {
        return POLYFILL_PATH_IN_RESOURCES;
    }
}
