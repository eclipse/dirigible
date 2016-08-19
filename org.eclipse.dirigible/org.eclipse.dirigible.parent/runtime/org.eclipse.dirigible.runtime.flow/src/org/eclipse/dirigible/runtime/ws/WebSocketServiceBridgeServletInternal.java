/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.ws;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.SecurityManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.filter.SecuredLocationVerifier;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

/**
 * WebSocket OSGi Proxy for Services support
 * It
 */
public class WebSocketServiceBridgeServletInternal {

	private static final Logger logger = Logger.getLogger(WebSocketServiceBridgeServletInternal.class);

	private static final String PARAM_SESSION = "websocket-session";
	private static final String PARAM_SESSIONS = "websocket-sessions";
	public static final String PROP_REQUEST = "REQUEST";

	public static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		logger.debug("[ws:exec] onOpen: " + session.getId());
	}

	public void onMessage(String message, Session session, String type) {
		logger.debug("[ws:exec] onMessage: " + message + ", type: " + type);

		WebSocketRequest webSocketRequest = null;
		try {
			webSocketRequest = WebSocketRequestParser.parseRequest(message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			try {
				session.getBasicRemote().sendText("Wrong format of the request");
			} catch (IOException e1) {
				logger.error(e.getMessage(), e);
			}
			return;
		}

		try {
			if (SecuredLocationVerifier.isLocationSecured(webSocketRequest.getModule())) {
				// the locations is secured - check the user's role(s)
				HandshakeRequest request = (HandshakeRequest) session.getUserProperties().get(PROP_REQUEST);
				if (!isUserInRole(request, webSocketRequest.getModule())) {
					// the user doesn't have permissions to execute this service
					String warning = String.format("The user doesn't have the required role(s) to execute the service [%s].",
							webSocketRequest.getModule());
					session.getBasicRemote().sendText(warning);
					logger.warn(warning);
					return;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// prepares the execution context
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		executionContext.put(PARAM_SESSION, session);
		executionContext.put(PARAM_SESSIONS, openSessions);
		executionContext.putAll(webSocketRequest.getParams());
		// call the service
		executeByEngineType(webSocketRequest.getModule(), executionContext, type);
	}

	private static Object executeByEngineType(String module, Map<Object, Object> executionContext, String serviceType) {
		try {
			Set<String> types = EngineUtils.getAliases();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(serviceType)) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutorByAlias(type, null);
					return scriptExecutor.executeServiceModule(null, null, module, executionContext);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public void onError(Session session, String error) {
		logger.debug("[ws:exec] onError: " + error);
	}

	public void onClose(Session session) {
		openSessions.remove(session.getId());
		logger.debug("[ws:exec] onClose: Session " + session.getId() + " has ended");
	}

	public Boolean isUserInRole(HandshakeRequest request, String serviceLocation) {
		try {
			// String location = PathUtils.extractPathWebSocket(request);
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				SecurityManager securityManager = SecurityManager.getInstance(RepositoryFacade.getInstance().getRepository(null),
						DataSourceFacade.getInstance().getDataSource(null));
				List<String> roles = securityManager.getRolesForLocation(serviceLocation);
				for (String role : roles) {
					if (request.isUserInRole(role)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

}
