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
package org.eclipse.dirigible.core.generation.velocity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;

public class VelocityGenerationEngine implements IGenerationEngine {

	public static final String ENGINE_NAME = "velocity";
	
	private VelocityEngine engine;

	public VelocityGenerationEngine() {
		engine = new VelocityEngine();
		try {
			engine.init();
		} catch (Throwable e) {
			// logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input) throws IOException {
		return generate(parameters, location, input, null, null);
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input, String sm, String em)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
		ByteArrayInputStream in = new ByteArrayInputStream(input);
		try {
			final VelocityContext context = new VelocityContext();
			prepareContextData(parameters, context);
			engine.evaluate(context, writer, location, new InputStreamReader(in, StandardCharsets.UTF_8));
			writer.flush();
			return baos.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException("Could not evaluate template by Velocity: " + location, ex);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
	/**
	 * Put the input parameters to the Velocity Context for processing
	 *
	 * @param parameters
	 * @param context
	 */
	private void prepareContextData(Map<String, Object> parameters, VelocityContext context) {
		if (parameters == null) {
			return;
		}
		Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			context.put(entry.getKey(), entry.getValue());
		}
	}

}
