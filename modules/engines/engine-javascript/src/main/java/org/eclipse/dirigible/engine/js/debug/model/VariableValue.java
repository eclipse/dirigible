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

public class VariableValue {
	private static final String VARIABLE_S_VALUE_S = "[variable = %s | value = %s]";
	private String variable;
	private String value;

	public VariableValue(String variable, String value) {
		this.variable = variable;
		this.value = value;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String message = String.format(VARIABLE_S_VALUE_S, variable, value);
		return message;
	}

}
