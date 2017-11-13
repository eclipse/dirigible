/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;

/**
 * The Java V8 Call Instance.
 */
public class JavaV8CallInstance extends JavaV8Callback implements JavaCallback {

	/*
	 * (non-Javadoc)
	 * @see com.eclipsesource.v8.JavaCallback#invoke(com.eclipsesource.v8.V8Object, com.eclipsesource.v8.V8Array)
	 */
	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		int i = 0;
		String uuid = (String) parameters.get(i++);
		String methodName = (String) parameters.get(i++);
		List<Object> params = normalizeParameters(parameters, i);
		try {
			Object result = JavaFacade.invoke(uuid, methodName, params.toArray(new Object[] {}));
			if ((result != null) && result.getClass().isArray()) {
				List<Object> list = new ArrayList<>();
				for (int j = 0; j < Array.getLength(result); j++) {
					Object next = Array.get(result, j);
					if (next instanceof Byte) {
						list.add(Integer.valueOf((byte) next));
					} else {
						list.add(next);
					}
				}
				return V8ObjectUtils.toV8Array(receiver.getRuntime(), list);
			}
			return result;
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException
				| SecurityException | ContextException e) {
			throw new RuntimeException(e);
		} finally {
			parameters.release();
		}
	}

}
