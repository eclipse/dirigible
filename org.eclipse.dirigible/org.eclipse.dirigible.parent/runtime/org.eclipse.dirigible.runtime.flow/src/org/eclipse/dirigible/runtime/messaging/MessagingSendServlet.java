package org.eclipse.dirigible.runtime.messaging;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MessagingSendServlet extends MessagingServlet {
	
	private static final Logger logger = Logger.getLogger(MessagingSendServlet.class);

	private static final long serialVersionUID = -8984115994661072558L;
	
	public static final String PARAMETERS_ERR = "Parameters 'topic' and 'subject' are not present. Use .../message/send?topic=XXX&subject=YYY";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String topic = req.getParameter(PARAMETER_TOPIC);
		String subject = req.getParameter(PARAMETER_SUBJECT);
		String body = IOUtils.toString(req.getInputStream());
		if (topic == null
				|| "".equals(topic.trim())
				|| subject == null
				|| "".equals(subject.trim())) {
			logger.error(PARAMETERS_ERR);
			throw new ServletException(PARAMETERS_ERR);
		}
		try {
			getMessageHub(req).send(
					RepositoryFacade.getUser(req), topic, subject, body);
			resp.getWriter().println("Message sent successfully.");
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (EMessagingException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}

	}
	
	

}
