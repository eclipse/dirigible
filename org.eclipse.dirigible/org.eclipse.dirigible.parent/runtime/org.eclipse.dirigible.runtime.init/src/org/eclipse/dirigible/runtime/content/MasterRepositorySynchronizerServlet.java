/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.task.IRunnableTask;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

/**
 * Enumerate and register the named DataSources on startup
 */
public class MasterRepositorySynchronizerServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;

	private static final Logger logger = Logger.getLogger(MasterRepositorySynchronizerServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void registerInitRegister() {
		TaskManagerShort.getInstance().registerRunnableTask(new MasterRepositorySynchronizerRegister(this));
		logger.info("Master Repository Synchronizer Register has been registered");
	}

	class MasterRepositorySynchronizerRegister implements IRunnableTask {

		MasterRepositorySynchronizerServlet contentInitializerServlet;

		MasterRepositorySynchronizerRegister(MasterRepositorySynchronizerServlet contentInitializerServlet) {
			this.contentInitializerServlet = contentInitializerServlet;
		}

		@Override
		public String getName() {
			return "Master Repository Synchronizer Register";
		}

		@Override
		public void start() {
			boolean ok = true;
			try {
				synchronizeMasterRepository(null);
			} catch (ServletException e) {
				logger.error(e.getMessage(), e);
				ok = false;
			}
			if (ok) {
				TaskManagerShort.getInstance().unregisterRunnableTask(this);
				logger.info("Master Repository Synchronizer Register has been un-registered");
			}
		}

	}

	private void synchronizeMasterRepository(HttpServletRequest request) throws ServletException {
		try {
			IRepository repository = RepositoryFacade.getInstance().getRepository(request);
			IMasterRepository masterRepository = RepositoryFacade.getInstance().getMasterRepository(request);
			if (masterRepository != null) {
				copyRepository(masterRepository, repository);
			} else {
				logger.info("Master Repository is not used in this instance");
			}

		} catch (Exception e) {
			throw new ServletException("Initializing local database for Repository use failed", e);
		}
	}

	private void copyRepository(IMasterRepository sourceRepository, IRepository targetRepository) throws IOException {
		ICollection root = sourceRepository.getRoot();
		copyCollection(root, targetRepository);
	}

	private void copyCollection(ICollection parent, IRepository targetRepository) throws IOException {
		List<IEntity> entities = parent.getChildren();
		for (IEntity entity : entities) {
			if (entity instanceof ICollection) {
				ICollection collection = (ICollection) entity;
				copyCollection(collection, targetRepository);
			} else {
				IResource resource = (IResource) entity;
				targetRepository.createResource(resource.getPath(), resource.getContent(), resource.isBinary(), resource.getContentType(), true);
				logger.info(String.format("Initial copy from the Mater Repository of the Resource: %s", resource.getPath()));
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		synchronizeMasterRepository(req);
	}

}
