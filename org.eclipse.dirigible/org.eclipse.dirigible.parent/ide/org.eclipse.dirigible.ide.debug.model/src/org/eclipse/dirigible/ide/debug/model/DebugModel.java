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

package org.eclipse.dirigible.ide.debug.model;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.BreakpointsMetadata;
import org.eclipse.dirigible.repository.ext.debug.VariableValuesMetadata;

public class DebugModel {

	private IDebugController debugController;

	private VariableValuesMetadata variableValuesMetadata;
	
	private BreakpointsMetadata breakpointsMetadata;
	
	private BreakpointMetadata currentLineBreak;
	
	private String sessionId;
	
	private String executionId;
	
	private String userId;

	
	DebugModel(IDebugController debugController) {
		this.debugController = debugController;
	}

	public void setBreakpoint(String path, int row) {
		debugController.setBreakpoint(this, path, row);
	}

	public void clearBreakpoint(String path, int row) {
		debugController.clearBreakpoint(this, path, row);
	}

	public void clearAllBreakpoints() {
		debugController.clearAllBreakpoints(this);
	}

	public void clearAllBreakpoints(String path) {
		debugController.clearAllBreakpoints(this, path);
	}

	public VariableValuesMetadata getVariableValuesMetadata() {
		return variableValuesMetadata;
	}

	public void setVariableValuesMetadata(
			VariableValuesMetadata variableValuesMetadata) {
		this.variableValuesMetadata = variableValuesMetadata;
	}

	public BreakpointsMetadata getBreakpointsMetadata() {
		return breakpointsMetadata;
	}

	public void setBreakpointsMetadata(BreakpointsMetadata breakpointMetadata) {
		this.breakpointsMetadata = breakpointMetadata;
	}

	public BreakpointMetadata getCurrentLineBreak() {
		return currentLineBreak;
	}

	public void setCurrentLineBreak(BreakpointMetadata currentLineBreak) {
		this.currentLineBreak = currentLineBreak;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
}
