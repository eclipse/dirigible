package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

/**
 * Removes the non-default content from Repository
 *
 */
public class ContentResetServlet extends ContentBaseServlet {

	private static final long serialVersionUID = 6896905886376599103L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO specific role!!!
		IRepository repository = getRepository(req);
		repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_REGISTRY);
		repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_SANDBOX);
		repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_USERS);
	}

}
