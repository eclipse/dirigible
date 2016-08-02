/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationServlet;

/**
 * Generation Service for WebContent for Entity artifacts
 * Sample requests:
 * POST Request for Entity List and Manage page:
 * ====
 * {
 * "templateType":"list_and_manage",
 * "fileName":"my_page.html",
 * "projectName":"myproject",
 * "packageName":"mypackage",
 * "pageTitle":"My Title",
 * "serviceEndpoint":"/mypackage/myservice.js",
 * "columns":
 * [
 * { "name":"BOOKID", "type":"INTEGER", "primaryKey":"true", "visible":"true", "size":"5", "widgetType":"text",
 * "label":"#" },
 * { "name":"BOOKISBN", "type":"CHAR", "primaryKey":"false", "visible":"true", "size":"13", "widgetType":"text",
 * "label":"ISBN" },
 * { "name":"BOOKTITLE", "type":"VARCHAR", "primaryKey":"false", "visible":"true", "size":"15", "widgetType":"text",
 * "label":"Title" },
 * { "name":"BOOKAUTHOR", "type":"VARCHAR", "primaryKey":"false", "visible":"true", "size":"7", "widgetType":"text",
 * "label":"Author" },
 * { "name":"BOOKPRICE", "type":"DOUBLE", "primaryKey":"false", "visible":"true", "size":"12", "widgetType":"float",
 * "label":"Price" }
 * ]
 * }
 * ====
 * Get Request returns all the available WebContent for Entity related templates
 */
public class WebContentEntityGenerationServlet extends AbstractGenerationServlet {

	private static final long serialVersionUID = -3650506905899341103L;

	@Override
	protected String doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			return new WebContentEntityGenerationWorker(getRepository(request), getWorkspace(request)).generate(parameters, request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	protected String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			return new WebContentEntityGenerationWorker(getRepository(request), getWorkspace(request)).enumerateTemplates(request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

}
