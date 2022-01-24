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
