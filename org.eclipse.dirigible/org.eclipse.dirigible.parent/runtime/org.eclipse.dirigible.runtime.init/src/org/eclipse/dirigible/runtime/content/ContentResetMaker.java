package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.PermissionsUtils;

/**
 * Removes the non-default content from Repository
 *
 */
public class ContentResetMaker {
	
	private static final Logger logger = Logger.getLogger(ContentResetMaker.class);

	public void doReset(HttpServletRequest request, IRepository repository) throws ServletException, IOException {
		// TODO specific role!!!
		if (PermissionsUtils.isUserInRole(request, IRoles.ROLE_OPERATOR)) {
			logger.debug("Reset called...");
			repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_REGISTRY);
			logger.debug("Reset - registry content removed.");
			repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_SANDBOX);
			logger.debug("Reset - sandbox content removed.");
			repository.removeCollection(IRepositoryPaths.DB_DIRIGIBLE_USERS);
			logger.debug("Reset - users content removed.");
			logger.debug("Reset done.");
		} else {
			String err = String.format(PermissionsUtils.PERMISSION_ERR, "Reset");
			logger.debug(err);
			throw new ServletException(err);
		}
	}

}
