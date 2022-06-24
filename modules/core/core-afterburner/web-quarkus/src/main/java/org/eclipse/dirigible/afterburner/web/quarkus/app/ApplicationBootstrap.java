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

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.eclipse.dirigible.afterburner.core.JavaScriptCodeRunner;
import org.eclipse.dirigible.afterburner.core.JavaScriptModuleType;
import org.eclipse.dirigible.afterburner.core.engine.modules.java.JavaModuleResolver;
import org.eclipse.dirigible.afterburner.core.typescript.TypeScriptCodeRunner;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ApplicationBootstrap {
    private static final ThreadLocal<ApplicationBootstrap> THREAD_LOCAL_JS_APP = new ThreadLocal<>();
    private final HashMap<String, ApplicationRouteHandler> appRoutes = new HashMap<>();
    private final Path cwd;
    private final Path appEntry;

    private ApplicationBootstrap(Path cwd, Path appEntry) {
        this.cwd = cwd;
        this.appEntry = appEntry;

        var cachePath = cwd.resolve("caches");

        JavaScriptCodeRunner codeRunner = JavaScriptCodeRunner
                .newBuilder(cwd, cachePath)
                .withJSModuleType(JavaScriptModuleType.ESM)
                .addModuleResolver(new JavaModuleResolver(cachePath))
                .addTypeMapping(Value.class, Uni.class, ApplicationBootstrap::toUni)
                .build();

        var runnable = TypeScriptCodeRunner.fromExistingCodeRunner(codeRunner).run(appEntry);
        runnable.invokeMember("app", new ApplicationRouter(appRoutes::put));
    }

    private static <R> Uni<R> toUni(Value value) {
        if (value.isHostObject()) {
            return value.asHostObject();
        }

        var future = new CompletableFuture<R>();
        value.invokeMember("then", (Consumer<R>) future::complete)
                .invokeMember("catch", (Consumer<Throwable>) future::completeExceptionally);
        return Uni.createFrom().future(future);
    }

    public static ApplicationBootstrap forCurrentThread(Path cwd, Path appEntry) {
        var jsApp = THREAD_LOCAL_JS_APP.get();
        if (jsApp == null) {
            jsApp = new ApplicationBootstrap(cwd, appEntry);
            THREAD_LOCAL_JS_APP.set(jsApp);
        }
        return jsApp;
    }

    public void registerJSRoutes(Router quarkusRouter) {
        // route handlers should be populated by the 'app' invocation
        for (var appRoute : appRoutes.entrySet()) {
            var routePath = appRoute.getKey();
            var routeHandler = appRoute.getValue();
            var routeHandlerHttpMethod = routeHandler.getRouteHttpMethod();

            if (routeHandlerHttpMethod == null) {
                quarkusRouter
                        .route(routePath)
                        .handler(BodyHandler.create())
                        .handler(rc -> {
                            forCurrentThread(cwd, appEntry).invokeRouteHandler(routePath, rc);
                        });
            } else {
                quarkusRouter
                        .route(routeHandlerHttpMethod, routePath)
                        .handler(BodyHandler.create())
                        .handler(rc -> {
                            forCurrentThread(cwd, appEntry).invokeRouteHandler(routePath, rc);
                        });
            }
        }
    }

    private void invokeRouteHandler(String routePath, RoutingContext routingContext) {
        ApplicationRouteHandler appRoute = appRoutes.get(routePath);
        appRoute.getRouteHandler().executeVoid(routingContext);
    }
}
