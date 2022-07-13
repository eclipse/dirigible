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
package org.eclipse.dirigible.engine.js.service;

import org.eclipse.dirigible.engine.js.processor.JavascriptEngineProcessor;
import org.eclipse.dirigible.repository.api.IRepository;

public class DefaultJSWebHandler implements JSWebHandler {

    private final JavascriptEngineProcessor processor = new JavascriptEngineProcessor();

    @Override
    public void handleJSRequest(String projectName, String projectFilePath, String projectFilePathParam) {
        String path = projectName + IRepository.SEPARATOR + projectFilePath + IRepository.SEPARATOR + projectFilePathParam;
        processor.executeService(path);
    }
}
