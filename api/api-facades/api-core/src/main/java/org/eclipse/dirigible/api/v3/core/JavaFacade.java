/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.core;

import static java.text.MessageFormat.format;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.NullType;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.Primitives;

public class JavaFacade {

	private static final Logger logger = LoggerFactory.getLogger(JavaFacade.class);

	public static final Object call(String className, String methodName, Object[] parameters) throws ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ContextException {
		logger.trace("API - JavaFacade.call() -> begin");
		Class<?> clazz = Class.forName(className);
		List<Object> params = normalizeParameters(parameters);
		Class<?>[] parameterTypes = enumerateTypes(params);
		Method method = findMethod(methodName, clazz, parameterTypes, params);
		if ((method != null) && Modifier.isStatic(method.getModifiers())) {
			Object result;
			try {
				result = method.invoke(null, params.toArray(new Object[] {}));
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
				return null;
			}
			if ((result != null) && !isPrimitive(result.getClass())) {
				if (isArrayOfPrimitives(result.getClass())) {
					String json = GsonHelper.GSON.toJson(result);
					return json;
				}
				// non primitive result - add to the context
				return ThreadContextFacade.setProxy(result);
			}

			return result;
		}
		String message = format("No such static method [{0}] in class [{1}] with parameters of types [{2}]", methodName, className,
				Arrays.toString(parameterTypes));
		logger.error(message);
		logger.trace("API - JavaFacade.call() -> end");
		throw new NoSuchMethodException(message);
	}

	private static Method findMethod(String methodName, Class<?> clazz, Class<?>[] parameterTypes, List<Object> params) throws NoSuchMethodException {
		Method method = null;
		try {
			method = clazz.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			// no method matching the exact parameters classes - try to find more generic one
			Method[] methods = clazz.getMethods();
			for (Method next : methods) {
				if (!next.getName().equals(methodName)) {
					continue;
				}
				Class[] nextParameterTypes = next.getParameterTypes();
				if (nextParameterTypes.length != parameterTypes.length) {
					break;
				}
				boolean matching = true;
				for (int i = 0; i < nextParameterTypes.length; i++) {
					Class nextClass = nextParameterTypes[i];
					if (nextClass.isPrimitive()) {
						nextClass = Primitives.wrap(nextClass);
					}
					if (parameterTypes[i].equals(Double.class)) {
						// Double handling for Rhino
						Double value = (Double) params.get(i);
						if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
							if (nextClass.isAssignableFrom(parameterTypes[i])) {
								continue;
							}
							if (Integer.class.isAssignableFrom(nextClass)) {
								params.set(i, value.intValue());
								continue;
							}
							if (Long.class.isAssignableFrom(nextClass)) {
								params.set(i, value.longValue());
								continue;
							}
						}
					} else {
						if (NullType.class.equals(parameterTypes[i])) {
							matching = true;
							break;
						}
						if (nextClass.isAssignableFrom(parameterTypes[i])) {
							matching = true;
							break;
						}
					}
					matching = false;
				}
				if (matching) {
					method = next;
					break;
				}
			}
		}
		return method;
	}

	public static final String instantiate(String className, Object[] parameters) throws ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ContextException {
		logger.trace("API - JavaFacade.instantiate() -> begin");
		Class<?> clazz = Class.forName(className);
		List<Object> params = normalizeParameters(parameters);
		Class<?>[] parameterTypes = enumerateTypes(params);
		Constructor constructor = clazz.getConstructor(parameterTypes);
		if (constructor != null) {
			Object result;
			try {
				result = constructor.newInstance(params.toArray(new Object[] {}));
				return ThreadContextFacade.setProxy(result);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
		String message = format("No such constructor [{0}] in class [{1}]", Arrays.toString(parameterTypes), className);
		logger.error(message);
		logger.trace("API - JavaFacade.instantiate() -> end");
		throw new NoSuchMethodException(message);
	}

	public static final Object invoke(String uuid, String methodName, Object[] parameters) throws ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ContextException {
		logger.trace("API - JavaFacade.invoke() -> begin");
		Object instance = ThreadContextFacade.getProxy(uuid);
		if (instance == null) {
			String message = format("Instance with UUID [{0}] does not exist in the context", uuid);
			logger.error(message);
			throw new IllegalStateException(message);
		}
		Class<?> clazz = instance.getClass();
		List<Object> params = normalizeParameters(parameters);
		Class<?>[] parameterTypes = enumerateTypes(params);
		Method method = findMethod(methodName, clazz, parameterTypes, params);
		if (method != null) {
			Object result;
			try {
				result = method.invoke(instance, params.toArray(new Object[] {}));
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
				return null;
			}
			if ((result != null) && !isPrimitive(result.getClass())) {
				if (isArrayOfPrimitives(result.getClass())) {
					String json = GsonHelper.GSON.toJson(result);
					return json;
				}
				// non primitive result - add to the context
				return ThreadContextFacade.setProxy(result);
			}

			return result;
		}
		String message = format("No such method [{0}] in class [{1}] with parameters of types [{2}]", methodName, clazz.getName(),
				Arrays.toString(parameterTypes));
		logger.error(message);
		logger.trace("API - JavaFacade.invoke() -> end");
		throw new NoSuchMethodException(message);
	}

	public static final void free(String uuid) throws ContextException {
		ThreadContextFacade.removeProxy(uuid);
	}

	private static boolean isPrimitive(Class<?> clazz) {
		return (clazz.isPrimitive() && (clazz != void.class)) || (clazz == Double.class) || (clazz == Float.class) || (clazz == Long.class)
				|| (clazz == Integer.class) || (clazz == Short.class) || (clazz == Character.class) || (clazz == Byte.class)
				|| (clazz == Boolean.class) || (clazz == String.class);
	}

	private static boolean isArrayOfPrimitives(Class<?> clazz) {
		return (clazz == int[].class) || (clazz == byte[].class) || (clazz == double[].class) || (clazz == long[].class) || (clazz == float[].class)
				|| (clazz == short[].class) || (clazz == char[].class) || (clazz == String[].class);
	}

	private static List<Object> normalizeParameters(Object[] parameters) throws ContextException {
		List<Object> params = new ArrayList<Object>();
		for (Object param : parameters) {
			if (param == null) {
				params.add(null);
				continue;
			}
			if (!"undefined".equals(param) && (!"jdk.nashorn.internal.runtime.Undefined".equals(param.getClass().getName()))) {
				if ((param instanceof String) && (ThreadContextFacade.getProxy((String) param) != null)) {
					params.add(ThreadContextFacade.getProxy((String) param));
				} else {
					params.add(param);
				}
			}
		}
		return params;
	}

	private static Class<?>[] enumerateTypes(List<Object> params) {
		Class<?>[] parameterTypes = new Class[params.size()];
		int i = 0;
		for (Object param : params) {
			if (param == null) {
				parameterTypes[i++] = NullType.class;
				continue;
			}
			parameterTypes[i++] = param.getClass();
		}
		return parameterTypes;
	}

}
