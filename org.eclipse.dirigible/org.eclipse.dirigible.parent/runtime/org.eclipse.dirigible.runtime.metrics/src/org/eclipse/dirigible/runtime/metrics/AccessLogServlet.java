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

package org.eclipse.dirigible.runtime.metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.registry.PathUtils;

import com.google.gson.Gson;

public class AccessLogServlet extends HttpServlet {

	private static final long serialVersionUID = 5004610851206076344L;
	
	private static final Logger logger = Logger.getLogger(AccessLogServlet.class);

	private static final String LOCATIONS = "/locations";
	private static final String ALL = "/all";
	private static final Gson GSON = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		logger.debug("entering AccessLogServlet doGet...");

		String paramHitsPerPattern = request.getParameter("hitsPerPattern");
		String paramHitsPerProject = request.getParameter("hitsPerProject");
		String paramHitsPerURI = request.getParameter("hitsPerURI");
		String paramRTimePerPattern = request.getParameter("rtimePerPattern");
		String paramRTimePerProject = request.getParameter("rtimePerProject");
		String paramRTimePerURI = request.getParameter("rtimePerURI");
		String paramHitsByURI = request.getParameter("hitsByURI");

		String path = PathUtils.extractPath(request);
		logger.debug("path=" + path);
		if (LOCATIONS.endsWith(path)) {
			listLocations(response);
		} else if (paramHitsPerPattern != null) {
			listHitsPerPattern(response);
		} else if (paramHitsPerProject != null) {
			listHitsPerProject(response);
		} else if (paramHitsPerURI != null) {
			listHitsPerURI(response);
		} else if (paramRTimePerPattern != null) {
			listRTimePerPattern(response);
		} else if (paramRTimePerProject != null) {
			listRTimePerProject(response);
		} else if (paramRTimePerURI != null) {
			listRTimePerURI(response);
		} else if (paramHitsByURI != null) {
			listHitsByURI(response);
		} else {
			listLog(response);
		}
		response.getWriter().flush();
		response.getWriter().close();
		logger.debug("existing AccessLogServlet doGet");
	}

	private void listHitsPerPattern(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByPattern();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsPerProject(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByProject();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsPerURI(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByURI();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerPattern(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByPattern();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerProject(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByProject();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerURI(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByURI();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsByURI(HttpServletResponse response) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getHitsByURI();
			printChartData(response, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void printChartData(HttpServletResponse response, String[][] result) throws IOException {
		PrintWriter writer = response.getWriter();
		if (result == null) {
			writer.write("");
			return;
		}
		response.setContentType("text/tab-separated-values");

		for (int i = 0; i < result.length; i++) {
			String[] row = result[i];
			for (int j = 0; j < row.length; j++) {
				writer.print(row[j] + "\t");
			}
			writer.println();
		}
	}

	private void listLog(HttpServletResponse response) throws IOException {
		logger.debug("printing the access log");
		try {
			AccessLogRecord[] records = AccessLogRecordDAO.getAccessLogRecords();
			sendJson(response, records);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			response.getWriter().print(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listLocations(HttpServletResponse response) throws IOException {
		logger.debug("listing registered access locations");
		try {
			AccessLogLocationsDAO.refreshLocations();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		List<String> locations = AccessLogLocationsSynchronizer.getAccessLogLocations();
		sendJson(response, locations);
	}

	private void sendJson(HttpServletResponse response, Object content) throws IOException {
		String json = GSON.toJson(content);
		response.setContentType(ContentTypeHelper.getContentType("json"));
		response.getWriter().print(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("entering AccessLogServlet doPost...");

		String path = PathUtils.extractPath(request);
		logger.debug("path=" + path);
		if (path != null) {
			try {
				logger.debug("inserting: " + path);
				AccessLogLocationsDAO.insertLocation(path);
				response.getWriter().print("Added: " + path);
				response.getWriter().flush();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				response.getWriter().print(e.getMessage());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}

		logger.debug("existing AccessLogServlet doPost");
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("entering AccessLogServlet doDelete...");

		String path = PathUtils.extractPath(request);
		logger.debug("path=" + path);
		if (path != null) {
			if (!path.endsWith(ALL)) {
				logger.debug("removing access location: " + path);
				try {
					AccessLogLocationsDAO.deleteLocation(path);
					response.getWriter().print("Removed: " + path);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					response.getWriter().print(e.getMessage());
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				logger.debug("removing all access locations");
				try {
					AccessLogLocationsDAO.deleteAllLocations();
					response.getWriter().print("Removed All");
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					response.getWriter().print(e.getMessage());
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
			response.getWriter().flush();
		}
		logger.debug("existing AccessLogServlet doDelete");
	}

}
