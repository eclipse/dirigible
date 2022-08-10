/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.generation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The Class GenerationEnginesManager.
 */
public class GenerationEnginesManager {
	
	/** The Constant GENERATION_ENGINES. */
	private static final ServiceLoader<IGenerationEngine> GENERATION_ENGINES = ServiceLoader.load(IGenerationEngine.class);
	
	/**
	 * Returns the registered Generation Engines.
	 *
	 * @return the list of Generation Engines
	 */
	public static final List<IGenerationEngine> getGenerationEngines() {
		List<IGenerationEngine> list = new ArrayList<>();
		for (IGenerationEngine next : GENERATION_ENGINES) {
			list.add(next);
		}
		return list;
	}
	
	/**
	 * Select a Generation Engine by Name.
	 *
	 * @param name the name
	 * @return the engine
	 */
	public static final IGenerationEngine getGenerationEngine(String name) {
		for (IGenerationEngine next : GENERATION_ENGINES) {
			if (next.getName().equalsIgnoreCase(name)) {
				return next;
			}
		}
		return null;
	}

}
