/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.ide.generation.processor;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class GenerationTemplateParameters.
 */
public class GenerationTemplateParameters {
	
	/** The template. */
	private String template;
	
	/** The parameters. */
	private Map<String, Object> parameters;

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Sets the template.
	 *
	 * @param template the new template
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters the parameters
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	

}
