package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MessagingSubscribeServlet
 */
@WebServlet({ "/message/subscribe/*" })
public class MessagingSubscribeServlet extends org.eclipse.dirigible.runtime.messaging.MessagingSubscribeServlet {
	private static final long serialVersionUID = 1L;
}
