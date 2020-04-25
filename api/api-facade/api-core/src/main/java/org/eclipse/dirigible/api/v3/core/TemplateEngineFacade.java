/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.v3.core;

import java.io.IOException;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.core.generation.mustache.MustacheGenerationEngine;
import org.eclipse.dirigible.core.generation.velocity.VelocityGenerationEngine;

public class TemplateEngineFacade implements IScriptingFacade {

	private static final IGenerationEngine ENGINE_MUSTACHE = new MustacheGenerationEngine();
	private static final IGenerationEngine ENGINE_VELOCITY = new VelocityGenerationEngine();

	private static final TemplateEngine TEMPLATE_ENGINE_MUSTACHE = new TemplateEngine(ENGINE_MUSTACHE);
	private static final TemplateEngine TEMPLATE_ENGINE_VELOCITY = new TemplateEngine(ENGINE_VELOCITY);

	public static TemplateEngine getDefaultEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
	}

	public static TemplateEngine getMustacheEngine() {
		return TEMPLATE_ENGINE_MUSTACHE;
	}

	public static TemplateEngine getVelocityEngine() {
		return TEMPLATE_ENGINE_VELOCITY;
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
