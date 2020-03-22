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
package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ContextFacade is used to store name value pairs to the execution (Thread) context.
 */
public class ContextFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(ContextFacade.class);

	/**
	 * Gets the value per name.
	 *
	 * @param name
	 *            the name
	 * @return the string
	 */
	public static final Object get(String name) {
		logger.trace("API - ContextFacade.get() -> begin");
		Object contextValue;
		try {
			contextValue = ThreadContextFacade.get(name);
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
		//String value = contextValue != null ? contextValue.toString() : null;
		logger.trace("API - ContextFacade.get() -> end");
		return contextValue;
	}

	/**
	 * Sets the value for name.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public static final void set(String name, Object value) {
		logger.trace("API - ContextFacade.set() -> begin");
		try {
			ThreadContextFacade.set(name, value);
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
		logger.trace("API - ContextFacade.set() -> end");
	}

}
