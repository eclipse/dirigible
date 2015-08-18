/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.memory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Servlet implementation class MemoryServlet
 */
public class MemoryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 5645919875259516138L;
	
	private static final Logger logger = Logger.getLogger(MemoryServlet.class);
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MemoryServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = response.getWriter();
		
		String paramLog = request.getParameter("log");
		
		try {
			if (paramLog != null) {
				response.setContentType("application/json");
				// full log for chart
				String result = MemoryLogRecordDAO.getMemoryLogRecords();
				writer.println(result);
			} else {
				// instant numbers
				String content = MemoryLogRecordDAO.generateMemoryInfo();
				writer.write(content);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}
		writer.flush();
		writer.close();
	}

}
