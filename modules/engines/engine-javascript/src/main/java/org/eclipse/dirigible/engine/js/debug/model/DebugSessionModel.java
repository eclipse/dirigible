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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugSessionModel {
	
	private static final Logger logger = LoggerFactory.getLogger(DebugSessionModel.class);

	private DebugModel model;
	
	private IDebugExecutor debugExecutor;

	private VariableValuesMetadata variableValuesMetadata;
	
	private LinebreakMetadata currentLineBreak;
	
//	private String sessionId;
//	
//	private String executionId;
//	
//	private String userId;
	
	private boolean updated = true;

	
	DebugSessionModel(DebugModel model) {
		this.model = model;
	}
	
	public DebugModel getModel() {
		return model;
	}
	
	public IDebugController getDebugController() {
		this.setUpdated(true);
		return this.model.getDebugController();
	}
	
	public IDebugExecutor getDebugExecutor() {
		return debugExecutor;
	}
	
	public void setDebugExecutor(IDebugExecutor debugExecutor) {
		this.debugExecutor = debugExecutor;
	}

//	public void setBreakpoint(String path, int row) {
//		this.model.getDebugController().setBreakpoint(path, row);
//	}
//
//	public void clearBreakpoint(String path, int row) {
//		this.model.getDebugController().clearBreakpoint(path, row);
//	}
//
//	public void clearAllBreakpoints() {
//		this.model.getDebugController().clearAllBreakpoints();
//	}

//	public void clearAllBreakpoints(String path) {
//		debugController.clearAllBreakpoints(this, path);
//	}

	public VariableValuesMetadata getVariableValuesMetadata() {
		return variableValuesMetadata;
	}

	public void setVariableValuesMetadata(
			VariableValuesMetadata variableValuesMetadata) {
		this.variableValuesMetadata = variableValuesMetadata;
	}

	public LinebreakMetadata getCurrentLineBreak() {
		return currentLineBreak;
	}

	public void setCurrentLineBreak(LinebreakMetadata currentLineBreak) {
		this.currentLineBreak = currentLineBreak;
	}

	public String getSessionId() {
//		return sessionId;
		if (getDebugExecutor() == null) {
			logger.error("getSessionId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getSessionId();
	}

//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}

	public String getExecutionId() {
//		return executionId;
		if (getDebugExecutor() == null) {
			logger.error("getExecutionId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getExecutionId();
	}

//	public void setExecutionId(String executionId) {
//		this.executionId = executionId;
//	}

	public String getUserId() {
//		return userId;
		if (getDebugExecutor() == null) {
			logger.error("getUserId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getUserId();
	}

//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public void release() {
		debugExecutor.skipAllBreakpoints();
		debugExecutor.continueExecution();
	}
	
	public DebugSessionMetadata getMetadata() {
		DebugSessionMetadata metadata = new DebugSessionMetadata(this.getSessionId(), this.getExecutionId(), this.getUserId());
		return  metadata;
	}
	
}
