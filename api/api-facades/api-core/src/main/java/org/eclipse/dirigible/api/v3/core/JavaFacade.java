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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.NullType;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.Primitives;

/**
 * The JavaFacade plays the role of the bridge between both environment - Java and Javascript.
 */
public class JavaFacade {

	private static final Logger logger = LoggerFactory.getLogger(JavaFacade.class);

	private static List<String> blacklist;
	static {
		try {
			blacklist = IOUtils.readLines(JavaFacade.class.getResourceAsStream("/.blacklist"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			blacklist = new ArrayList<String>();
		}
	}

	/**
	 * Call a static method of the given class.
	 *
	 * @param className
	 *            the class name
	 * @param methodName
	 *            the method name
	 * @param parameters
	 *            the parameters
	 * @return the object
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws ContextException
	 *             the context exception
	 */
	public static final Object call(String className, String methodName, Object[] parameters) throws ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ContextException {
		checkBlacklist(className, methodName);
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

	private static void checkBlacklist(String className, String methodName) throws IllegalAccessException {
		String find = className + ":" + methodName;
		if (blacklist.contains(find)) {
			throw new IllegalAccessException(format("Calling of method [{0}] from the class [{1}] is forbidden.", methodName, className));
		}
	}

	/**
	 * Find method of a given class per name and parameters. It performs some artificial logic based on the underlying
	 * javascript engine in use
	 *
	 * @param methodName
	 *            the method name
	 * @param clazz
	 *            the clazz
	 * @param parameterTypes
	 *            the parameter types
	 * @param params
	 *            the params
	 * @return the method
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	public static Method findMethod(String methodName, Class<?> clazz, Class<?>[] parameterTypes, List<Object> params) throws NoSuchMethodException {
		Method method = null;
		try {
			// exact match
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
				// iterates over the methods with the same name and parameters number
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
							continue;
						}
						if (nextClass.isAssignableFrom(parameterTypes[i])) {
							continue;
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
		// if (method == null) {
		// logger.warn("Called method could not be found in:\n" +
		// ReflectionToStringBuilder.toString((clazz.getMethods())));
		// }
		return method;
	}

	/**
	 * Instantiate an Object at Java side per given class name and constructor parameters.
	 *
	 * @param className
	 *            the class name
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws ContextException
	 *             the context exception
	 */
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

	/**
	 * Invoke an object instance's method with list of parameters.
	 *
	 * @param uuid
	 *            the uuid pointer to the object instance
	 * @param methodName
	 *            the method name
	 * @param parameters
	 *            the parameters
	 * @return the object
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws ContextException
	 *             the context exception
	 */
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
		Method method = null;
		if (Modifier.isPublic(clazz.getModifiers())) {
			method = findMethod(methodName, clazz, parameterTypes, params);
		} else {
			Class[] interfaces = clazz.getInterfaces();
			for (Class i : interfaces) {
				method = findMethod(methodName, i, parameterTypes, params);
				if (method != null) {
					break;
				}
			}
		}

		if (method != null) {
			checkBlacklist(method.getDeclaringClass().getCanonicalName(), methodName);
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

	/**
	 * Free the object instance from the execution context.
	 *
	 * @param uuid
	 *            the uuid the object instance pointer
	 * @throws ContextException
	 *             the context exception
	 */
	public static final void free(String uuid) throws ContextException {
		ThreadContextFacade.removeProxy(uuid);
	}

	/**
	 * Checks if is primitive.
	 *
	 * @param clazz
	 *            the clazz
	 * @return true, if is primitive
	 */
	private static boolean isPrimitive(Class<?> clazz) {
		return (clazz.isPrimitive() && (clazz != void.class)) || (clazz == Double.class) || (clazz == Float.class) || (clazz == Long.class)
				|| (clazz == Integer.class) || (clazz == Short.class) || (clazz == Character.class) || (clazz == Byte.class)
				|| (clazz == Boolean.class) || (clazz == String.class);
	}

	/**
	 * Checks if is array of primitives.
	 *
	 * @param clazz
	 *            the clazz
	 * @return true, if is array of primitives
	 */
	private static boolean isArrayOfPrimitives(Class<?> clazz) {
		return (clazz == int[].class) || (clazz == byte[].class) || (clazz == double[].class) || (clazz == long[].class) || (clazz == float[].class)
				|| (clazz == short[].class) || (clazz == char[].class) || (clazz == String[].class);
	}

	/**
	 * Normalize parameters.
	 *
	 * @param parameters
	 *            the parameters
	 * @return the list
	 * @throws ContextException
	 *             the context exception
	 */
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

	/**
	 * Enumerate types.
	 *
	 * @param params
	 *            the params
	 * @return the class[]
	 */
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
