package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for TestCaseServlet
 */
@WebServlet({ "/services/test/*", "/services/test-secured/*", "/services/test-sandbox/*" })
public class TestCaseServlet extends org.eclipse.dirigible.runtime.js.TestCasesServlet {
	private static final long serialVersionUID = 1L;
}
