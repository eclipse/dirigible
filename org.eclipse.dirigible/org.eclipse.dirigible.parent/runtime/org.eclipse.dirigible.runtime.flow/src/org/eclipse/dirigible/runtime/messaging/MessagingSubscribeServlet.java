package org.eclipse.dirigible.runtime.messaging;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MessagingSubscribeServlet extends MessagingServlet {
	
	private static final Logger logger = Logger.getLogger(MessagingSubscribeServlet.class);

	private static final long serialVersionUID = -8984115994661072558L;
	
	public static final String PARAMETERS_ERR = "Parameter 'topic' is not present. Use .../message/subscribe?topic=XXX";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String topic = req.getParameter(PARAMETER_TOPIC);
		if (topic == null
				|| "".equals(topic.trim())) {
			logger.error(PARAMETERS_ERR);
			throw new ServletException(PARAMETERS_ERR);
		}
		try {
			getMessageHub(req).subscribe(
					RepositoryFacade.getUser(req), topic);
			resp.getWriter().println("User subscribed successfully.");
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (EMessagingException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}

	}
	
	

}
