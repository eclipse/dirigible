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
package org.eclipse.dirigible.vertx.server;

import java.io.IOException;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.runtime.core.embed.EmbeddedDirigible;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {
	
	private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
	
	private static final String DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE = "DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE";
	
	private static final String DIRIGIBLE_VERTX_PORT = "DIRIGIBLE_VERTX_PORT";
	
	private static final int VERTX_HTTP_PORT = 8888;
	
	static EmbeddedDirigible DIRIGIBLE;

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		// Initialize Dirigible instance
		DIRIGIBLE = new EmbeddedDirigible();
		DirigibleInitializer initializer = DIRIGIBLE.initialize();
		String workspace = resolveWorkspace();
		loadWorkspace(workspace);
		
		// Create Router
		Router router = Router.router(vertx);
		
		// Register JavaScript Engine Handler
		JavaScriptRequestHandler<RoutingContext> javascriptRequestHandler = new JavaScriptRequestHandler<RoutingContext>();
		Route route = router.route(JavaScriptRequestHandler.ROUTE);
		route.handler(javascriptRequestHandler);
		
		// Initialize the HTTP Server
		final int httpPort = resolvePort();
		vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
			if (http.succeeded()) {
				startPromise.complete();
				if (logger.isInfoEnabled()) {logger.info("HTTP server started on port: " + httpPort);}
			} else {
				startPromise.fail(http.cause());
			}
		});
	}

	private String resolveWorkspace() {
		String workspace = Configuration.get(DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE);
		if (workspace == null) {
			if (logger.isWarnEnabled()) {logger.warn("Workspace parameter is missing.");}
			if (logger.isWarnEnabled()) {logger.warn("Use DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE environment variable.");}
			if (logger.isWarnEnabled()) {logger.warn("Default folder 'workspace' will be used in this case.");}
			workspace = "workspace";
		}
		return workspace;
	}
	
	private void loadWorkspace(String workspace) {
		try {
    		DIRIGIBLE.load(workspace);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
	}
	
	private int resolvePort() {
		String port = Configuration.get(DIRIGIBLE_VERTX_PORT, VERTX_HTTP_PORT + "");
		int httpPort = VERTX_HTTP_PORT;
		try {
			httpPort = Integer.parseInt(port);
		} catch(Throwable t) {
			if (logger.isErrorEnabled()) {logger.error(t.getMessage(), t);}
		}
		return httpPort;
	}
}
