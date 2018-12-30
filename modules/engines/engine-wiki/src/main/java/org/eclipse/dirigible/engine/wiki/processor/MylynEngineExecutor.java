/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.wiki.processor;

import org.eclipse.dirigible.engine.api.resource.AbstractResourceExecutor;

/**
 * The Mylyn Wiki Engine Executor.
 */
public class MylynEngineExecutor extends AbstractResourceExecutor {
	
	public static final String ENGINE_TYPE = "wiki";
	
	public static final String ENGINE_NAME = "Mylyn Wiki Content Engine";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return ENGINE_TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

}
