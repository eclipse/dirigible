package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MessagingReceiveServlet
 */
@WebServlet({ "/message/receive/*" })
public class MessagingReceiveServlet extends org.eclipse.dirigible.runtime.messaging.MessagingReceiveServlet {
	private static final long serialVersionUID = 1L;
}
