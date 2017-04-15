package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for ListenerRegistryServlet
 */
@WebServlet({ "/services/registry-listener/*", "/services/flow/listener/*", "/services/flow/listener-secured/*" })
public class ListenerRegistryServlet extends org.eclipse.dirigible.runtime.listener.ListenerRegistryServlet {
	private static final long serialVersionUID = 1L;
}
