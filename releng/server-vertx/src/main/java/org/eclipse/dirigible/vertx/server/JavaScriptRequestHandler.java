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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class JavaScriptRequestHandler<E> implements Handler<E> {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaScriptRequestHandler.class);
	
	public static final String ROUTE = "/services/v4/js/*";

	@Override
	public void handle(E ctx) {
		HttpServerRequest request = ((RoutingContext) ctx).request();
		HttpServerResponse response = ((RoutingContext) ctx).response();
		response.setChunked(true);
		
		try {
			Map<Object, Object> context = new HashMap<Object, Object>();
			context.put("vertx.request", request);
			context.put("vertx.response", response);
			String module = request.path().substring(ROUTE.length() - 2);
    		MainVerticle.DIRIGIBLE.execute("javascript", module, context);
		} catch (ScriptingException | ContextException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
		
		response.end();
	}

}
