/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.util.Set;

import org.eclipse.dirigible.engine.js.debug.model.BreakpointMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionModel;
import org.eclipse.dirigible.engine.js.debug.model.IDebugExecutor;

public class RhinoJavascriptDebugActionCommander implements IDebugExecutor {

	private boolean executing = false;
	private DebugCommand currentCommand;

	private RhinoJavascriptDebugFrame debugFrame;

	private RhinoJavascriptDebugger debugger;

	private String sessionId;
	
	private String executionId;

	private String userId;

	private DebugSessionModel session;

	public RhinoJavascriptDebugActionCommander(DebugSessionModel session, String sessionId, String executionId, String userId) {
		this.session = session;
		this.sessionId = sessionId;
		this.executionId = executionId;
		this.userId = userId;
//		this.debuggerActionManager.addCommander(this);
		init();
	}

	public void init() {
		currentCommand = null;
		executing = false;
	}

	@Override
	public void continueExecution() {
		currentCommand = DebugCommand.CONTINUE;
	}

	@Override
	public void pauseExecution() {
		executing = false;
	}

	@Override
	public boolean isExecuting() {
		return executing;
	}

	@Override
	public void resumeExecution() {
		executing = true;
	}

	@Override
	public void stepOver() {
		currentCommand = DebugCommand.STEPOVER;
		resumeExecution();
	}

	@Override
	public void stepInto() {
		currentCommand = DebugCommand.STEPINTO;
		resumeExecution();
	}

//	@Override
//	public void addBreakpoint(BreakpointMetadata breakpoint) {
//		this.session.getModel().getBreakpointsMetadata().getBreakpoints().add(breakpoint);
//	}
//
//	@Override
//	public void clearBreakpoint(BreakpointMetadata breakpoint) {
//		getDebuggerActionManager().getBreakpoints().remove(breakpoint);
//	}
//
//	@Override
//	public void clearAllBreakpoints() {
//		getDebuggerActionManager().getBreakpoints().clear();
//	}
//
//	@Override
//	public void clearAllBreakpoints(String path) {
//		Iterator<BreakpointMetadata> iterator = getDebuggerActionManager().getBreakpoints()
//				.iterator();
//		while (iterator.hasNext()) {
//			BreakpointMetadata breakpoint = iterator.next();
//			if (breakpoint.getFullPath().equals(path)) {
//				iterator.remove();
//			}
//		}
//	}

	@Override
	public Set<BreakpointMetadata> getBreakpoints() {
		return this.session.getModel().getBreakpointsMetadata().getBreakpoints();
	}

	@Override
	public void skipAllBreakpoints() {
		currentCommand = DebugCommand.SKIP_ALL_BREAKPOINTS;
		resumeExecution();
	}

	@Override
	public DebugCommand getCommand() {
		return currentCommand;
	}

//	public DebuggerActionManager getDebuggerActionManager() {
//		return debuggerActionManager;
//	}

	public RhinoJavascriptDebugFrame getDebugFrame() {
		return debugFrame;
	}

	public void setDebugFrame(RhinoJavascriptDebugFrame debugFrame) {
		this.debugFrame = debugFrame;
	}

	public RhinoJavascriptDebugger getDebugger() {
		return debugger;
	}

	public void setDebugger(RhinoJavascriptDebugger debugger) {
		this.debugger = debugger;
	}

	public void clean() {
		this.debugFrame = null;
		this.debugger = null;
//		this.debuggerActionManager.removeCommander(this);
	}

	/**
	 * @return the session id of logged in user
	 */
	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * @return the execution id for the debug session
	 */
	@Override
	public String getExecutionId() {
		return this.executionId;
	}

	/**
	 * @return the user id of logged in user
	 */
	@Override
	public String getUserId() {
		return userId;
	}

}
