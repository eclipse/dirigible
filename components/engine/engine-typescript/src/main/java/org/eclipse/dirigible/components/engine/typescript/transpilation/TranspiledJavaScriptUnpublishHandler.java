/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.typescript.transpilation;

import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.stereotype.Component;

/**
 * Removes the JavaScript a TypeScript file was transpiled into when the TypeScript file is
 * unpublished: the {@code .js} beside a {@code .ts} in the registry is that file's output, and
 * would otherwise stay served after its source is gone.
 */
@Component
class TranspiledJavaScriptUnpublishHandler implements PublisherHandler {

    private static final String TYPESCRIPT_EXTENSION = ".ts";

    private static final String JAVASCRIPT_EXTENSION = ".js";

    private final IRepository repository;

    TranspiledJavaScriptUnpublishHandler(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterUnpublish(String location) {
        if (!location.endsWith(TYPESCRIPT_EXTENSION)) {
            return;
        }
        String transpiledLocation = location.substring(0, location.length() - TYPESCRIPT_EXTENSION.length()) + JAVASCRIPT_EXTENSION;
        IResource transpiled = repository.getResource(transpiledLocation);
        if (transpiled.exists()) {
            transpiled.delete();
        }
    }

}
