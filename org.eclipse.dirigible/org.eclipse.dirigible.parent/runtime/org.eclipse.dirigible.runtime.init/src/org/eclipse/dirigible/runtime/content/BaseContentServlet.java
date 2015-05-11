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

public class BaseContentServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;
	
	static final String COM_SAP_DIRIGIBLE_RUNTIME = "org.eclipse.dirigible.runtime"; //$NON-NLS-1$

	static final String COULD_NOT_INITIALIZE_REPOSITORY = Messages
			.getString("ContentInitializerServlet.COULD_NOT_INITIALIZE_REPOSITORY"); //$NON-NLS-1$
		static final String REPOSITORY_ATTRIBUTE = "org.eclipse.dirigible.services.content.repository"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(BaseContentServlet.class);

	static final String SYSTEM_USER = "SYSTEM"; //$NON-NLS-1$
	static final String DEFAULT_PATH_FOR_EXPORT = IRepositoryPaths.REGISTRY_DEPLOY_PATH;
	static final String DEFAULT_PATH_FOR_IMPORT = IRepositoryPaths.REGISTRY_IMPORT_PATH; //$NON-NLS-1$

	// Folder in the generated zip-file (org.eclipse.dirigible.runtime*.zip) for
	// exported content
	static final String EXPORTED_PATH = "exported/"; //$NON-NLS-1$
	static final String EXPORTED_CONTENT = "content"; //$NON-NLS-1$
	static final String ZIP = ".zip"; //$NON-NLS-1$

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BaseContentServlet() {
		super();
	}

//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		super.init(config);
//		logger.debug("Base Content Servlet Init"); //$NON-NLS-1$
//		initRepository();
//		try {
//			checkAndImportContent(SYSTEM_USER);
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}
//		logger.debug("Base Content Servlet Init finished successfuly."); //$NON-NLS-1$
//	}

	/**
	 * Helper method.
	 * 
	 * @return
	 * @throws IOException
	 */
	private IRepository initRepository(HttpServletRequest request) throws ServletException {
		try {
			IRepository repository = RepositoryFacade.getInstance().getRepository(request);
//			getServletContext().setAttribute(REPOSITORY_ATTRIBUTE, repository);
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
//		final IRepository repository = (IRepository) getServletContext().getAttribute(
//				REPOSITORY_ATTRIBUTE);
//		if (repository == null) {
			try {
				return initRepository(request);
			} catch (ServletException e) {
				logger.error("Exception in getRepository(): " + e.getMessage(), e); //$NON-NLS-1$
				throw new IOException(e);
			}
//		}
//		return repository;
	}

//	/**
//	 * Check if there is any content, and deploy it into Dirigible registry.
//	 * 
//	 * @throws IOException
//	 */
//	private void checkAndImportContent(String user, HttpServletRequest request) throws IOException {
//		logger.debug("checkAndImporContent: Entering"); //$NON-NLS-1$
//		if (existsContentToImport()) {
//			// Clean registry from eventual old content
//			ICollection collection = getRepository().getCollection(DEFAULT_PATH_FOR_IMPORT);
//			if (collection.exists()) {
//				collection.delete();
//				collection.create();
//			}
//			// Get all files under CONTENT folder. Possibility to import
//			// multiple content files. TODO - is it necessary?
//			Set<String> paths = getServletContext().getResourcePaths(
//					IRepository.SEPARATOR + EXPORTED_PATH); //$NON-NLS-1$
//			logger.debug("resource paths:" + paths); //$NON-NLS-1$
//			for (Iterator<String> iterator = paths.iterator(); iterator.hasNext();) {
//				String pathToContent = (String) iterator.next();
//				logger.debug("Path to content: " + pathToContent); //$NON-NLS-1$
//				if (!pathToContent.endsWith(ZIP)) {
//					continue;
//				}
//				InputStream content = getServletContext().getResourceAsStream(pathToContent);
//				importZipAndUpdate(content, request);
//				logger.debug(" Successfully imported " + pathToContent); //$NON-NLS-1$
//			}
//		} else {
//			logger.debug(" Nothing to import. Folder" + IRepository.SEPARATOR + EXPORTED_PATH //$NON-NLS-1$
//					+ " was not found in deployable."); //$NON-NLS-1$
//
//		}
//	}

	/**
	 * Import input stream as a content into repository and execute db updates.
	 * TODO - can be used with POST request to import only a zip
	 * 
	 * @param content
	 */
	public void importZipAndUpdate(InputStream content, HttpServletRequest request) {
		importZipAndUpdate(content, request, false);
	}

	public void importZipAndUpdate(InputStream content, HttpServletRequest request, boolean override) {
		importZipAndUpdate(content, DEFAULT_PATH_FOR_IMPORT, request, override);
	}

	public void importZipAndUpdate(InputStream content, String pathForImport, HttpServletRequest request) {
		importZipAndUpdate(content, pathForImport, request, false);
	}

	public void importZipAndUpdate(InputStream content, String pathForImport, HttpServletRequest request, boolean override) {
		try {
			// 1. Import content.zip into repository
			getRepository(request).importZip(new ZipInputStream(content), pathForImport, override);

			postImport(request);

		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void postImport(HttpServletRequest request) throws IOException,
			Exception {
		// 2. Post import actions
		ContentPostImportUpdater contentPostImportUpdater = new ContentPostImportUpdater(
				getRepository(request));
		contentPostImportUpdater.update(request);
	}

//	/**
//	 * Check if the path exists in current dirigible.runtime.war
//	 * 
//	 * @return
//	 */
//	private boolean existsContentToImport() {
//		Set<String> paths = getServletContext().getResourcePaths(IRepository.SEPARATOR);
//		return paths.contains(IRepository.SEPARATOR + EXPORTED_PATH);
//	}

}