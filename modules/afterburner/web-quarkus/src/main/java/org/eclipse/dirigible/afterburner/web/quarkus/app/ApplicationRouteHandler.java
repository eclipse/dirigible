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
package org.eclipse.dirigible.afterburner.web.quarkus.app;

import io.vertx.core.http.HttpMethod;
import org.graalvm.polyglot.Value;

public class ApplicationRouteHandler {
    private final Value routeHandler;

    private final HttpMethod routeHttpMethod;

    public ApplicationRouteHandler(Value routeHandler) {
        this(routeHandler, null);
    }

    public ApplicationRouteHandler(Value routeHandler, HttpMethod routeHttpMethod) {
        this.routeHandler = routeHandler;
        this.routeHttpMethod = routeHttpMethod;
    }

    public Value getRouteHandler() {
        return routeHandler;
    }

    public HttpMethod getRouteHttpMethod() {
        return routeHttpMethod;
    }

}
