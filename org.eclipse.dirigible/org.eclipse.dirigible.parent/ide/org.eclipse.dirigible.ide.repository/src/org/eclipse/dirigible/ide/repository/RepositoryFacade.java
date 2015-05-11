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

package org.eclipse.dirigible.ide.repository;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;


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

		HttpServletRequest request = CommonParameters.getRequest();

		IRepository repository = getRepositoryInstance(request);

		if (repository != null) {
			return repository;
		}

		try {
			DataSource dataSource = lookupDataSource();
			String user = getUser(request);
//			repository = new DBRepository(dataSource, user, false);
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

	public DataSource lookupDataSource() throws NamingException {
		return DataSourceFacade.getInstance().getDataSource();
	}

	public String getUser(HttpServletRequest request) {
		String user = CommonParameters.getUserName(); // shared one //$NON-NLS-1$
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
//		if (request == null) {
//			return null;
//		}
		return (IRepository) CommonParameters.getObject(REPOSITORY);
//		return (IRepository) request.getSession().getAttribute(REPOSITORY);
	}

	public void saveRepositoryInstance(HttpServletRequest request,
			IRepository repository) {
		if (request == null) {
			return;
		}
		CommonParameters.setObject(REPOSITORY, repository);
//		request.getSession().setAttribute(REPOSITORY, repository);
	}

}
