package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JavaScriptRegistryServlet
 */
@WebServlet({ "/services/js-src/*" })
public class JavaScriptSourceRegistryServlet extends org.eclipse.dirigible.runtime.js.debug.JavaScriptSourceRegistryServlet {
	private static final long serialVersionUID = 1L;
}
