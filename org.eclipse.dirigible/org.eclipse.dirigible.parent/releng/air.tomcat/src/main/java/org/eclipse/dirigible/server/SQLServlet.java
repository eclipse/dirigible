package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for SQLServlet
 */
@WebServlet({ "/services/sql/*", "/services/sql-secured/*", "/services/sql-sandbox/*" })
public class SQLServlet extends org.eclipse.dirigible.runtime.sql.SQLServlet {
	private static final long serialVersionUID = 1L;
}
