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
package org.eclipse.dirigible.api.v3.core.test;

import java.util.Map;

/**
 * The Class BaseObject.
 */
public abstract class BaseObject implements IBaseObject {

	/**
	 * Do something.
	 *
	 * @param parameters the parameters
	 * @param inheritedParameter the inherited parameter
	 * @param exactParameter the exact parameter
	 * @return the string
	 */
	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter) {
		return "";
	}

	/**
	 * Do something.
	 *
	 * @param parameters the parameters
	 * @param inheritedParameter the inherited parameter
	 * @param exactParameter the exact parameter
	 * @param s the s
	 * @return the string
	 */
	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter, String s) {
		return "";
	}

	/**
	 * Do something else.
	 *
	 * @param parameters the parameters
	 * @param inheritedParameter the inherited parameter
	 * @param s the s
	 * @return the string
	 */
	@Override
	public String doSomethingElse(Map parameters, IBaseParameter inheritedParameter, String s) {
		return "";
	}

}
