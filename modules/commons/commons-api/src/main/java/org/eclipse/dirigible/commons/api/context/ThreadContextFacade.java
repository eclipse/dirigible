/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scripting context facade is the centralized place where the different scripting facade providers
 * can register the request (sync) scoped objects.
 */
public class ThreadContextFacade {

	private static final Logger logger = LoggerFactory.getLogger(ThreadContextFacade.class);

	private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<Map<String, Object>>();

	private static final ThreadLocal<Map<String, Object>> PROXIES = new ThreadLocal<Map<String, Object>>();
	
	private static final ThreadLocal<Map<String, AutoCloseable>> CLOSEABLES = new ThreadLocal<Map<String, AutoCloseable>>();

	private static final AtomicLong UUID_GENERATOR = new AtomicLong(Long.MIN_VALUE);
	
	private static final ThreadLocal<Integer> STACK_ID = new ThreadLocal<Integer>();
	
	/**
	 * Initializes the context. This has to be called at the very first (as possible) place at the service entry point
	 *
	 */
	public static final void setUp() {
		if (CONTEXT.get() == null || CONTEXT.get().size() == 0) {
			CONTEXT.set(new HashMap<String, Object>());
		}
		if (PROXIES.get() == null || PROXIES.get().size() == 0) {
			PROXIES.set(new HashMap<String, Object>());
		}
		if (CLOSEABLES.get() == null || CLOSEABLES.get().size() == 0) {
			CLOSEABLES.set(new HashMap<String, AutoCloseable>());
		}
		if (STACK_ID.get() == null) {
			STACK_ID.set(0);
		} else {
			STACK_ID.set(STACK_ID.get() + 1);
		}
		logger.trace("Scripting context {} has been set up", Thread.currentThread().hashCode());
	}

	/**
	 * IMPORTANT! This have to be added at the finally block to clean up objects after the execution of the service.
	 *
	 */
	public static final void tearDown() {
		if (CONTEXT.get() != null && CONTEXT.get().size() > 0) {
			CONTEXT.get().clear();
			CONTEXT.remove();
		}
		if (PROXIES.get() != null && PROXIES.get().size() > 0) {
			PROXIES.get().clear();
			PROXIES.remove();
		}
		if (CLOSEABLES.get() != null && CLOSEABLES.get().size() > 0) {
			int stackId = STACK_ID.get();
			for (Entry<String, AutoCloseable> closeable : CLOSEABLES.get().entrySet()) {
				try {
					String prefix = stackId + "_";
					if (closeable.getKey().startsWith(prefix)) {
						logger.error("Object of type {} from the context {} has not been closed properly.", closeable.getValue().getClass().getCanonicalName(), Thread.currentThread().hashCode());
						closeable.getValue().close();
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (stackId == 0) {
				CLOSEABLES.get().clear();
				CLOSEABLES.remove();
			}
			STACK_ID.set(stackId - 1);
		}
		logger.trace("Scripting context {} has been torn down", Thread.currentThread().hashCode());
	}

	/**
	 * Get a context scripting object.
	 *
	 * @param key
	 *            the key
	 * @return the value by this key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final Object get(String key) throws ContextException {
		checkContext();
		return CONTEXT.get().get(key);
	}

	/**
	 * Set a context scripting object.
	 *
	 * @param value
	 *            the value
	 * @return the UUID of the object
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final String set(Object value) throws ContextException {
		final String uuid = generateObjectId();
		set(uuid, value);
		return uuid;
	}

	/**
	 * Set a context scripting object. If object with
	 * with this key exists, it will be replaced with
	 * the new object
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void set(String key, Object value) throws ContextException {
		checkContext();
		CONTEXT.get().put(key, value);
		logger.trace("Context object has been added to {} with key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Remove a context scripting object.
	 *
	 * @param key
	 *            the key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void remove(String key) throws ContextException {
		checkContext();
		CONTEXT.get().remove(key);
		logger.trace("Context object has been removed - key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Check context.
	 *
	 * @throws ContextException
	 *             the context exception
	 */
	private static void checkContext() throws ContextException {
		if (CONTEXT.get() == null) {
			throw new ContextException("Context has not been initialized");
		}
	}

	/**
	 * Check whether the facade is valid.
	 *
	 * @return yes, if it is valid
	 */
	public static boolean isValid() {
		return (CONTEXT.get() != null);
	}

	/**
	 * Get a proxy scripting object.
	 *
	 * @param key
	 *            the key
	 * @return the value by this key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final Object getProxy(String key) throws ContextException {
		checkContext();
		return PROXIES.get().get(key);
	}

	/**
	 * Set a proxy scripting object.
	 *
	 * @param value
	 *            the value
	 * @return the UUID of the object
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final String setProxy(Object value) throws ContextException {
		final String uuid = generateObjectId();
		setProxy(uuid, value);
		return uuid;
	}

	/**
	 * Generate object id.
	 *
	 * @return the string
	 */
	private static String generateObjectId() {
		return Long.toString(UUID_GENERATOR.incrementAndGet(), Character.MAX_RADIX);
	}

	/**
	 * Set a proxy scripting object. If proxy object
	 * with this key exists, it will be replaced with
	 * the new object
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void setProxy(String key, Object value) throws ContextException {
		checkContext();
		PROXIES.get().put(key, value);
		logger.trace("Proxy object has been added to {} with key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Remove a proxy scripting object.
	 *
	 * @param key
	 *            the key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void removeProxy(String key) throws ContextException {
		checkContext();
		PROXIES.get().remove(key);
		logger.trace("Proxy object has been removes - key {}", Thread.currentThread().hashCode(), key);
	}
	
	/**
	 * Add a closeable object to the map
	 * 
	 * @param closeable the closeable object
	 */
	public static final void addCloseable(AutoCloseable closeable) {
		if (CLOSEABLES.get() != null) {
			CLOSEABLES.get().put(STACK_ID.get() + "_" + closeable.hashCode(), closeable);
			logger.trace("Closeable object has been added to {} with hash {}", Thread.currentThread().hashCode(), closeable.hashCode());
		}
	}
	
	/**
	 * Remove a closeable object.
	 *
	 * @param closeable the closeable object
	 */
	public static final void removeCloseable(AutoCloseable closeable) {
		if (CLOSEABLES.get() != null) {
			CLOSEABLES.get().remove(STACK_ID.get() + "_" + closeable.hashCode());
			logger.trace("Proxy object has been removes - hash {}", Thread.currentThread().hashCode(), closeable.hashCode());
		}
	}
}
