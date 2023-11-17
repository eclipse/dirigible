/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.Consumer;
import org.apache.camel.component.platform.http.HttpEndpointModel;

public class DirigibleHttpEndpointModel extends HttpEndpointModel {

    private DirigibleHttpEndpointModel(String uri, String verbs, Consumer consumer) {
        super(patchUri(uri), verbs, consumer);
    }

    public static DirigibleHttpEndpointModel from(HttpEndpointModel model) {
        return new DirigibleHttpEndpointModel(model.getUri(), model.getVerbs(), model.getConsumer());
    }

    private static String patchUri(String uri) {
        String base = "/services/integrations";
        if (uri == null || uri.isEmpty()) {
            return base;
        }
        return base + (uri.startsWith("/") ? uri : "/" + uri);
    }
}
