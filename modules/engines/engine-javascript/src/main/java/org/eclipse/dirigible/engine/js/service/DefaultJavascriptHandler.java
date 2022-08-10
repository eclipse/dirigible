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

/**
 * The Class DefaultJavascriptHandler.
 */
public class DefaultJavascriptHandler implements JavascriptHandler {

    /** The processor. */
    private final JavascriptEngineProcessor processor = new JavascriptEngineProcessor();

    /**
     * Handle request.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     */
    @Override
    public void handleRequest(String projectName, String projectFilePath, String projectFilePathParam, boolean debug) {
        String path = projectName + IRepository.SEPARATOR + projectFilePath + IRepository.SEPARATOR + projectFilePathParam;
        processor.executeService(path);
    }
}
