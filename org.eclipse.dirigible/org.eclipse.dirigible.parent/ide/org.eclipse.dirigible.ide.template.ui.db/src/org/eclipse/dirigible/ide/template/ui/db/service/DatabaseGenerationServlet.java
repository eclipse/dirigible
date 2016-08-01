/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.service;

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
 * "templateType":"0_table",
 * "fileName":"mytable.table",
 * "projectName":"myproject",
 * "packageName":"mypackage",
 * "columns":
 * [
 * { "name":"BOOKID", "type":"INTEGER", "length":"0", "notNull":"true", "primaryKey":"true", "defaultValue":"" },
 * { "name":"BOOKISBN", "type":"CHAR", "length":"13", "notNull":"true", "primaryKey":"false", "defaultValue":""},
 * { "name":"BOOKTITLE", "type":"VARCHAR", "length":"200", "notNull":"true", "primaryKey":"false", "defaultValue":""},
 * { "name":"BOOKAUTHOR", "type":"VARCHAR", "length":"100", "notNull":"true", "primaryKey":"false", "defaultValue":""},
 * { "name":"BOOKPRICE", "type":"DOUBLE", "length":"0", "notNull":"true", "primaryKey":"false", "defaultValue":""}
 * ]
 * }
 * ====
 * POST Request for View
 * ====
 * {
 * "templateType":"1_view",
 * "fileName":"myview.view",
 * "projectName":"gen_test",
 * "packageName":"mypackage",
 * "query":"SELECT * FROM mytable6"
 * }
 * ====
 * POST Request for DSV
 * ====
 * {
 * "templateType":"2_dsv",
 * "fileName":"mydsv.dsv",
 * "projectName":"gen_test",
 * "packageName":"mypackage",
 * "rows":"123|234324|234234
 * 6545464|645654|64654654
 * 89769789|98797|978"
 * }
 * ====
 * Get Request returns all the available DataStructre related templates
 */
public class DatabaseGenerationServlet extends AbstractGenerationServlet {

	private static final long serialVersionUID = -3650506905899341103L;

	@Override
	protected String doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			return new DatabaseGenerationWorker(getRepository(request), getWorkspace(request)).generate(parameters, request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	protected String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			return new DatabaseGenerationWorker(getRepository(request), getWorkspace(request)).enumerateTemplates(request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

}
