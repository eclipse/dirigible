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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.registry.PathUtils;

public class AccessLogServlet extends HttpServlet {

	private static final String LOCATIONS = "/locations";

	private static final Logger logger = Logger.getLogger(AccessLogServlet.class);

	private static final String ALL = "/all";
	private static final long serialVersionUID = 5004610851206076344L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		logger.debug("entering AccessLogServlet doGet...");

		String paramHitsPerPattern = req.getParameter("hitsPerPattern");
		String paramHitsPerProject = req.getParameter("hitsPerProject");
		String paramHitsPerURI = req.getParameter("hitsPerURI");
		String paramRTimePerPattern = req.getParameter("rtimePerPattern");
		String paramRTimePerProject = req.getParameter("rtimePerProject");
		String paramRTimePerURI = req.getParameter("rtimePerURI");
		String paramHitsByURI = req.getParameter("hitsByURI");

		Gson gson = new Gson();
		String path = PathUtils.extractPath(req);
		logger.debug("path=" + path);
		if (LOCATIONS.endsWith(path)) {
			listLocations(resp, gson);
		} else if (paramHitsPerPattern != null) {
			listHitsPerPattern(resp);
		} else if (paramHitsPerProject != null) {
			listHitsPerProject(resp);
		} else if (paramHitsPerURI != null) {
			listHitsPerURI(resp);
		} else if (paramRTimePerPattern != null) {
			listRTimePerPattern(resp);
		} else if (paramRTimePerProject != null) {
			listRTimePerProject(resp);
		} else if (paramRTimePerURI != null) {
			listRTimePerURI(resp);
		} else if (paramHitsByURI != null) {
			listHitsByURI(resp);
		} else {
			listLog(resp, gson);
		}
		resp.getWriter().flush();
		resp.getWriter().close();
		logger.debug("existing AccessLogServlet doGet");
	}

	private void listHitsPerPattern(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByPattern();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsPerProject(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByProject();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsPerURI(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getLastRecordsByURI();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerPattern(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByPattern();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerProject(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByProject();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listRTimePerURI(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getRTRecordsByURI();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listHitsByURI(HttpServletResponse resp) throws IOException {
		try {
			AccessLogRecordDAO accessLogRecordDAO = new AccessLogRecordDAO();
			String[][] result = accessLogRecordDAO.getHitsByURI();
			printChartData(resp, result);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void printChartData(HttpServletResponse resp, String[][] result) throws IOException {
		PrintWriter writer = resp.getWriter();
		if (result == null) {
			writer.write("");
			return;
		}
		resp.setContentType("text/tab-separated-values");

		for (int i = 0; i < result.length; i++) {
			String[] row = result[i];
			for (int j = 0; j < row.length; j++) {
				writer.print(row[j] + "\t");
			}
			writer.println();
		}
	}

	private void listLog(HttpServletResponse resp, Gson gson) throws IOException {
		logger.debug("printing the access log");
		try {
			String content = gson.toJson(AccessLogRecordDAO.getAccessLogRecords());
			printJson(resp, content);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().print(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void listLocations(HttpServletResponse resp, Gson gson) throws IOException {
		logger.debug("listing registered access locations");
		try {
			AccessLogLocationsDAO.refreshLocations();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		String content = gson.toJson(AccessLogLocationsSynchronizer.getAccessLogLocations()
				.toArray(new String[] {}));
		printJson(resp, content);
	}

	private void printJson(HttpServletResponse resp, String content) throws IOException {
		resp.setContentType(ContentTypeHelper.getContentType("json"));
		resp.getWriter().print(content);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("entering AccessLogServlet doPost...");

		String path = PathUtils.extractPath(req);
		logger.debug("path=" + path);
		if (path != null) {
			try {
				logger.debug("inserting: " + path);
				AccessLogLocationsDAO.insertLocation(path);
				resp.getWriter().print("Added: " + path);
				resp.getWriter().flush();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				resp.getWriter().print(e.getMessage());
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}

		logger.debug("existing AccessLogServlet doPost");
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("entering AccessLogServlet doDelete...");

		String path = PathUtils.extractPath(req);
		logger.debug("path=" + path);
		if (path != null) {
			if (!path.endsWith(ALL)) {
				logger.debug("removing access location: " + path);
				try {
					AccessLogLocationsDAO.deleteLocation(path);
					resp.getWriter().print("Removed: " + path);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					resp.getWriter().print(e.getMessage());
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				logger.debug("removing all access locations");
				try {
					AccessLogLocationsDAO.deleteAllLocations();
					resp.getWriter().print("Removed All");
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					resp.getWriter().print(e.getMessage());
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
			resp.getWriter().flush();
		}
		logger.debug("existing AccessLogServlet doDelete");
	}

}
