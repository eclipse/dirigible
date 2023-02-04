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
package org.eclipse.dirigible.components.engine.template.javascript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.components.engine.template.TemplateEngine;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class JavascriptGenerationEngine.
 */
@Component
public class JavascriptGenerationEngine implements TemplateEngine {

	/** The Constant ENGINE_NAME. */
	public static final String ENGINE_NAME = "javascript";
	
	
	/** The javascript service. */
    @Autowired
    private JavascriptService javascriptService;
	
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
	    	RepositoryPath path = new RepositoryPath((String) parameters.get("handler"));
	    	Object result = javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
//			String result = ScriptEngineExecutorsManager.evalModule((String) parameters.get("handler"), context).toString();
			return (result != null && result instanceof String) ? ((String) result).getBytes(StandardCharsets.UTF_8) : new byte[]{};
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
