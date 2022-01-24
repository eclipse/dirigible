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

import java.util.Set;

public interface IDebugExecutor {
	public enum DebugCommand {
		CONTINUE, PAUSE, STEPOVER, STEPINTO, SKIP_ALL_BREAKPOINTS;
	}

	public void continueExecution();

	public void pauseExecution();

	public boolean isExecuting();

	public void resumeExecution();

	public void stepOver();

	public void stepInto();

//	public void addBreakpoint(BreakpointMetadata breakpoint);
//
//	public void clearBreakpoint(BreakpointMetadata breakpoint);
//
//	public void clearAllBreakpoints();
//
//	public void clearAllBreakpoints(String path);

	public Set<BreakpointMetadata> getBreakpoints();

	public void skipAllBreakpoints();

	public DebugCommand getCommand();
	
	/**
	 * @return the session id of logged in user
	 */
	public String getSessionId();

	/**
	 * @return the execution id for the debug session
	 */
	public String getExecutionId();

	/**
	 * @return the user id of logged in user
	 */
	public String getUserId();
	
}
