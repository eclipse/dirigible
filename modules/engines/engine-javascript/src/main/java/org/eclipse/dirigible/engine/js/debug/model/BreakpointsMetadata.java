/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.debug.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BreakpointsMetadata {
	
	private transient DebugModel model;

	private Set<BreakpointMetadata> breakpointsList = new HashSet<BreakpointMetadata>();

	BreakpointsMetadata(DebugModel model) {
		super();
		this.model = model;
	}
	
	public DebugModel getModel() {
		return model;
	}

	public Set<BreakpointMetadata> getBreakpoints() {
		return breakpointsList;
	}

	public void setBreakpointsList(Set<BreakpointMetadata> breakpointsList) {
		this.breakpointsList = breakpointsList;
	}

	public int[] getBreakpoints(String fullPath) {
		List<BreakpointMetadata> list = new ArrayList<BreakpointMetadata>();
		for (BreakpointMetadata breakpoint : getBreakpoints()) {
			if (breakpoint.getFullPath().equals(fullPath)) {
				list.add(breakpoint);
			}
		}

		int breakpoints[] = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			breakpoints[i] = list.get(i).getRow();
		}
		return breakpoints;
	}

	@Override
	public String toString() {
		return Arrays.toString((breakpointsList.toArray(new BreakpointMetadata[breakpointsList
				.size()])));
	}
}
