/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.api;

import static java.text.MessageFormat.format;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating EngineExecutor objects.
 */
public class EngineExecutorFactory {

	/** The Constant ENGINE_EXECUTORS. */
	private static final ServiceLoader<IEngineExecutor> ENGINE_EXECUTORS = ServiceLoader.load(IEngineExecutor.class);

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(EngineExecutorFactory.class);

	/**
	 * Gets the engine executor.
	 *
	 * @param type
	 *            the type
	 * @return the engine executor
	 */
	@SuppressWarnings("deprecation")
	public static IEngineExecutor getEngineExecutor(String type) {
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			if (next.getType().equals(type)) {
				try {
					return next.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				}
			}
		}
		if (logger.isErrorEnabled()) {logger.error(format("Executor of Type {0} does not exist.", type));}
		return null;
	}
	
	/**
	 * Gets the engines types.
	 *
	 * @return the engines types
	 */
	public static Set<String> getEnginesTypes() {
		Set<String> engineTypes = new HashSet<String>();
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			engineTypes.add(next.getType());
		}
		return engineTypes;
	}
	
	/**
	 * Gets the engines names.
	 *
	 * @return the engines names
	 */
	public static Set<String> getEnginesNames() {
		Set<String> engineTypes = new HashSet<String>();
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			engineTypes.add(next.getName());
		}
		return engineTypes;
	}

}
