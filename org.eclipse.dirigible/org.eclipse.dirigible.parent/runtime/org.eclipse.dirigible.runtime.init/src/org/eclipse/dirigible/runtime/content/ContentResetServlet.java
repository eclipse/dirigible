package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Removes the non-default content from Repository
 *
 */
public class ContentResetServlet extends ContentBaseServlet {

	private static final long serialVersionUID = 6896905886376599103L;
	
	private static final Logger logger = Logger.getLogger(ContentResetServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("Reset servlet called...");
		IRepository repository = getRepository(req);
		ContentResetMaker resetMaker = new ContentResetMaker();
		resetMaker.doReset(req, repository);
	}

}
