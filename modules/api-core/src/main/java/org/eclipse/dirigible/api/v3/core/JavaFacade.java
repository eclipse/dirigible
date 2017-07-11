package org.eclipse.dirigible.api.v3.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaFacade {
	
	public static final Object call(String className, String methodName, Object[] params) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class clazz = Class.forName(className);
		Class[] parameterTypes = new Class[params.length];
		int i=0;
		for (Object param : params) {
			parameterTypes[i++] = param.getClass();
		}
		Method method = clazz.getMethod(methodName, parameterTypes);
		Object result = method.invoke(null, params);
		return result;
	}

}
