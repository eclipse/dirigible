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
package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

/**
 * The Java V8 New Instance.
 */
public class JavaV8NewInstance extends JavaV8Callback implements JavaCallback {

	/*
	 * (non-Javadoc)
	 * @see com.eclipsesource.v8.JavaCallback#invoke(com.eclipsesource.v8.V8Object, com.eclipsesource.v8.V8Array)
	 */
	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		int i = 0;
		String className = (String) parameters.get(i++);
		List<Object> params = normalizeParameters(parameters, i);
		try {
			Object result = JavaFacade.instantiate(className, params.toArray(new Object[] {}));
			return result;
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException
				| SecurityException | ContextException e) {
			throw new RuntimeException(e);
		} finally {
			parameters.release();
		}
	}

}
