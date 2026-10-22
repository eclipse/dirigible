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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.Test;

/**
 * Unpublishing a TypeScript file removes the JavaScript it was transpiled into, and only that.
 */
class TranspiledJavaScriptUnpublishHandlerTest {

    private final IRepository repository = mock(IRepository.class);

    private final TranspiledJavaScriptUnpublishHandler handler = new TranspiledJavaScriptUnpublishHandler(repository);

    @Test
    void theTranspiledJavaScriptOfAnUnpublishedTypeScriptFileIsDeleted() {
        IResource transpiled = resource("/registry/public/app.ts/api/orders.js", true);

        handler.afterUnpublish("/registry/public/app.ts/api/orders.ts");

        verify(transpiled).delete();
    }

    @Test
    void aTypeScriptFileNeverTranspiledLeavesNothingToDelete() {
        IResource transpiled = resource("/registry/public/app/api/orders.js", false);

        handler.afterUnpublish("/registry/public/app/api/orders.ts");

        verify(transpiled, never()).delete();
    }

    @Test
    void anyOtherUnpublishedLocationIsLeftAlone() {
        handler.afterUnpublish("/registry/public/app/api/orders.js");
        handler.afterUnpublish("/registry/public/app");

        verifyNoInteractions(repository);
    }

    private IResource resource(String location, boolean exists) {
        IResource resource = mock(IResource.class);
        when(resource.exists()).thenReturn(exists);
        when(repository.getResource(location)).thenReturn(resource);
        return resource;
    }

}
