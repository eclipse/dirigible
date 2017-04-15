package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JavaScriptRegistryServlet
 */
@WebServlet({ "/services/registry-js/*", "/services/scripting/javascript/*" })
public class JavaScriptRegistryServlet extends org.eclipse.dirigible.runtime.registry.JavaScriptRegistryServlet {
	private static final long serialVersionUID = 1L;
}
