/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow.log;

public class FlowLog {

	public static final int STATUS_STARTED = 1;

	public static final int STATUS_FAILED = 2;

	public static final int STATUS_COMPLETED = 3;

	public static final int STATUS_STEP_STARTED = 4;

	public static final int STATUS_STEP_FAILED = 5;

	public static final int STATUS_STEP_COMPLETED = 6;

	private String instance;

	private String flowName;

	private String flowUUID;

	private String stepName;

	private int status;

	private String message;

	private String context;

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getFlowUUID() {
		return flowUUID;
	}

	public void setFlowUUID(String flowUUID) {
		this.flowUUID = flowUUID;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
