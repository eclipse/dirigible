/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.repository;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
// import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.datasource.WrappedDataSource;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class RepositoryFacade {

	private static final String PARAM_RECREATE = "recreate"; //$NON-NLS-1$
	private static final String PARAM_USER = "user"; //$NON-NLS-1$
	private static final String PARAM_DATASOURCE = "datasource"; //$NON-NLS-1$
	private static final String PARAM_GIT_TARGET_FOLDER = "masterRepositoryGitTarget"; //$NON-NLS-1$
	private static final String PARAM_GIT_LOCATION = "masterRepositoryGitLocation"; //$NON-NLS-1$
	private static final String PARAM_GIT_USER = "masterRepositoryGitUser"; //$NON-NLS-1$
	private static final String PARAM_GIT_PASSWORD = "masterRepositoryGitPassword"; //$NON-NLS-1$
	private static final String PARAM_GIT_BRANCH = "masterRepositoryGitBranch"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(RepositoryFacade.class);

	// private static final String JAVA_COMP_ENV_JDBC_DEFAULT_DB = "java:comp/env/jdbc/DefaultDB"; //$NON-NLS-1$

	private static final String LOCAL_DB_ACTION = "create"; //$NON-NLS-1$

	private static final String LOCAL_DB_NAME = "derby"; //$NON-NLS-1$

	public static final String REPOSITORY = "repository-instance"; //$NON-NLS-1$

	private static final String MASTER_REPOSITORY = "master-repository-instance"; //$NON-NLS-1$

	private static RepositoryFacade instance;

	private static DataSource localDataSource;

	private WrappedDataSource dataSource;

	private RepositoryFacade() {

	}

	public static RepositoryFacade getInstance() {
		if (instance == null) {
			instance = new RepositoryFacade();
		}
		return instance;
	}

	public IRepository getRepository(HttpServletRequest request) throws RepositoryException {

		IRepository repository = getRepositoryInstance(request);

		if (repository != null) {
			return repository;
		}

		try {
			DataSource defaultDataSource = DataSourceFacade.getInstance().getDataSource(request);
			String user = getUser(request);
			// repository = new DBRepository(dataSource, user, false);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(PARAM_DATASOURCE, defaultDataSource);
			parameters.put(PARAM_USER, user);
			parameters.put(PARAM_RECREATE, Boolean.FALSE);
			parameters.put(PARAM_GIT_TARGET_FOLDER, System.getProperty(PARAM_GIT_TARGET_FOLDER));
			parameters.put(PARAM_GIT_LOCATION, System.getProperty(PARAM_GIT_LOCATION));
			parameters.put(PARAM_GIT_USER, System.getProperty(PARAM_GIT_USER));
			parameters.put(PARAM_GIT_PASSWORD, System.getProperty(PARAM_GIT_PASSWORD));
			parameters.put(PARAM_GIT_BRANCH, System.getProperty(PARAM_GIT_BRANCH));
			repository = RepositoryFactory.createRepository(parameters);
			saveRepositoryInstance(request, repository);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RepositoryException(e);
		}

		return repository;
	}

	public IMasterRepository getMasterRepository(HttpServletRequest request) throws RepositoryException {

		IMasterRepository repository = getMasterRepositoryInstance(request);

		if (repository != null) {
			return repository;
		}

		try {
			DataSource defaultDataSource = DataSourceFacade.getInstance().getDataSource(request);
			String user = getUser(request);
			// repository = new DBRepository(dataSource, user, false);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(PARAM_DATASOURCE, defaultDataSource);
			parameters.put(PARAM_USER, user);
			parameters.put(PARAM_RECREATE, Boolean.FALSE);

			repository = RepositoryFactory.createMasterRepository(parameters);
			if (repository == null) {
				return null;
			}
			saveMasterRepositoryInstance(request, repository);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RepositoryException(e);
		}

		return repository;
	}

	public static String getUser(HttpServletRequest request) {
		return RequestUtils.getUser(request);
	}

	private IRepository getRepositoryInstance(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		try {
			return (IRepository) request.getSession().getAttribute(REPOSITORY);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private IMasterRepository getMasterRepositoryInstance(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		try {
			return (IMasterRepository) request.getSession().getAttribute(MASTER_REPOSITORY);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private void saveRepositoryInstance(HttpServletRequest request, IRepository repository) {
		if (request == null) {
			return;
		}
		request.getSession().setAttribute(REPOSITORY, repository);
	}

	private void saveMasterRepositoryInstance(HttpServletRequest request, IMasterRepository repository) {
		if (request == null) {
			return;
		}
		request.getSession().setAttribute(MASTER_REPOSITORY, repository);
	}

}
