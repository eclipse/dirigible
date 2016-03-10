/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Servlet implementation class FlowLogServlet
 */
public class FlowLogServlet extends HttpServlet {

	private static final long serialVersionUID = 5645919875259516138L;

	private static final Logger logger = Logger.getLogger(FlowLogServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FlowLogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter writer = response.getWriter();

		try {
			response.setContentType("application/json");
			// full log for chart
			String result = FlowLogRecordDAO.getFlowLogRecords();
			writer.println(result);

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}
		writer.flush();
		writer.close();
	}

}
