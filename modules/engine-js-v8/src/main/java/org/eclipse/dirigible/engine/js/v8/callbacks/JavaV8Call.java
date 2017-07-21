package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.dirigible.api.v3.core.JavaFacade;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public class JavaV8Call implements JavaCallback {

	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		int i = 0;
		String className = (String) parameters.get(i++);
		String methodName = (String) parameters.get(i++);
		Object[] params = new Object[parameters.length() - 2];
		for (int j = i; j < parameters.length(); j++) {
			V8Array param = (V8Array) parameters.get(j);
			params[j - 2] = V8ObjectUtils.getValue(param, 0);
			param.release();
		}
		try {
			return JavaFacade.call(className, methodName, params);
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		} finally {
			parameters.release();
		}
	}
}
