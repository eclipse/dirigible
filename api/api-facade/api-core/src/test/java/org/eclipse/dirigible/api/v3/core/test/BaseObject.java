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
package org.eclipse.dirigible.api.v3.core.test;

import java.util.Map;

public abstract class BaseObject implements IBaseObject {

	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter) {
		return "";
	}

	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter, String s) {
		return "";
	}

	@Override
	public String doSomethingElse(Map parameters, IBaseParameter inheritedParameter, String s) {
		return "";
	}

}
