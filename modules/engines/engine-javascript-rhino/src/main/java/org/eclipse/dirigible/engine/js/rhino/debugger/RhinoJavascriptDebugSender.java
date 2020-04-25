/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.io.IOException;
import java.util.List;

import javax.websocket.Session;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.engine.js.debug.model.BreakpointsMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugModel;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionModel;
import org.eclipse.dirigible.engine.js.debug.model.LinebreakMetadata;
import org.eclipse.dirigible.engine.js.debug.model.VariableValuesMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send a debug event to the corresponding open user's debug sessions 
 *
 */
public class RhinoJavascriptDebugSender {
	
	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptDebugSender.class);
	
	private static final String DEBUG_EVENT_SESSIONS_LIST = "sessions";
	private static final String DEBUG_EVENT_SESSION_REGISTER = "register";
	private static final String DEBUG_EVENT_SESSION_FINISH = "finish";
	private static final String DEBUG_EVENT_SESSION_LINEBREAK = "linebreak";
	private static final String DEBUG_EVENT_SESSION_VARIABLES = "variables";
	private static final String DEBUG_EVENT_SESSION_BREAKPOINTS = "breakpoints";
	
	/**
	 * Send the current sessions list
	 * 
	 * @param userId the user identifier
	 * @param debugModel the debug model
	 */
	public static void sendCurrentSessions(String userId, DebugModel debugModel) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSIONS_LIST;
		event.sessions = debugModel.getSessionsMetadata();
		sendToUser(userId, event);
	}
	
	/**
	 * Send the register event
	 * 
	 * @param userId the user identifier
	 * @param session the session
	 */
	public static void sendRegisterSession(String userId, DebugSessionModel session) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSION_REGISTER;
		event.session = session.getMetadata();
		sendToUser(userId, event);
	}
	
	/**
	 * Send the finish event
	 * 
	 * @param userId the user identifier
	 * @param session the session
	 */
	public static void sendFinishSession(String userId, DebugSessionModel session) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSION_FINISH;
		event.session = session.getMetadata();
		sendToUser(userId, event);
	}
	
	/**
	 * Send the line break event
	 * 
	 * @param userId the user identifier 
	 * @param session the session
	 * @param linebreak the line break meta-data
	 */
	public static void sendLineBreak(String userId, DebugSessionModel session, LinebreakMetadata linebreak) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSION_LINEBREAK;
		event.session = session.getMetadata();
		event.linebreak = linebreak;
		sendToUser(userId, event);
	}
	
	/**
	 * Send the refresh variables event
	 * 
	 * @param userId the user identifier 
	 * @param session the session
	 * @param variables the variables meta-data
	 */
	public static void sendVariables(String userId, DebugSessionModel session, VariableValuesMetadata variables) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSION_VARIABLES;
		event.session = session.getMetadata();
		event.variables = variables;
		sendToUser(userId, event);
	}
	
	/**
	 * Send the refresh breakpoints event
	 * 
	 * @param userId the user identifier 
	 * @param breakpoints the breakpoints meta-data
	 */
	public static void sendBreakpoints(String userId, BreakpointsMetadata breakpoints) {
		DebugEventMetadata event = new DebugEventMetadata();
		event.type = DEBUG_EVENT_SESSION_BREAKPOINTS;
		event.breakpoints = breakpoints;
		sendToUser(userId, event);
	}

	
	
	private static void sendToUser(String userId, DebugEventMetadata event) {
		List<Session> userSessions = RhinoJavascriptDebugProcessor.getUserSessions(userId);
		if (userSessions != null) {
			for (Session session : userSessions) {
				try {
					session.getBasicRemote().sendText(GsonHelper.GSON.toJson(event));
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}


}
