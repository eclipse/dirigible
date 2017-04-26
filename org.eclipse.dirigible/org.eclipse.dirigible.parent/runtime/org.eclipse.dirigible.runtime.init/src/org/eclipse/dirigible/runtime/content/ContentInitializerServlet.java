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
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.task.IRunnableTask;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

/**
 * Checks and imports if needed the default content into the Registry on startup
 */
public class ContentInitializerServlet extends HttpServlet {

	private static final String INIT_FLAG_FILE_NAME = "/db/dirigible/default.content"; //$NON-NLS-1$

	private static final String CONTENT_FILE_NAME = "/content/repository.zip"; //$NON-NLS-1$

	private static final long serialVersionUID = 6468050094756163896L;

	private static final Logger logger = Logger.getLogger(ContentInitializerServlet.class);

	private static final String PATH_REGISTY_ROOT_TARGET = "/db"; //$NON-NLS-1$

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void registerInitRegister() {
		TaskManagerShort.getInstance().registerRunnableTask(new ContentInitializerRegister(this));
		logger.info("Content Initializer Register has been registered");
	}

	class ContentInitializerRegister implements IRunnableTask {

		ContentInitializerServlet contentInitializerServlet;

		ContentInitializerRegister(ContentInitializerServlet contentInitializerServlet) {
			this.contentInitializerServlet = contentInitializerServlet;
		}

		@Override
		public String getName() {
			return "Content Initializer Register";
		}

		@Override
		public void start() {
			boolean ok = false;
			try {
				ok = initDefaultContent(null);
			} catch (ServletException e) {
				logger.error(e.getMessage(), e);
			}
			if (ok) {
				TaskManagerShort.getInstance().unregisterRunnableTask(this);
				logger.info("Content Initializer Register has been un-registered");
			}
		}

	}

	public boolean initDefaultContent(HttpServletRequest request) throws ServletException {

		ContentImporterServlet contentImporterServlet = new ContentImporterServlet();

		try {
			IRepository repository = contentImporterServlet.getRepository(request);
			if (repository == null) {
				return false;
			}
		} catch (IOException e1) {
			return false;
		}
		try {
			// TODO better check for content init done
			IResource resource = contentImporterServlet.getRepository(request).getResource(INIT_FLAG_FILE_NAME);
			if (!resource.exists()) {
				logger.info("Initializing default content..."); //$NON-NLS-1$
				contentImporterServlet.importZipAndUpdate(getContentZip(), PATH_REGISTY_ROOT_TARGET, request);
				logger.info("Default content initialized."); //$NON-NLS-1$
			} else {
				logger.info("Post import actions..."); //$NON-NLS-1$
				contentImporterServlet.postImport(request);
				logger.info("Post import actions done."); //$NON-NLS-1$
			}
			// --
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return true;
	}

	private InputStream getContentZip() {
		InputStream in = this.getClass().getClassLoader().getSystemClassLoader().getResourceAsStream(CONTENT_FILE_NAME);
		if (in == null) {
			in = this.getClass().getClassLoader().getParent().getResourceAsStream(CONTENT_FILE_NAME);
		}
		if (in == null) {
			in = this.getClass().getResourceAsStream(CONTENT_FILE_NAME);
		}
		return in;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		initDefaultContent(req);
	}

}
