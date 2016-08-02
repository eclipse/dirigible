/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationServlet;

/**
 * Generation Service for DataStructure definition
 * Sample requests:
 * POST Request for Table:
 * ====
 * {
 * "templateType":"3_js_crud",
 * "fileName":"myservice.js",
 * "projectName":"myproject",
 * "packageName":"mypackage",
 * "tableName":"BOOKS",
 * "tableType":"TABLE",
 * "columns":
 * [
 * { "name":"BOOKID", "type":"INTEGER", "primaryKey":"true", "visible":"true" },
 * { "name":"BOOKISBN", "type":"CHAR", "primaryKey":"false", "visible":"true" },
 * { "name":"BOOKTITLE", "type":"VARCHAR", "primaryKey":"false", "visible":"true" },
 * { "name":"BOOKAUTHOR", "type":"VARCHAR", "primaryKey":"false", "visible":"true" },
 * { "name":"BOOKPRICE", "type":"DOUBLE", "primaryKey":"false", "visible":"true" }
 * ]
 * }
 * ====
 * Get Request returns all the available ScriptingServices related templates
 */
public class ScriptingGenerationServlet extends AbstractGenerationServlet {

	private static final long serialVersionUID = -3650506905899341103L;

	@Override
	protected String doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			return new ScriptingGenerationWorker(getRepository(request), getWorkspace(request)).generate(parameters, request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	protected String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			return new ScriptingGenerationWorker(getRepository(request), getWorkspace(request)).enumerateTemplates(request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

}
