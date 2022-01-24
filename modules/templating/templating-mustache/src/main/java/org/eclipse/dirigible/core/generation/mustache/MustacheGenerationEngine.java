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
package org.eclipse.dirigible.core.generation.mustache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.core.generation.api.IGenerationEngine;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.util.DecoratedCollection;

public class MustacheGenerationEngine implements IGenerationEngine {

	private static final String DECORATION = "_";
	private static final String ENGINE_NAME = "mustache";
	private static final String MUSTACHE_DEFAULT_START_SYMBOL = "{{";
	private static final String MUSTACHE_DEFAULT_END_SYMBOL = "}}";
	
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input) throws IOException {
		return generate(parameters, location, input, MUSTACHE_DEFAULT_START_SYMBOL, MUSTACHE_DEFAULT_END_SYMBOL);
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input, String sm, String em)
			throws IOException {
		sm = sm == null ? MUSTACHE_DEFAULT_START_SYMBOL : sm;
		em = em == null ? MUSTACHE_DEFAULT_END_SYMBOL : em;
		decorateParameters(parameters);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
		DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
		Mustache mustache = defaultMustacheFactory.compile(new InputStreamReader(new ByteArrayInputStream(input), StandardCharsets.UTF_8), location, sm, em);
		mustache.execute(writer, parameters);
		writer.flush();
		return baos.toByteArray();
	}

	private void decorateParameters(Map<String, Object> parameters) {
		if (parameters != null) {
			Map<String, Object> newParameters = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				if (entry.getValue() != null && entry.getValue() instanceof Map) {
					decorateParameters((Map) entry.getValue());
				} else if (entry.getValue() != null && entry.getValue() instanceof Collection) {
					if (entry.getValue() != null && entry.getValue() instanceof DecoratedCollection) {
						newParameters.put(entry.getKey() + DECORATION, (Collection) entry.getValue());
					} else {
						newParameters.put(entry.getKey() + DECORATION, new DecoratedCollection<>((Collection) entry.getValue()));
					}
					for (Object item : (Collection) entry.getValue()) {
						if (item != null && item instanceof Map) {
							decorateParameters((Map) item);
						}
					}
				}
			}
			parameters.putAll(newParameters);
		}
	}

}
