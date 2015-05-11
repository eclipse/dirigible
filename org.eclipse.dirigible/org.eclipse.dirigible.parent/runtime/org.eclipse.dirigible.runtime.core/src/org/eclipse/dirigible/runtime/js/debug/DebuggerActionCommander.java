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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;

public class DebuggerActionCommander implements IDebugCommands {

	private boolean executing = false;
	private DebugCommand currentCommand;

	private JavaScriptDebugFrame debugFrame;

	private JavaScriptDebugger debugger;

	private String executionId;

	private String userId;

	private DebuggerActionManager debuggerActionManager;

	public DebuggerActionCommander(DebuggerActionManager debuggerActionManager, String executionId,
			String userId) {
		this.debuggerActionManager = debuggerActionManager;
		this.executionId = executionId;
		this.userId = userId;
		this.debuggerActionManager.addCommander(this);
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
	}

	@Override
	public void stepInto() {
		currentCommand = DebugCommand.STEPINTO;
	}

	@Override
	public void addBreakpoint(BreakpointMetadata breakpoint) {
		getDebuggerActionManager().getBreakpoints().add(breakpoint);
	}

	@Override
	public void clearBreakpoint(BreakpointMetadata breakpoint) {
		getDebuggerActionManager().getBreakpoints().remove(breakpoint);
	}

	@Override
	public void clearAllBreakpoints() {
		getDebuggerActionManager().getBreakpoints().clear();
	}

	@Override
	public void clearAllBreakpoints(String path) {
		Iterator<BreakpointMetadata> iterator = getDebuggerActionManager().getBreakpoints()
				.iterator();
		while (iterator.hasNext()) {
			BreakpointMetadata breakpoint = iterator.next();
			if (breakpoint.getFullPath().equals(path)) {
				iterator.remove();
			}
		}
	}

	@Override
	public Set<BreakpointMetadata> getBreakpoints() {
		return getDebuggerActionManager().getBreakpoints();
	}

	@Override
	public void skipAllBreakpoints() {
		currentCommand = DebugCommand.SKIP_ALL_BREAKPOINTS;
	}

	@Override
	public DebugCommand getCommand() {
		return currentCommand;
	}

	public DebuggerActionManager getDebuggerActionManager() {
		return debuggerActionManager;
	}

	public JavaScriptDebugFrame getDebugFrame() {
		return debugFrame;
	}

	public void setDebugFrame(JavaScriptDebugFrame debugFrame) {
		this.debugFrame = debugFrame;
	}

	public JavaScriptDebugger getDebugger() {
		return debugger;
	}

	public void setDebugger(JavaScriptDebugger debugger) {
		this.debugger = debugger;
	}

	public void clean() {
		this.debugFrame = null;
		this.debugger = null;
		this.debuggerActionManager.removeCommander(this);
	}

	/**
	 * @return the session id of logged in user
	 */
	public String getSessionId() {
		return getDebuggerActionManager().getSessionId();
	}

	/**
	 * @return the execution id for the debug session
	 */
	public String getExecutionId() {
		return this.executionId;
	}

	/**
	 * @return the user id of logged in user
	 */
	public String getUserId() {
		return userId;
	}

}
