package org.eclipse.dirigible.server.log;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for ListenerLogServlet
 */
@WebServlet({ "/services/flow-log/*" })
public class ListenerLogServlet extends org.eclipse.dirigible.runtime.listener.log.ListenerLogServlet {
	private static final long serialVersionUID = 1L;
}
