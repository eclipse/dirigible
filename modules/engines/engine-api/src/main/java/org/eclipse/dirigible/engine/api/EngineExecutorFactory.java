/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api;

import static java.text.MessageFormat.format;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating EngineExecutor objects.
 */
public class EngineExecutorFactory {

	private static final ServiceLoader<IEngineExecutor> ENGINE_EXECUTORS = ServiceLoader.load(IEngineExecutor.class);

	private static final Logger logger = LoggerFactory.getLogger(EngineExecutorFactory.class);

	/**
	 * Gets the engine executor.
	 *
	 * @param type
	 *            the type
	 * @return the engine executor
	 */
	public static IEngineExecutor getEngineExecutor(String type) {
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			if (next.getType().equals(type)) {
				return StaticInjector.getInjector().getInstance(next.getClass());
			}
		}
		logger.error(format("Executor of Type {0} does not exist.", type));
		return null;
	}
	
	public static Set<String> getEnginesTypes() {
		Set<String> engineTypes = new HashSet<String>();
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			engineTypes.add(next.getType());
		}
		return engineTypes;
	}
	
	public static Set<String> getEnginesNames() {
		Set<String> engineTypes = new HashSet<String>();
		for (IEngineExecutor next : ENGINE_EXECUTORS) {
			engineTypes.add(next.getName());
		}
		return engineTypes;
	}

}
