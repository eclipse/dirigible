package org.eclipse.dirigible.api.v3.core;

import static java.text.MessageFormat.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFacade {

	private static final Logger logger = LoggerFactory.getLogger(JavaFacade.class);

	public static final Object call(String className, String methodName, Object[] params) throws ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> clazz = Class.forName(className);
		Class<?>[] parameterTypes = new Class[params.length];
		int i = 0;
		for (Object param : params) {
			parameterTypes[i++] = param.getClass();
		}
		Method method = clazz.getMethod(methodName, parameterTypes);
		if (Modifier.isStatic(method.getModifiers())) {
			Object result;
			try {
				result = method.invoke(null, params);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
				return null;
			}
			return result;
		}
		String message = format("No such static method [{0}] in class [{1}]", methodName, className);
		logger.error(message);
		throw new NoSuchMethodException(message);
	}

}
