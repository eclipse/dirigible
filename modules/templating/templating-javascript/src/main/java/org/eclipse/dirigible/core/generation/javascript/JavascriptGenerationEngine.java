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
package org.eclipse.dirigible.core.generation.javascript;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;

/**
 * The Class JavascriptGenerationEngine.
 */
public class JavascriptGenerationEngine implements IGenerationEngine {

	/** The Constant ENGINE_NAME. */
	public static final String ENGINE_NAME = "javascript";
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	/**
	 * Generate.
	 *
	 * @param parameters the parameters
	 * @param location the location
	 * @param input the input
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input) throws IOException {
		return generate(parameters, location, input, null, null);
	}

	/**
	 * Generate.
	 *
	 * @param parameters the parameters
	 * @param location the location
	 * @param input the input
	 * @param sm the sm
	 * @param em the em
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input, String sm, String em)
			throws IOException {
		try {
			Map<Object, Object> context = new HashMap<Object, Object>();
	        BiConsumer<Object, Object> action = new ContextBiConsumer(context);
			parameters.forEach(action);
			String result = ScriptEngineExecutorsManager.evalModule((String) parameters.get("handler"), context).toString();
			return result.getBytes(StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException("Could not evaluate template by Javascript: " + location, ex);
		}
	}
	
	/**
	 * The Class ContextBiConsumer.
	 */
	class ContextBiConsumer implements BiConsumer<Object, Object> {
		
		/** The context. */
		Map<Object, Object> context;
		
		/**
		 * Instantiates a new context bi consumer.
		 *
		 * @param context the context
		 */
		ContextBiConsumer(Map<Object, Object> context) {
			this.context = context;
		}
		  
	    /**
    	 * Accept.
    	 *
    	 * @param k the k
    	 * @param v the v
    	 */
    	public void accept(Object k, Object v) { 
	    	this.context.put(k, v);
	    } 
	} 

}
