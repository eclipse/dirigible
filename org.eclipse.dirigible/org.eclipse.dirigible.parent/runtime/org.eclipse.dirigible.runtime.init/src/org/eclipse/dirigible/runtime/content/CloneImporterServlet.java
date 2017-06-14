package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.PermissionsUtils;

/**
 * Imports cloned content from another instance.
 * Can be used for backups as well.
 */
public class CloneImporterServlet extends ContentImporterServlet {

	private static final long serialVersionUID = -7275411031828315889L;

	private static final Logger logger = Logger.getLogger(CloneImporterServlet.class);

	private static final String CLONE_PATH_FOR_IMPORT = IRepositoryPaths.DB_DIRIGIBLE_BASE.substring(0,
			IRepositoryPaths.DB_DIRIGIBLE_BASE.length() - 1);

	private static final String PARAMETER_RESET = "reset";

	private static final String HEADER_RESET = "reset";

	@Override
	protected String getDefaultPathForImport() {
		return CLONE_PATH_FOR_IMPORT;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!PermissionsUtils.isUserInRole(request, IRoles.ROLE_OPERATOR)) {
			String err = String.format(PermissionsUtils.PERMISSION_ERR, "Import Cloned Content");
			logger.error(err);
			throw new ServletException(err);
		}

		boolean reset = Boolean.parseBoolean(request.getParameter(PARAMETER_RESET)) || Boolean.parseBoolean(request.getHeader(HEADER_RESET));
		if (reset) {
			IRepository repository = getRepository(request);
			ContentResetMaker resetMaker = new ContentResetMaker();
			resetMaker.doReset(request, repository);
		}
		super.doPost(request, response);
	}
}
