/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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

public class JavascriptGenerationEngine implements IGenerationEngine {

	public static final String ENGINE_NAME = "javascript";
	
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
		try {
			Map<Object, Object> context = new HashMap<Object, Object>();
	        BiConsumer<Object, Object> action = new ContextBiConsumer(context);
			parameters.forEach(action);
			String result = ScriptEngineExecutorsManager.executeServiceModule("javascript", (String) parameters.get("handler"), context).toString();
			return result.getBytes(StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException("Could not evaluate template by Javascript: " + location, ex);
		}
	}
	
	class ContextBiConsumer implements BiConsumer<Object, Object> {
		
		Map<Object, Object> context;
		
		ContextBiConsumer(Map<Object, Object> context) {
			this.context = context;
		}
		  
	    public void accept(Object k, Object v) { 
	    	this.context.put(k, v);
	    } 
	} 

}
