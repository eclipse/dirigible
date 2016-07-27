/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.repository;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;

public class RepositoryFacade {

	private static final String REPOSITORY = "repository-instance"; //$NON-NLS-1$

	private static RepositoryFacade instance;

	public static RepositoryFacade getInstance() {
		if (instance == null) {
			instance = new RepositoryFacade();
		}
		return instance;
	}

	public IRepository getRepository() throws RepositoryException {
		HttpServletRequest request = CommonIDEParameters.getRequest();
		return getRepository(request);
	}

	public IRepository getRepository(HttpServletRequest request) throws RepositoryException {
		if (request == null) {
			request = CommonIDEParameters.getRequest();
		}

		IRepository repository = getRepositoryInstance(request);

		if (repository != null) {
			return repository;
		}

		try {
			DataSource dataSource = lookupDataSource(request);
			String user = getUser(request);
			// repository = new DBRepository(dataSource, user, false);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("datasource", dataSource);
			parameters.put("user", user);
			parameters.put("recreate", Boolean.FALSE);
			repository = RepositoryFactory.createRepository(parameters);
			saveRepositoryInstance(request, repository);
		} catch (Exception e) {
			throw new RepositoryException(e);
		}

		return repository;
	}

	public DataSource lookupDataSource(HttpServletRequest request) throws NamingException {
		return DataSourceFacade.getInstance().getDataSource(request);
	}

	public String getUser(HttpServletRequest request) {
		String user = CommonIDEParameters.getUserName(request); // shared one
		try {
			if (request != null) {
				user = RequestUtils.getUser(request);
			}
		} catch (Exception e) {
			// TODO - do nothing
		}
		return user;
	}

	private IRepository getRepositoryInstance(HttpServletRequest request) {
		// if (request == null) {
		// return null;
		// }
		return (IRepository) CommonIDEParameters.getObject(REPOSITORY);
		// return (IRepository) request.getSession().getAttribute(REPOSITORY);
	}

	public void saveRepositoryInstance(HttpServletRequest request, IRepository repository) {
		if (request == null) {
			return;
		}
		CommonIDEParameters.setObject(REPOSITORY, repository);
		// request.getSession().setAttribute(REPOSITORY, repository);
	}

}
