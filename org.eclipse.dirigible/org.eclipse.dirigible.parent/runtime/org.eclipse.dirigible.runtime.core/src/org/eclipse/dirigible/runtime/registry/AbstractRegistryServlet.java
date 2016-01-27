/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

/**
 * Servlet implementation class AbstractRegistryServlet
 */
public abstract class AbstractRegistryServlet extends HttpServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	protected static final String REPOSITORY_ATTRIBUTE = "org.eclipse.dirigible.services.registry.repository"; //$NON-NLS-1$

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AbstractRegistryServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		initRepository(request);
		super.service(request, response);

	}

	private IRepository initRepository(HttpServletRequest request) throws ServletException {
		try {
			final IRepository repository = RepositoryFacade.getInstance().getRepository(request);
			if (request != null) {
				request.getSession().setAttribute(REPOSITORY_ATTRIBUTE, repository);
			}
			return repository;
			// getServletContext().setAttribute(REPOSITORY_ATTRIBUTE, repository);
		} catch (Exception ex) {
			throw new ServletException("Could not initialize Repository", ex);
		}
	}

	protected IRepository getRepository(HttpServletRequest request) throws IOException {
		IRepository repository = null;
		if (request != null) {
			repository = (IRepository) request.getSession().getAttribute(REPOSITORY_ATTRIBUTE);
		}
		if (repository == null) {
			try {
				repository = initRepository(request);
			} catch (Exception e) {
				log(e.getMessage(), e);
				throw new IOException(e);
			}
		}
		return repository;
	}

	protected String extractRepositoryPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		return getRepositoryPathPrefix(request) + requestPath;
	}

	protected String getRepositoryPathPrefix(HttpServletRequest req) {
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH;
	}

	protected IEntity getEntity(String repositoryPath, HttpServletRequest request) throws FileNotFoundException, IOException {
		IEntity result = null;
		final IRepository repository = getRepository(request);
		final IResource resource = repository.getResource(repositoryPath);
		if (!resource.exists()) {
			final ICollection collection = repository.getCollection(repositoryPath);
			if (collection.exists()) {
				result = collection;
			}
			// else {
			// throw new
			// FileNotFoundException("There is no collection or resource at the path specified: "
			// + repositoryPath);
			// }
		} else {
			result = resource;
		}
		return result;
	}

	protected byte[] readResourceData(IResource resource) throws IOException {
		return resource.getContent();
	}

	protected void sendData(OutputStream out, byte[] data) throws IOException {
		out.write(data);
	}

}
