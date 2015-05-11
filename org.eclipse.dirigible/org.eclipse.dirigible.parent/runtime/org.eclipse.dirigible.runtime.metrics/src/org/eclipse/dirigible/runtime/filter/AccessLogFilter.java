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

package org.eclipse.dirigible.runtime.filter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.metrics.AccessLogLocationsSynchronizer;
import org.eclipse.dirigible.runtime.metrics.AccessLogRecord;
import org.eclipse.dirigible.runtime.metrics.AccessLogRecordDAO;
import org.eclipse.dirigible.runtime.registry.PathUtils;

public class AccessLogFilter implements Filter {

	private static final Logger logger = Logger.getLogger(AccessLogFilter.class);

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String location = PathUtils.extractPath(req);
		AccessLogRecord accessLogRecord = null;
		boolean logLocation = isAccessLogEnabled(location);
		if (logLocation) {
			String pattern = getAccessLogPattern(location);
			accessLogRecord = new AccessLogRecord(req, pattern);
		}
		try {
			chain.doFilter(request, response);
		} finally {
			if (logLocation) {
				try {
					accessLogRecord.setResponseStatus(((HttpServletResponse) response).getStatus());
					accessLogRecord
							.setResponseTime((int) (GregorianCalendar.getInstance().getTime().getTime() - accessLogRecord
									.getTimestamp().getTime()));

					AccessLogRecordDAO.insert(accessLogRecord);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	private boolean isAccessLogEnabled(String location) throws ServletException {
		logger.trace("isAccessLogEnabled: " + location);
		for (String accessLogLocation : AccessLogLocationsSynchronizer.getAccessLogLocations()) {
			if (location.startsWith(accessLogLocation)) {
				logger.debug("Access Log Enabled: " + location);
				return true;
			}
		}
		logger.debug("Access Log Not Enabled: " + location);
		return false;
	}

	private String getAccessLogPattern(String location) throws ServletException {
		logger.debug("entering getAccessLogPattern: " + location);
		for (String accessLogLocation : AccessLogLocationsSynchronizer.getAccessLogLocations()) {
			if (location.startsWith(accessLogLocation)) {
				logger.debug("Access Log for Location: " + location + " by pattern: "
						+ accessLogLocation);
				return accessLogLocation;
			}
		}
		logger.debug("exiting getAccessLogPattern: " + location);
		return null;
	}

}
