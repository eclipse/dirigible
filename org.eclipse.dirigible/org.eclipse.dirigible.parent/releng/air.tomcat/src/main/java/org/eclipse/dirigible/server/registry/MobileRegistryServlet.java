package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MobileRegistryServlet
 */
@WebServlet({ "/services/registry-mobile/*", "/services/scripting/mobile/*", "/services/scripting/mobile/content/*",
		"/services/scripting/mobile/content-secured/*" })
public class MobileRegistryServlet extends org.eclipse.dirigible.runtime.mobile.MobileRegistryServlet {
	private static final long serialVersionUID = 1L;
}
