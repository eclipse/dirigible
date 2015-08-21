package org.eclipse.dirigible.runtime.messaging;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;
import org.eclipse.dirigible.repository.ext.messaging.MessageDefinition;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

import com.google.gson.Gson;

public class MessagingReceiveServlet extends MessagingServlet {
	
	private static final Logger logger = Logger.getLogger(MessagingReceiveServlet.class);

	private static final long serialVersionUID = -8984115994661072558L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String topic = req.getParameter(PARAMETER_TOPIC);
		
		List<MessageDefinition> messages = null;
		try {
			if (topic == null
					|| "".equals(topic.trim())) {
				messages = getMessageHub(req).receive(
						RepositoryFacade.getUser(req));
			} else {
				messages = getMessageHub(req).receiveByTopic(
						RepositoryFacade.getUser(req), topic);
			}
			
			resp.setContentType("application/json");
			
			Gson gson = new Gson();
			resp.getWriter().println(gson.toJson(messages));
			resp.getWriter().flush();
			resp.getWriter().close();
			
		} catch (EMessagingException e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}

	}
	
	

}
