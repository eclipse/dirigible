package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for SQLRegistryServlet
 */
@WebServlet({ "/services/registry-sql/*", "/services/scripting/sql/*" })
public class SQLRegistryServlet extends org.eclipse.dirigible.runtime.registry.SQLRegistryServlet {
	private static final long serialVersionUID = 1L;
}
