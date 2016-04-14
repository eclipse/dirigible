/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingServlet extends HttpServlet {

	private static final String B_CLOSE = "</b>";
	private static final String B = "<b>";
	private static final String A_HREF_JAVASCRIPT_LOCATION_RELOAD_TRUE_REFRESH_A = "<a href=\"javascript:location.reload(true);\">Refresh</a>";
	private static final String LOG_FILES_A_CLOSE = "\">Log Files</a>";
	private static final String SMALL_CLOSE = "</small>";
	private static final String SMALL = "<small>";
	private static final String BR = "<br/>";
	private static final String HR = "<hr>";
	private static final String TD_TR = "</td></tr>";
	private static final String TD_TD = "</td><td>";
	private static final String TR_TD = "<tr><td>";
	private static final String A_HREF_END = "</a>";
	private static final String A_HREF_CLOSE = "\">";
	private static final String A_HREF = "<a href=\"";
	private static final String TR_TD_B_LOG_FILES_B_TD_TD_B_LAST_MODIFIED_B_TD_TR = "<tr><td><b>Log Files</b></td><td><b>Last modified</b></td></tr>";
	private static final String TABLE_CLOSE = "</table>";
	private static final String TABLE_OPEN = "<table  border=\"0\">";
	private static final String FONT_CLOSE = "</font>";
	private static final String FONT_ARIAL_OPEN = "<font face=\"arial\">";
	private static final String BAD_INITIAL_CONFIGURATION_PLEASE_SET_PROPERLY_LOGGING_DIRECTORY = "Bad initial configuration [%s]. Please set properly logging directory.";
	private static final String MM_DD_YYYY_HH_MM_SS = "MM/dd/yyyy HH:mm:ss";
	private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
	private static final String LOG_FILE_S_DOSN_T_EXIST = "Log file '%s' dosn't exist!";
	private static final String LOG_PARAMETER = "log";
	private static final String EMPTY_STRING = "";
	private static final String DOT = ".";
	private static final String INIT_LOGGING_DIRECTORY = "initLoggingDirectory";
	private static final String HTML_START = "<!DOCTYPE html><html><body>";
	private static final String HTML_END = "</body></html>";
	private static final String LOGGING_FILES_LIST_LOCATION = "logging";
	private static final String LOGGING_FILE_LOCATION = LOGGING_FILES_LIST_LOCATION + "?log=";
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(LoggingServlet.class.getCanonicalName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String initLoggingDirectory = System.getProperty(INIT_LOGGING_DIRECTORY);
		if (initLoggingDirectory == null) {
			initLoggingDirectory = getInitParameter(INIT_LOGGING_DIRECTORY);
		}
		String serverFileSystemPath = getServerFileSystemPath();

		File loggingDirectory = new File(serverFileSystemPath + initLoggingDirectory);
		if (!loggingDirectory.exists()) {
			String err = String.format(BAD_INITIAL_CONFIGURATION_PLEASE_SET_PROPERLY_LOGGING_DIRECTORY, loggingDirectory.getCanonicalPath());
			response.sendError(500, err);
			logger.severe(err);

		} else {
			File[] loggingFiles = loggingDirectory.listFiles();
			String logFile = request.getParameter(LOG_PARAMETER);
			if ((logFile != null) && !logFile.equals(EMPTY_STRING)) {
				printLogFile(response, loggingFiles, logFile);
			} else {
				printLogFilesList(response, loggingDirectory);
			}
		}
	}

	private void printLogFilesList(HttpServletResponse response, File loggingDirectory) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY_HH_MM_SS);

		StringBuilder fileLinks = new StringBuilder(HTML_START);
		fileLinks.append(FONT_ARIAL_OPEN);
		fileLinks.append(TABLE_OPEN);
		fileLinks.append(TR_TD_B_LOG_FILES_B_TD_TD_B_LAST_MODIFIED_B_TD_TR);

		for (File loggingFile : loggingDirectory.listFiles()) {
			String fileName = XSSUtils.stripXSS(loggingFile.getName());
			String lastModified = XSSUtils.stripXSS(dateFormat.format(loggingFile.lastModified()));
			String a = A_HREF + LOGGING_FILE_LOCATION + fileName + A_HREF_CLOSE + fileName + A_HREF_END;
			fileLinks.append(TR_TD + a + TD_TD + lastModified + TD_TR);
		}

		fileLinks.append(TABLE_CLOSE);
		fileLinks.append(FONT_CLOSE);
		fileLinks.append(HTML_END);

		PrintWriter writer = response.getWriter();
		writer.println(fileLinks.toString());
		writer.flush();
		writer.close();
	}

	private void printLogFile(HttpServletResponse response, File[] loggingFiles, String logFile) throws FileNotFoundException, IOException {
		response.setContentType(CONTENT_TYPE_TEXT_HTML);
		PrintWriter writer = response.getWriter();

		boolean found = false;

		for (File loggingFile : loggingFiles) {
			if (loggingFile.getName().equalsIgnoreCase(logFile)) {
				BufferedReader reader = new BufferedReader(new FileReader(loggingFile));
				String line;
				writer.print(HTML_START);
				writer.print(FONT_ARIAL_OPEN);
				printBeforeLogFile(writer);
				while ((line = reader.readLine()) != null) {
					writer.print(BR);
					writer.println(SMALL + XSSUtils.stripXSS(line) + SMALL_CLOSE);
				}
				printAfterLogFile(writer);
				writer.print(FONT_CLOSE);
				writer.print(HTML_END);
				writer.flush();
				reader.close();
				found = true;
				break;
			}
		}
		if (!found) {
			writer.println(String.format(LOG_FILE_S_DOSN_T_EXIST, logFile));
			writer.flush();
		}
		writer.close();
	}

	private void printBeforeLogFile(PrintWriter writer) {
		printScript(writer);
		writer.print(HR);
	}

	private void printAfterLogFile(PrintWriter writer) {
		writer.print(HR);
		printScript(writer);
	}

	private void printScript(PrintWriter writer) {
		writer.print(TABLE_OPEN);
		String linkLogList = A_HREF + LOGGING_FILES_LIST_LOCATION + LOG_FILES_A_CLOSE;
		String linkRefresh = A_HREF_JAVASCRIPT_LOCATION_RELOAD_TRUE_REFRESH_A;
		writer.print(TR_TD + B + linkLogList + B_CLOSE + TD_TD + B + linkRefresh + B_CLOSE + TD_TR);
		writer.print(TABLE_CLOSE);
		writer.flush();
	}

	private String getServerFileSystemPath() throws IOException {
		String binDir = new File(DOT).getCanonicalPath();
		binDir = binDir.replace("\\", "/");
		String logDir = binDir.substring(0, binDir.lastIndexOf('/') + 1);
		return logDir;
	}

}
