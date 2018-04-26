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

import java.util.Arrays;
import java.util.List;

public class VariableValuesMetadata extends DebugSessionMetadata {
	
	private List<VariableValue> variableValueList;
	
	public VariableValuesMetadata(String sessionId, String executionId, String userId) {
		super(sessionId, executionId, userId);
	}

	public VariableValuesMetadata(String sessionId, String executionId, String userId, List<VariableValue> variableValueList) {
		super(sessionId, executionId, userId);
		this.variableValueList = variableValueList;
	}

	public List<VariableValue> getVariableValueList() {
		return variableValueList;
	}

	public void setVariableValueList(List<VariableValue> variableValueList) {
		this.variableValueList = variableValueList;
	}

	@Override
	public String toString() {
		return Arrays.toString((variableValueList.toArray(new VariableValue[variableValueList
				.size()])));
	}
}
