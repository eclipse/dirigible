/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.rhino.service;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.engine.js.rhino.debugger.RhinoJavascriptDebugProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@ServerEndpoint("/websockets/v3/ide/debug/sessions")
public class RhinoJavascriptEngineDebugWebsocketService {
	
	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptEngineDebugWebsocketService.class);
	
	@Inject
	private RhinoJavascriptDebugProcessor processor = StaticInjector.getInjector().getInstance(RhinoJavascriptDebugProcessor.class);

	@OnOpen
	public void onOpen(Session session) throws IOException {
		processor.onOpen(session);
		logger.info("[ws:console] onOpen: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		processor.onError(session, throwable);
		logger.info("[ws:console] onError: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		processor.onMessage(message, session);
		logger.info("[ws:console] onMessage: " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		processor.onClose(session);
		logger.info("[ws:console] onClose: " + session.getId());
	}

}
