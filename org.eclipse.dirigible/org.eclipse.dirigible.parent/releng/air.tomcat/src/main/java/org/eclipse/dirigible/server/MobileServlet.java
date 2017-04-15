package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MobileServlet
 */
@WebServlet({ "/services/mobile/*", "/services/mobile-secured/*", "/services/mobile-sandbox/*" })
public class MobileServlet extends org.eclipse.dirigible.runtime.mobile.MobileRegistryServlet {
	private static final long serialVersionUID = 1L;
}
