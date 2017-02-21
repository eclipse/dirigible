package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JavaScriptServlet
 */
@WebServlet({ "/services/js/*", "/services/js-secured/*" })
public class JavaScriptServlet extends org.eclipse.dirigible.runtime.js.JavaScriptServlet {
	private static final long serialVersionUID = 1L;
}
