package org.eclipse.dirigible.runtime.messaging;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.messaging.MessageHub;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MessagingServlet extends HttpServlet {

	private static final long serialVersionUID = -8824923926986449674L;
	
	protected static final String PARAMETER_SUBJECT = "subject";

	protected static final String PARAMETER_TOPIC = "topic";

	protected MessageHub getMessageHub(HttpServletRequest req) {
		return new MessageHub(RepositoryFacade.getInstance().getDataSource(), req);
	}
	
	
	

}
