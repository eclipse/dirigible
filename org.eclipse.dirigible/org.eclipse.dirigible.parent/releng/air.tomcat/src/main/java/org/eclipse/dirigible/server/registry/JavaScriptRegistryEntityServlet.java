package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JavaScriptRegistryEntityServlet
 */
@WebServlet({ "/services/registry-js-entity/*" })
public class JavaScriptRegistryEntityServlet extends org.eclipse.dirigible.runtime.registry.JavaScriptRegistryEntityServlet {
	private static final long serialVersionUID = 1L;
}
