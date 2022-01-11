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

	private static final ThreadLocal<Map<String, Map<String, Object>>> STACKED_CONTEXT = new ThreadLocal<Map<String, Map<String, Object>>>();

	private static final ThreadLocal<Map<String, Map<String, AutoCloseable>>> STACKED_CLOSEABLES = new ThreadLocal<Map<String, Map<String, AutoCloseable>>>();

	private static final AtomicLong UUID_GENERATOR = new AtomicLong(Long.MIN_VALUE);
	
	private static final ThreadLocal<Integer> STACK_ID = new ThreadLocal<Integer>();
	
	/**
	 * Initializes the context. This has to be called at the very first (as possible) place at the service entry point
	 *
	 */
	public static final void setUp() {
		if (STACKED_CONTEXT.get() == null || STACKED_CONTEXT.get().size() == 0) {
			STACKED_CONTEXT.set(new HashMap<String, Map<String, Object>>());
		}
		if (STACKED_CLOSEABLES.get() == null || STACKED_CLOSEABLES.get().size() == 0) {
			STACKED_CLOSEABLES.set(new HashMap<String, Map<String, AutoCloseable>>());
		}
		if (STACK_ID.get() == null) {
			STACK_ID.set(0);
		} else {
			STACK_ID.set(STACK_ID.get() + 1);
		}
		STACKED_CONTEXT.get().put(STACK_ID.get() + "", new HashMap<String, Object>());
		STACKED_CLOSEABLES.get().put(STACK_ID.get() + "", new HashMap<String, AutoCloseable>());
		
		logger.trace("Scripting context {} has been set up", Thread.currentThread().hashCode());
	}

	/**
	 * IMPORTANT! This have to be added at the finally block to clean up objects after the execution of the service.
	 *
	 */
	public static final void tearDown() {
		if (STACK_ID.get() == null) {
			return;
		}
		int stackId = STACK_ID.get();
		if (STACKED_CONTEXT.get() != null && STACKED_CONTEXT.get().size() > 0) {
			STACKED_CONTEXT.get().get(STACK_ID.get() + "").clear();
		}
		if (STACKED_CLOSEABLES.get() != null && STACKED_CLOSEABLES.get().size() > 0) {
			Map<String, AutoCloseable> CLOSEABLES = STACKED_CLOSEABLES.get().get(STACK_ID.get() + "");
			for (Entry<String, AutoCloseable> closeable : CLOSEABLES.entrySet()) {
				try {
					logger.error("Object of type {} from the context {} has not been closed properly.", closeable.getValue().getClass().getCanonicalName(), Thread.currentThread().hashCode());
					closeable.getValue().close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			STACKED_CLOSEABLES.get().get(STACK_ID.get() + "").clear();
		}
		if (stackId == 0) {
			STACKED_CONTEXT.remove();
			STACKED_CLOSEABLES.remove();
		}
		STACK_ID.set(stackId - 1);
		
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
		return STACKED_CONTEXT.get().get(STACK_ID.get() + "").get(key);
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
		STACKED_CONTEXT.get().get(STACK_ID.get() + "").put(key, value);
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
		STACKED_CONTEXT.get().get(STACK_ID.get() + "").remove(key);
		logger.trace("Context object has been removed - key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Check context.
	 *
	 * @throws ContextException
	 *             the context exception
	 */
	private static void checkContext() throws ContextException {
		if (STACKED_CONTEXT.get() == null) {
			throw new ContextException("Context has not been initialized");
		}
	}

	/**
	 * Check whether the facade is valid.
	 *
	 * @return yes, if it is valid
	 */
	public static boolean isValid() {
		return (STACKED_CONTEXT.get() != null);
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
	 * Add a closeable object to the map
	 * 
	 * @param closeable the closeable object
	 */
	public static final void addCloseable(AutoCloseable closeable) {
		if (STACKED_CLOSEABLES.get() != null) {
			STACKED_CLOSEABLES.get().get(STACK_ID.get() + "").put(STACK_ID.get() + "_" + closeable.hashCode(), closeable);
			logger.trace("Closeable object has been added to {} with hash {}", Thread.currentThread().hashCode(), closeable.hashCode());
		}
	}
	
	/**
	 * Remove a closeable object.
	 *
	 * @param closeable the closeable object
	 */
	public static final void removeCloseable(AutoCloseable closeable) {
		if (STACKED_CLOSEABLES.get() != null) {
			STACKED_CLOSEABLES.get().get(STACK_ID.get() + "").remove(STACK_ID.get() + "_" + closeable.hashCode());
			logger.trace("Closeable object has been removed - hash {}", Thread.currentThread().hashCode(), closeable.hashCode());
		}
	}
}
