package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JavaScriptRegistrySwaggerServlet
 */
@WebServlet({ "/services/registry-js-swagger/*" })
public class JavaScriptRegistrySwaggerServlet extends org.eclipse.dirigible.runtime.registry.JavaScriptRegistrySwaggerServlet {
	private static final long serialVersionUID = 1L;
}
