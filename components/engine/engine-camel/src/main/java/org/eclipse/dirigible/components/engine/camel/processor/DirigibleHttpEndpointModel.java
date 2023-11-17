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

import java.nio.file.Path;

public class DirigibleHttpEndpointModel extends HttpEndpointModel {

    public DirigibleHttpEndpointModel(String uri, String verbs, Consumer consumer) {
        super(patchUri(uri), verbs, consumer);
    }

    public static DirigibleHttpEndpointModel from(HttpEndpointModel model) {
        return new DirigibleHttpEndpointModel(model.getUri(), model.getVerbs(), model.getConsumer());
    }

    private static String patchUri(String uri) {
        return Path.of("/services/integrations", uri)
                   .toString();
    }
}
