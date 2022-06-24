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
import org.eclipse.dirigible.afterburner.core.CalledFromJS;
import org.graalvm.polyglot.Value;

import java.util.function.BiConsumer;

public class ApplicationRouter {
    private final BiConsumer<String, ApplicationRouteHandler> onRouteRegistered;

    public ApplicationRouter(BiConsumer<String, ApplicationRouteHandler> onRouteRegistered) {
        this.onRouteRegistered = onRouteRegistered;
    }

    @CalledFromJS
    public void route(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void route(String routePath, String routeMethod, Value routeHandler) {
        var httpMethod = HttpMethod.valueOf(routeMethod.toUpperCase());
        var appRoute = new ApplicationRouteHandler(routeHandler, httpMethod);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void get(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.GET);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void put(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.PUT);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void patch(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.PATCH);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void post(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.POST);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void delete(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.DELETE);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void head(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.HEAD);
        onRouteRegistered.accept(routePath, appRoute);
    }

    @CalledFromJS
    public void options(String routePath, Value routeHandler) {
        var appRoute = new ApplicationRouteHandler(routeHandler, HttpMethod.OPTIONS);
        onRouteRegistered.accept(routePath, appRoute);
    }
}