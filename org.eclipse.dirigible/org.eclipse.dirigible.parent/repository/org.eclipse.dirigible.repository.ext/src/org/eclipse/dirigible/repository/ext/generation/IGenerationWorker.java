/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.generation;

import javax.servlet.http.HttpServletRequest;

public interface IGenerationWorker {

	public abstract String generate(String parameters, HttpServletRequest request) throws Exception;

	public abstract String getTemplates(HttpServletRequest request) throws Exception;

}
