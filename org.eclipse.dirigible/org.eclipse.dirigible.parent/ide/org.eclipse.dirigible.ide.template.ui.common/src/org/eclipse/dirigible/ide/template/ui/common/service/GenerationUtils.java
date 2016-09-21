package org.eclipse.dirigible.ide.template.ui.common.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public class GenerationUtils {

	private static final String COULD_NOT_INITIALIZE_REPOSITORY = "Could not initialize Repository"; //$NON-NLS-1$

	private static final String COULD_NOT_INITIALIZE_WORKSPACE = "Could not initialize Workspace"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(GenerationUtils.class);

	public static IRepository getRepository(HttpServletRequest request) throws ServletException {

		try {
			final IRepository repository = RepositoryFacade.getInstance().getRepository(request);
			return repository;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new ServletException(COULD_NOT_INITIALIZE_REPOSITORY, ex);
		}
	}

	public static IWorkspace getWorkspace(HttpServletRequest request) throws ServletException {
		try {
			final IWorkspace workspace = WorkspaceLocator.getWorkspace(request);
			return workspace;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new ServletException(COULD_NOT_INITIALIZE_WORKSPACE, ex);
		}
	}

}
