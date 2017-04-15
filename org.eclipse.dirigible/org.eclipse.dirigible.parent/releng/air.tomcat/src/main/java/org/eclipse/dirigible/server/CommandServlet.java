package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for CommandServlet
 */
@WebServlet({ "/services/command/*", "/services/command-secured/*", "/services/command-sandbox/*" })
public class CommandServlet extends org.eclipse.dirigible.runtime.js.JavaScriptServlet {
	private static final long serialVersionUID = 1L;
}
