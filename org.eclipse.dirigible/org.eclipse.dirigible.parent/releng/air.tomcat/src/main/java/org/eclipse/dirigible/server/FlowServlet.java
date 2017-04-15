package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for FlowServlet
 */
@WebServlet({ "/services/flow/*", "/services/flow-secured/*", "/services/flow-sandbox/*" })
public class FlowServlet extends org.eclipse.dirigible.runtime.flow.FlowServlet {
	private static final long serialVersionUID = 1L;
}
