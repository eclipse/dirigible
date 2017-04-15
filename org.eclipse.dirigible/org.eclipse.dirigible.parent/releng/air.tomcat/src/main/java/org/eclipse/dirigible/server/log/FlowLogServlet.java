package org.eclipse.dirigible.server.log;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for FlowLogServlet
 */
@WebServlet({ "/services/flow-log/*" })
public class FlowLogServlet extends org.eclipse.dirigible.runtime.flow.log.FlowLogServlet {
	private static final long serialVersionUID = 1L;
}
