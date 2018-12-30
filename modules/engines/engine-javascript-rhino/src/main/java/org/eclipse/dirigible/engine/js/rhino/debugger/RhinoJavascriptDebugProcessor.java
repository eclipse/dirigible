/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.engine.js.debug.model.DebugManager;
import org.eclipse.dirigible.engine.js.debug.model.DebugModel;
import org.eclipse.dirigible.engine.js.debug.model.DebugModelFacade;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RhinoJavascriptDebugProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptDebugProcessor.class);
	
	private static Map<String, List<Session>> OPEN_USER_SESSIONS = new ConcurrentHashMap<String, List<Session>>();
	
	public static final String DEBUG_SEPARATOR = ":"; //$NON-NLS-1$
	
	private static volatile int DISABLE = 0;
	
	public void onOpen(Session session) throws IOException {
		String userId = UserFacade.getName(session);
		List<Session> userSessions = OPEN_USER_SESSIONS.get(userId);
		if (userSessions == null) {
			userSessions = new ArrayList<Session>();
		}
		userSessions.add(session);
		OPEN_USER_SESSIONS.put(userId, userSessions);
		if (DebugModelFacade.getDebugModel(userId) == null) {
			DebugModelFacade.createDebugModel(userId, new RhinoJavascriptDebugController(userId));
		}
		logger.debug("Open debug session for user: " + userId);
	}

	public void onError(Session session, Throwable throwable) {
		logger.error("Error while processing debug information: " + throwable.getMessage());
	}

	public void onMessage(String message, Session session) {
		//
	}

	public void onClose(Session session) {
		if (DISABLE == 1) {
			return;
		}
		String userId = UserFacade.getName(session);
		List<Session> userSessions = OPEN_USER_SESSIONS.get(userId);
		if (userSessions == null) {
			logger.error("Could not map the given session for the active user!");
			return;
		}
		Iterator<Session> iterator = userSessions.iterator();
		while (iterator.hasNext()) {
			Session nextSession = iterator.next();
			if (nextSession.getId().equalsIgnoreCase(session.getId())) {
				iterator.remove();
			}
		}
		if (userSessions.isEmpty()) {
			OPEN_USER_SESSIONS.remove(userId);
		}
		logger.debug("Session " + userId + " has ended");
	}

	public static void clearCurrentSession(String userId) {
		DebugModel debugModel = DebugManager.getDebugModel(userId);
		if (debugModel != null) {
			DebugSessionModel currentSession = debugModel.getActiveSession();
			if (currentSession != null) {
				debugModel.removeSession(currentSession);
				List<DebugSessionModel> sessions = debugModel.getSessions();
				if (!sessions.isEmpty()) {
					debugModel.setActiveSession(sessions.get(0));
				}
				//DebugManager.registerDebugModel(userId, debugModel);   ???
				RhinoJavascriptDebugSender.sendCurrentSessions(userId, debugModel);
			}
		}
	}

	public static void closeAll() {
		DISABLE = 1;
		try {
			Collection<DebugModel> debugModels = DebugManager.getDebugModels();
			for (DebugModel debugModel : debugModels) {
				if (debugModel != null) {
					List<DebugSessionModel> sessions = debugModel.getSessions();
					for (DebugSessionModel session : sessions) {
						session.getDebugExecutor().skipAllBreakpoints();
					}
				}
			}
			for (List<Session> openSessions : OPEN_USER_SESSIONS.values()) {
				Iterator<Session> iterator = openSessions.iterator();
				while (iterator.hasNext()) {
					try {
						synchronized (iterator) {
							iterator.next().close();
							iterator.remove();
						}
					} catch (Throwable e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		} finally {
			DISABLE = 0;
		}
	}
	
	public static List<Session> getUserSessions(String userId) {
		List<Session> userSessions = OPEN_USER_SESSIONS.get(userId);
		return userSessions;
	}
	
}
