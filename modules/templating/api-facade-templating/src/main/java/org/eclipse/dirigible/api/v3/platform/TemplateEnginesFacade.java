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
package org.eclipse.dirigible.api.v3.platform;

import java.io.IOException;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.generation.api.GenerationEnginesManager;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;

public class TemplateEnginesFacade implements IScriptingFacade {

	private static final IGenerationEngine ENGINE_MUSTACHE = GenerationEnginesManager.getGenerationEngine("mustache");
	private static final IGenerationEngine ENGINE_VELOCITY = GenerationEnginesManager.getGenerationEngine("velocity");
	private static final IGenerationEngine ENGINE_JAVASCRIPT = GenerationEnginesManager.getGenerationEngine("javascript");

	private static final TemplateEngine TEMPLATE_ENGINE_MUSTACHE = new TemplateEngine(ENGINE_MUSTACHE);
	private static final TemplateEngine TEMPLATE_ENGINE_VELOCITY = new TemplateEngine(ENGINE_VELOCITY);
	private static final TemplateEngine TEMPLATE_ENGINE_JAVASCRIPT = new TemplateEngine(ENGINE_JAVASCRIPT);

	public static TemplateEngine getDefaultEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
	}

	public static TemplateEngine getMustacheEngine() {
		return TEMPLATE_ENGINE_MUSTACHE;
	}

	public static TemplateEngine getVelocityEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
	}
	
	public static TemplateEngine getJavascriptEngine() {
		return TEMPLATE_ENGINE_JAVASCRIPT;
	}

	public static class TemplateEngine {

		private static final String LOCATION_API_FACADE = "api-facade";

		private IGenerationEngine engine;

		public TemplateEngine(IGenerationEngine engine) {
			this.engine = engine;
		}

		@SuppressWarnings("unchecked")
		public String generate(String template, String parametersJson) throws IOException {
			Map<String, Object> parameters = GsonHelper.GSON.fromJson(parametersJson, Map.class);
			byte[] result = engine.generate(parameters, LOCATION_API_FACADE, template.getBytes());
			return new String(result);
		}

		@SuppressWarnings("unchecked")
		public String generate(String template, String parametersJson, String sm, String em) throws IOException {
			Map<String, Object> parameters = GsonHelper.GSON.fromJson(parametersJson, Map.class);
			byte[] result = engine.generate(parameters, LOCATION_API_FACADE, template.getBytes(), sm, em);
			return new String(result);
		}
	}
}
