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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugSessionModel {
	
	private static final Logger logger = LoggerFactory.getLogger(DebugSessionModel.class);

	private DebugModel model;
	
	private IDebugExecutor debugExecutor;

	private VariableValuesMetadata variableValuesMetadata;
	
	private LinebreakMetadata currentLineBreak;
	
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
		if (getDebugExecutor() == null) {
			logger.error("getSessionId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getSessionId();
	}

	public String getExecutionId() {
		if (getDebugExecutor() == null) {
			logger.error("getExecutionId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getExecutionId();
	}
	public String getUserId() {
		if (getDebugExecutor() == null) {
			logger.error("getUserId() - Debug executor not assigned");
			return "none";
		}
		return getDebugExecutor().getUserId();
	}

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
		DebugSessionMetadata metadata = new DebugSessionMetadata(this.getSessionId(), this.getExecutionId(), this.getUserId(), this.getExecutionId().equals(this.model.getActiveSession().getExecutionId()));
		return  metadata;
	}
	
}
