package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

import org.eclipse.dirigible.runtime.web.WebRegistryServlet;

/**
 * Wrapper for WebServlet
 */
@WebServlet({ "/services/web/*", "/services/web-secured/*", "/services/web-sandbox/*" })
public class WebContentServlet extends WebRegistryServlet {
	private static final long serialVersionUID = 1L;

}
