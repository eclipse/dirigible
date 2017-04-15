package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MessagingSendServlet
 */
@WebServlet({ "/message/send/*" })
public class MessagingSendServlet extends org.eclipse.dirigible.runtime.messaging.MessagingSendServlet {
	private static final long serialVersionUID = 1L;
}
