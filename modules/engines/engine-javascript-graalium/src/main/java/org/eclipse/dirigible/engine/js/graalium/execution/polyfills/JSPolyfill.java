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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface JSPolyfill {

    String getSource();

    String getFileName();

    default String getPolyfillFromResources(String polyfillPathInResources) {
        try {
            InputStream polyfill = JSPolyfill.class.getResourceAsStream(polyfillPathInResources);
            if (polyfill == null) {
                throw new IOException("Polyfill '" + polyfillPathInResources + "' not found in resources!");
            }
            return IOUtils.toString(polyfill, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
