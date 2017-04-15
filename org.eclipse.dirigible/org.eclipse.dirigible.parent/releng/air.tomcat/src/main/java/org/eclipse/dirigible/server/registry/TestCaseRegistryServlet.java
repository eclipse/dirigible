package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for TestCaseRegistryServlet
 */
@WebServlet({ "/services/registry-tc/*", "/services/scripting/tests/*" })
public class TestCaseRegistryServlet extends org.eclipse.dirigible.runtime.registry.TestCasesRegistryServlet {
	private static final long serialVersionUID = 1L;
}
