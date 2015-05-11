/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.debug;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.gson.Gson;
import org.eclipse.dirigible.repository.ext.debug.DebugConstants;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionsMetadata;
import org.eclipse.dirigible.repository.ext.debug.IDebugProtocol;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.task.IRunnableTask;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

public class DebugGlobalManager implements HttpSessionListener, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(DebugGlobalManager.class);

	private static Map<String, DebuggerActionManager> debuggerActionManagers = Collections
			.synchronizedMap(new HashMap<String, DebuggerActionManager>());

	private IDebugProtocol debugProtocol;

	public DebugGlobalManager() {
		logger.debug("entering DebugHttpSessionListener.constructor");

		TaskManagerShort.getInstance().registerRunnableTask(new DebugBridgeRegister(this));

		logger.debug("assign the DebugHttpSessionListener as listener to the DebugBridge");

		logger.debug("exiting DebugHttpSessionListener.constructor");
	}

	class DebugBridgeRegister implements IRunnableTask {

		DebugGlobalManager debugGlobalManager;

		DebugBridgeRegister(DebugGlobalManager debugGlobalManager) {
			this.debugGlobalManager = debugGlobalManager;
		}

		@Override
		public String getName() {
			return "Debug Bridge Register";
		}

		@Override
		public void start() {
			if (debugProtocol == null) {
				debugProtocol = DebugProtocolUtils.lookupDebugProtocol();
				if (debugProtocol != null) {
					debugProtocol.addPropertyChangeListener(debugGlobalManager);
					TaskManagerShort.getInstance().unregisterRunnableTask(this);
					logger.info("DebugGlobalManager has been register to DebuggerBridge");
				}
			}
		}

	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		logger.debug("entering DebugHttpSessionListener.sessionCreated() with sessionId: "
				+ se.getSession().getId());
		DebuggerActionManager debuggerActionManager = DebuggerActionManager.getInstance(se
				.getSession());
		debuggerActionManagers.put(se.getSession().getId(), debuggerActionManager);
		logger.debug("exiting DebugHttpSessionListener.sessionCreated()");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		logger.debug("entering DebugHttpSessionListener.sessionDestroyed() with sessionId: "
				+ se.getSession().getId());
		debuggerActionManagers.remove(se.getSession().getId());
		logger.debug("exiting DebugHttpSessionListener.sessionDestroyed()");
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String commandId = event.getPropertyName();
		String clientId = (String) event.getOldValue();
		String commandBody = (String) event.getNewValue();
		logger.debug("DebugHttpSessionListener propertyChange() command: " + commandId
				+ ", clientId: " + clientId + ", body: " + commandBody);

		if (!"debug.global.manager".equals(clientId)) {
			return;
		}

		if (commandId.equals(DebugConstants.DEBUG_REFRESH)) {
			sendSessionsMetadata();
		}
	}

	private void sendSessionsMetadata() {
		DebugSessionsMetadata debugSessionsMetadata = new DebugSessionsMetadata();
		for (DebuggerActionManager debuggerActionManager : debuggerActionManagers.values()) {
			for (DebuggerActionCommander debuggerActionCommander : debuggerActionManager
					.getCommanders().values()) {
				DebugSessionMetadata debugSessionMetadata = new DebugSessionMetadata(
						debuggerActionCommander.getSessionId(),
						debuggerActionCommander.getExecutionId(),
						debuggerActionCommander.getUserId());
				debugSessionsMetadata.getDebugSessionsMetadata().add(debugSessionMetadata);
			}
		}
		Gson gson = new Gson();
		String sessionsJson = gson.toJson(debugSessionsMetadata);
		send(DebugConstants.VIEW_SESSIONS, sessionsJson);

	}

	public void send(String commandId, String commandBody) {
		logger.debug("JavaScriptDebugFrame send() command: " + commandId + ", body: " + commandBody);
		DebugProtocolUtils.send(debugProtocol, commandId, "debug.global.manager", commandBody);
	}

}
