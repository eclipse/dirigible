/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class ContentBaseServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;
	
	static final String COULD_NOT_INITIALIZE_REPOSITORY = Messages
			.getString("ContentInitializerServlet.COULD_NOT_INITIALIZE_REPOSITORY"); //$NON-NLS-1$
	
	static final String REPOSITORY_ATTRIBUTE = "org.eclipse.dirigible.services.content.repository"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(ContentBaseServlet.class);

	static final String SYSTEM_USER = "SYSTEM"; //$NON-NLS-1$
	
	
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ContentBaseServlet() {
		super();
	}

	/**
	 * Helper method.
	 * 
	 * @return
	 * @throws IOException
	 */
	private IRepository initRepository(HttpServletRequest request) throws ServletException {
		try {
			IRepository repository = RepositoryFacade.getInstance().getRepository(request);
			return repository;
		} catch (Exception ex) {
			logger.error("Exception in initRepository(): " + ex.getMessage(), ex); //$NON-NLS-1$
			throw new ServletException(COULD_NOT_INITIALIZE_REPOSITORY, ex);
		}
	}

	/**
	 * Helper method.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected IRepository getRepository(HttpServletRequest request) throws IOException {
		try {
			return initRepository(request);
		} catch (ServletException e) {
			logger.error("Exception in getRepository(): " + e.getMessage(), e); //$NON-NLS-1$
			throw new IOException(e);
		}
	}

	

}