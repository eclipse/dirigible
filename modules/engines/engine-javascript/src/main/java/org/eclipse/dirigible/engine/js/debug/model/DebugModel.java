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
package org.eclipse.dirigible.engine.js.debug.model;

import java.util.ArrayList;
import java.util.List;

public class DebugModel {

	public static final String DEBUG_MODEL = "debug.model";

	private transient IDebugController debugController;

	private BreakpointsMetadata breakpointsMetadata;

	private List<DebugSessionModel> sessions;

	private DebugSessionModel activeSession;

	public DebugModel(IDebugController debugController) {
		this.debugController = debugController;
		this.breakpointsMetadata = new BreakpointsMetadata(this);
		this.sessions = new ArrayList<DebugSessionModel>();
	}

	public IDebugController getDebugController() {
		return debugController;
	}

	public void setDebugController(IDebugController debugController) {
		this.debugController = debugController;
	}

	public DebugSessionModel createSession() {
		DebugSessionModel session = new DebugSessionModel(this);
		this.sessions.add(session);
		if (this.sessions.size() == 1) {
			setActiveSession(session);
		}
		return session;
	}

	public void removeSession(DebugSessionModel session) {
		session.release();
		this.sessions.remove(session);
	}

	public List<DebugSessionModel> getSessions() {
		return sessions;
	}
	
	public List<DebugSessionMetadata> getSessionsMetadata() {
		List<DebugSessionMetadata> result = new ArrayList<>();
		for (DebugSessionModel model : sessions) {
			DebugSessionMetadata metadata = model.getMetadata();
			result.add(metadata);
		}
		return result;
	}

	public DebugSessionModel getActiveSession() {
		return activeSession;
	}

	public void setActiveSession(DebugSessionModel activeSession) {
		this.activeSession = activeSession;
	}

	public DebugSessionModel getSessionByExecutionId(String executionId) {
		for (DebugSessionModel debugSessionModel : sessions) {
			if ((debugSessionModel.getExecutionId() != null) && debugSessionModel.getExecutionId().equals(executionId)) {
				return debugSessionModel;
			}
		}
		return null;
	}

	public BreakpointsMetadata getBreakpointsMetadata() {
		return breakpointsMetadata;
	}

	public void setBreakpointsMetadata(BreakpointsMetadata breakpointMetadata) {
		this.breakpointsMetadata = breakpointMetadata;
	}

	public DebugSessionModel getSessionModelById(String sessionId) {
		for (DebugSessionModel session : sessions) {
			String id = session.getSessionId();
			String executionId = session.getExecutionId();
			StringBuilder pattern = new StringBuilder();
			pattern.append(id).append(":").append("\\d+").append(":").append(executionId).append(":");
			if (sessionId.matches(pattern.toString())) {
				return session;
			}
		}
		return null;
	}
}
