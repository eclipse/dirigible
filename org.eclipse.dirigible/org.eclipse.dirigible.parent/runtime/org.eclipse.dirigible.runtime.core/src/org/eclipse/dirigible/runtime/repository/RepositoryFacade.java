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

package org.eclipse.dirigible.runtime.repository;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;
//import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.ext.db.WrappedDataSource;
import org.eclipse.dirigible.repository.logging.Logger;

public class RepositoryFacade {

	private static final Logger logger = Logger.getLogger(RepositoryFacade.class);

//	private static final String JAVA_COMP_ENV_JDBC_DEFAULT_DB = "java:comp/env/jdbc/DefaultDB"; //$NON-NLS-1$

	private static final String LOCAL_DB_ACTION = "create"; //$NON-NLS-1$

	private static final String LOCAL_DB_NAME = "derby"; //$NON-NLS-1$

	private static final String REPOSITORY = "repository-instance"; //$NON-NLS-1$

	private static RepositoryFacade instance;

	private static DataSource localDataSource;

	private WrappedDataSource dataSource;
	
	public static final String GUEST = ICommonConstants.GUEST;
	
	public static final String INITIAL_CONTEXT = ICommonConstants.INITIAL_CONTEXT;

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
			DataSource dataSource = getDataSource(request);
			String user = getUser(request);
//			repository = new DBRepository(dataSource, user, false);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("datasource", dataSource);
			parameters.put("user", user);
			parameters.put("recreate", Boolean.FALSE);
			repository = RepositoryFactory.createRepository(parameters);
			saveRepositoryInstance(request, repository);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RepositoryException(e);
		}

		return repository;
	}
	
	public DataSource getDataSource() {
		return getDataSource(null);
	}

	public DataSource getDataSource(HttpServletRequest request) {
		if (dataSource == null) {
			logger.debug("Lookup Datasource...");
			if (request == null) {
				logger.debug("No request - try from Env...");
				dataSource = (WrappedDataSource) getFromEnv();
			} else {
				logger.debug("Request exists - try from Request...");
				dataSource = (WrappedDataSource) getFromSession(request);
			} 
			
			if (dataSource == null) {
				logger.debug("Try from Context...");
				dataSource = (WrappedDataSource) getFromContext();
			}
			
			if (dataSource == null) {
				dataSource = createLocal();
				logger.warn("Created Local DataSource!");
			} else {
				logger.debug("Lookup done.");
			}
		}
		return dataSource;
	}
	
	private static final String DATASOURCE_DEFAULT 				= "DEFAULT_DATASOURCE"; //$NON-NLS-1$
	private static final String JNDI_DEFAULT_DATASOURCE 		= "jndiDefaultDataSource"; //$NON-NLS-1$
	
	private WrappedDataSource getFromSession(HttpServletRequest request) {
		logger.debug("Try to get datasource from the Request");
		
		DataSource dataSource = null;
		dataSource = (DataSource) request.getSession().getAttribute(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
			logger.debug("Datasource retrieved from the Request");
			return wrappedDataSource;
		} else {
			logger.debug("Datasource NOT available in the Request");
		}
		return null;
	}
	
	private WrappedDataSource getFromEnv() {
		logger.debug("Try to get datasource from System Properties");
		
		DataSource dataSource = null;
		dataSource = (DataSource) System.getProperties().get(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
			logger.debug("Datasource retrieved from System Properties");
			return wrappedDataSource;
		} else {
			logger.debug("Datasource NOT available in System Properties");
		}
		return null;
	}
	

	private DataSource getFromContext() {
		logger.debug("Try to get datasource from the InitialContext");
		
		try {
			InitialContext context = (InitialContext) System.getProperties().get(INITIAL_CONTEXT);
			String jndiName = System.getProperty(JNDI_DEFAULT_DATASOURCE);
			DataSource datasource = (DataSource) context.lookup(jndiName);
			if (datasource == null) {
				logger.error("Could not find DataSource in Initial Context by name: " + jndiName);
			} else {
				WrappedDataSource wrappedDataSource = new WrappedDataSource(datasource);
				logger.debug("Datasource retrieved from InitialContext");
				return wrappedDataSource;
			}
		} catch (Throwable e) {
			logger.error("Could not find DataSource", e);
		}
		
		return null;
	}

	private WrappedDataSource createLocal() {
		
		logger.debug("Try to create embedded datasource");
		
		localDataSource = (DataSource) System.getProperties().get(LOCAL_DB_NAME);
		if (localDataSource == null) {
			localDataSource = new EmbeddedDataSource();
			((EmbeddedDataSource) localDataSource).setDatabaseName(LOCAL_DB_NAME);
			((EmbeddedDataSource) localDataSource).setCreateDatabase(LOCAL_DB_ACTION);
			System.getProperties().put(LOCAL_DB_NAME, localDataSource);
		}
		logger.error("Embedded DataSource is used! In case you intentionally use local datasource, ignore this error.");

		WrappedDataSource wrappedDataSource = new WrappedDataSource(localDataSource);
		return wrappedDataSource;
	}

	public static String getUser(HttpServletRequest request) {
		String user = GUEST; // shared one
		try {
			if ((request != null) && (request.getUserPrincipal() != null)) {
				user = request.getUserPrincipal().getName();
			}
		} catch (Exception e) {
			// TODO - do nothing
		}
		return user;
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

	public void saveRepositoryInstance(HttpServletRequest request, IRepository repository) {
		if (request == null) {
			return;
		}
		try {
			request.getSession().setAttribute(REPOSITORY, repository);
		} catch (Exception e) {
			repository.dispose();
		}
	}

}
