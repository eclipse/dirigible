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

import java.util.Set;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;

public interface IDebugCommands {
	public enum DebugCommand {
		CONTINUE, PAUSE, STEPOVER, STEPINTO, SKIP_ALL_BREAKPOINTS;
	}

	public void continueExecution();

	public void pauseExecution();

	public boolean isExecuting();

	public void resumeExecution();

	public void stepOver();

	public void stepInto();

	public void addBreakpoint(BreakpointMetadata breakpoint);

	public void clearBreakpoint(BreakpointMetadata breakpoint);

	public void clearAllBreakpoints();

	public void clearAllBreakpoints(String path);

	public Set<BreakpointMetadata> getBreakpoints();

	public void skipAllBreakpoints();

	public DebugCommand getCommand();
}
