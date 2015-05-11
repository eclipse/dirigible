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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;

public class DebuggerActionManager {

	private static final String DEBUGGER_ACTION_MANAGER = "debugger.action.manager";

	private String sessionId;

	private Set<BreakpointMetadata> breakpoints = new TreeSet<BreakpointMetadata>();

	private Map<String, DebuggerActionCommander> commanders = Collections
			.synchronizedMap(new HashMap<String, DebuggerActionCommander>());

	public static DebuggerActionManager getInstance(HttpSession session) {
		DebuggerActionManager debuggerActionManager = (DebuggerActionManager) session
				.getAttribute(DEBUGGER_ACTION_MANAGER);
		if (debuggerActionManager == null) {
			debuggerActionManager = new DebuggerActionManager(session.getId());
			session.setAttribute(DEBUGGER_ACTION_MANAGER, debuggerActionManager);
		}
		return debuggerActionManager;
	}

	private DebuggerActionManager(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void addCommander(DebuggerActionCommander commander) {
		commanders.put(commander.getExecutionId(), commander);
	}

	public void removeCommander(DebuggerActionCommander commander) {
		commanders.remove(commander.getExecutionId());
	}

	public DebuggerActionCommander getCommander(String executionId) {
		return commanders.get(executionId);
	}

	public Map<String, DebuggerActionCommander> getCommanders() {
		return commanders;
	}

	public Set<BreakpointMetadata> getBreakpoints() {
		return breakpoints;
	}

}
