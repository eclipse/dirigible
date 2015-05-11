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

public class ContentInitializerServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;
	
	private static final Logger logger = Logger.getLogger(ContentInitializerServlet.class);
	
//	private static final String INVALID_FILE_OR_FOLDER_LOCATION_DURING_CONTENT_INITIALIZATION = Messages
//			.getString("ContentInitializerServlet.INVALID_FILE_OR_FOLDER_LOCATION_DURING_CONTENT_INITIALIZATION"); //$NON-NLS-1$
//	private static final String CONTENT_INITIALIZATION_FAILED = Messages
//			.getString("ContentInitializerServlet.CONTENT_INITIALIZATION_FAILED"); //$NON-NLS-1$
//	private static final String COULD_NOT_INITIALIZE_REPOSITORY = Messages
//			.getString("ContentInitializerServlet.COULD_NOT_INITIALIZE_REPOSITORY"); //$NON-NLS-1$
//	
//	private static final String REPOSITORY_ATTRIBUTE = "org.eclipse.dirigible.services.content.repository"; //$NON-NLS-1$
//	private static final String PATH_REGISTRY_ROOT_SOURCE = "/WEB-INF/content/db/"; //$NON-NLS-1$
//
	private static final String PATH_REGISTY_ROOT_TARGET = "/db"; //$NON-NLS-1$
//
//	
//
//	private static final String SYSTEM_USER = "SYSTEM"; //$NON-NLS-1$
	
//	public ContentInitializerServlet() {
//		super();
//		registerInitRegister();
//	}
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
//		initDefaultContent(null);
//		registerInitRegister();
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
			IResource resource = contentImporterServlet.getRepository(request).getResource("/db/dirigible/default.content");
			if (!resource.exists()) {
				logger.info("Initializing default content..."); //$NON-NLS-1$
				contentImporterServlet.importZipAndUpdate(this.getClass().getResourceAsStream("/content.zip"), PATH_REGISTY_ROOT_TARGET, request);
				logger.info("Default content initialized."); //$NON-NLS-1$
			} else {
				logger.info("Post import actions..."); //$NON-NLS-1$
				contentImporterServlet.postImport(request);
				logger.info("Post import actions done."); //$NON-NLS-1$
			}
			//--
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return true;
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		initDefaultContent(req);
	}

//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		super.init(config);
//		logger.info("Content Servlet Init"); //$NON-NLS-1$
//		initRepository();
//		try {
//			// 1. Import pre-delivered content
//			checkAndImportRegistry(config, SYSTEM_USER);
//			// 2. Post import actions
//			ContentPostImportUpdater contentPostImportUpdater = new ContentPostImportUpdater(
//					getRepository(SYSTEM_USER));
//			contentPostImportUpdater.update();
//		} catch (IOException e) {
//			throw new ServletException(e);
//		} catch (Exception e) {
//			throw new ServletException(e);
//		}
//	}
//	
//	
//	
//
//	private void initRepository() throws ServletException {
//		try {
//			final IRepository repository = RepositoryFacade.getInstance().getRepository(null);
//			getServletContext().setAttribute(REPOSITORY_ATTRIBUTE, repository);
//		} catch (Exception ex) {
//			throw new ServletException(COULD_NOT_INITIALIZE_REPOSITORY, ex);
//		}
//	}
//
//	private IRepository getRepository(String user) throws IOException {
//		final IRepository repository = (IRepository) getServletContext().getAttribute(
//				REPOSITORY_ATTRIBUTE);
//		if (repository == null) {
//			try {
//				initRepository();
//			} catch (ServletException e) {
//				throw new IOException(e);
//			}
//		}
//		return repository;
//	}
//
//	private void checkAndImportRegistry(ServletConfig config, String user) throws IOException {
//		String path = config.getServletContext().getRealPath(PATH_REGISTRY_ROOT_SOURCE);
//		File root = new File(path);
//		logger.debug("root: " + root.getCanonicalPath().replace('\\', '/')); //$NON-NLS-1$
//		if (root.exists() && root.isDirectory()) {
//			File[] files = root.listFiles();
//			if (files != null) {
//				for (int i = 0; i < files.length; i++) {
//					File folder = files[i];
//					checkAndImportFileOrFolder(root.getCanonicalPath().length(), folder, user);
//				}
//			}
//		} else {
//			throw new IOException(CONTENT_INITIALIZATION_FAILED + path);
//		}
//	}
//
//	private void checkAndImportFileOrFolder(int rootLength, File fileOrFolder, String user)
//			throws IOException {
//		if (fileOrFolder.exists()) {
//			if (fileOrFolder.isDirectory()) {
//				String folderName = fileOrFolder.getCanonicalPath().substring(rootLength);
//				folderName = folderName.replace('\\', '/');
//				logger.debug(folderName + " source: " //$NON-NLS-1$
//						+ fileOrFolder.getCanonicalPath().replace('\\', '/'));
//				IRepository repository = getRepository(user);
//				ICollection collection = repository.getCollection(PATH_REGISTY_ROOT_TARGET
//						+ folderName);
//				if (!collection.exists()) {
//					collection.create();
//					logger.info("Folder created from: " //$NON-NLS-1$
//							+ fileOrFolder.getCanonicalPath().replace('\\', '/') + " to: " //$NON-NLS-1$
//							+ collection.getPath());
//				}
//				File[] files = fileOrFolder.listFiles();
//				if (files != null) {
//					for (int i = 0; i < files.length; i++) {
//						File folder = files[i];
//						checkAndImportFileOrFolder(rootLength, folder, user);
//					}
//				}
//			} else {
//				String fileName = fileOrFolder.getCanonicalPath().substring(rootLength);
//				fileName = fileName.replace('\\', '/');
//				logger.debug(fileName + " source: " //$NON-NLS-1$
//						+ fileOrFolder.getCanonicalPath());
//				IRepository repository = getRepository(user);
//				IResource resource = repository.getResource(PATH_REGISTY_ROOT_TARGET + fileName);
//				if (resource.exists()) {
//					resource.delete();
//				}
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				IOUtils.copy(new FileInputStream(fileOrFolder), baos);
//				String mimeType = null;
//				String extension = ContentTypeHelper.getExtension(fileName);
//				if ((mimeType = ContentTypeHelper.getContentType(extension)) != null) {
//					repository.createResource(PATH_REGISTY_ROOT_TARGET + fileName,
//							baos.toByteArray(), ContentTypeHelper.isBinary(mimeType), mimeType);
//				} else {
//					repository.createResource(PATH_REGISTY_ROOT_TARGET + fileName,
//							baos.toByteArray());
//				}
//				logger.info("Resource initialized from: " //$NON-NLS-1$
//						+ fileOrFolder.getCanonicalPath().replace('\\', '/')
//						+ " to: " + resource.getPath()); //$NON-NLS-1$
//				// logger.info(new String(baos.toByteArray()));
//			}
//		} else {
//			throw new IOException(INVALID_FILE_OR_FOLDER_LOCATION_DURING_CONTENT_INITIALIZATION
//					+ fileOrFolder.getCanonicalPath().replace('\\', '/'));
//		}
//
//	}

}
