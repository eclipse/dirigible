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
package org.eclipse.dirigible.engine.js.debug.model;

public class LinebreakMetadata extends DebugSessionMetadata implements Comparable<BreakpointMetadata> {
	
	private BreakpointMetadata breakpoint;

	public LinebreakMetadata(String sessionId, String executionId, String userId, BreakpointMetadata breakpoint) {
		super(sessionId, executionId, userId);
		this.breakpoint = breakpoint;
	}

	public LinebreakMetadata(String sessionId, String executionId, String userId, String path, Integer row) {
		super(sessionId, executionId, userId);
		this.breakpoint = new BreakpointMetadata(path, row);
	}

	public BreakpointMetadata getBreakpoint() {
		return breakpoint;
	}
	
	public void setBreakpoint(BreakpointMetadata breakpoint) {
		this.breakpoint = breakpoint;
	}

	@Override
	public int compareTo(BreakpointMetadata o) {
		return this.getBreakpoint().compareTo(o);
	}
}
