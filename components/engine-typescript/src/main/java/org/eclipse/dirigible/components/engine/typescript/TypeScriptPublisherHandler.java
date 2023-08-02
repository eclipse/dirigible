/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.typescript;

import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeScriptPublisherHandler implements PublisherHandler {
    private final TypeScriptService typeScriptService;

    @Autowired
    public TypeScriptPublisherHandler(TypeScriptService typeScriptService) {
        this.typeScriptService = typeScriptService;
    }

    @Override
    public void afterPublish(String workspaceLocation, String registryLocation, AfterPublishMetadata metadata) {
        if (typeScriptService.shouldCompileTypeScript(metadata.projectName(), metadata.entryPath())) {
            typeScriptService.compileTypeScript(metadata.projectName(), metadata.entryPath());
        }
    }
}
