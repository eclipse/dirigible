package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for FlowRegistryServlet
 */
@WebServlet({ "/services/registry-flow/*", "/services/flow/flow/*", "/services/flow/flow-secured/*" })
public class FlowRegistryServlet extends org.eclipse.dirigible.runtime.flow.FlowRegistryServlet {
	private static final long serialVersionUID = 1L;
}
