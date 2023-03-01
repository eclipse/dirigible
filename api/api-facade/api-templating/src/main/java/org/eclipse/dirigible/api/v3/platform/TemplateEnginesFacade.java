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
package org.eclipse.dirigible.api.v3.platform;

import java.io.IOException;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.generation.api.GenerationEnginesManager;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;

/**
 * The Class TemplateEnginesFacade.
 */
public class TemplateEnginesFacade implements IScriptingFacade {

	/** The Constant ENGINE_MUSTACHE. */
	private static final IGenerationEngine ENGINE_MUSTACHE = GenerationEnginesManager.getGenerationEngine("mustache");
	
	/** The Constant ENGINE_VELOCITY. */
	private static final IGenerationEngine ENGINE_VELOCITY = GenerationEnginesManager.getGenerationEngine("velocity");
	
	/** The Constant ENGINE_JAVASCRIPT. */
	private static final IGenerationEngine ENGINE_JAVASCRIPT = GenerationEnginesManager.getGenerationEngine("javascript");

	/** The Constant TEMPLATE_ENGINE_MUSTACHE. */
	private static final TemplateEngine TEMPLATE_ENGINE_MUSTACHE = new TemplateEngine(ENGINE_MUSTACHE);
	
	/** The Constant TEMPLATE_ENGINE_VELOCITY. */
	private static final TemplateEngine TEMPLATE_ENGINE_VELOCITY = new TemplateEngine(ENGINE_VELOCITY);
	
	/** The Constant TEMPLATE_ENGINE_JAVASCRIPT. */
	private static final TemplateEngine TEMPLATE_ENGINE_JAVASCRIPT = new TemplateEngine(ENGINE_JAVASCRIPT);

	/**
	 * Gets the default engine.
	 *
	 * @return the default engine
	 */
	public static TemplateEngine getDefaultEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
	}

	/**
	 * Gets the mustache engine.
	 *
	 * @return the mustache engine
	 */
	public static TemplateEngine getMustacheEngine() {
		return TEMPLATE_ENGINE_MUSTACHE;
	}

	/**
	 * Gets the velocity engine.
	 *
	 * @return the velocity engine
	 */
	public static TemplateEngine getVelocityEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
	}
	
	/**
	 * Gets the javascript engine.
	 *
	 * @return the javascript engine
	 */
	public static TemplateEngine getJavascriptEngine() {
		return TEMPLATE_ENGINE_JAVASCRIPT;
	}

	/**
	 * The Class TemplateEngine.
	 */
	public static class TemplateEngine {

		/** The Constant LOCATION_API_FACADE. */
		private static final String LOCATION_API_FACADE = "api-facade";

		/** The engine. */
		private IGenerationEngine engine;

		/**
		 * Instantiates a new template engine.
		 *
		 * @param engine the engine
		 */
		public TemplateEngine(IGenerationEngine engine) {
			this.engine = engine;
		}

		/**
		 * Generate.
		 *
		 * @param template the template
		 * @param parametersJson the parameters json
		 * @return the string
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		@SuppressWarnings("unchecked")
		public String generate(String template, String parametersJson) throws IOException {
			Map<String, Object> parameters = GsonHelper.fromJson(parametersJson, Map.class);
			byte[] result = engine.generate(parameters, LOCATION_API_FACADE, template.getBytes());
			return new String(result);
		}

		/**
		 * Generate.
		 *
		 * @param template the template
		 * @param parametersJson the parameters json
		 * @param sm the sm
		 * @param em the em
		 * @return the string
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		@SuppressWarnings("unchecked")
		public String generate(String template, String parametersJson, String sm, String em) throws IOException {
			Map<String, Object> parameters = GsonHelper.fromJson(parametersJson, Map.class);
			byte[] result = engine.generate(parameters, LOCATION_API_FACADE, template.getBytes(), sm, em);
			return new String(result);
		}
	}
}
