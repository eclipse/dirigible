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

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;

public class RepositoryFacade {

	private static final String REPOSITORY = "repository-instance"; //$NON-NLS-1$

	private static RepositoryFacade instance;

	private org.eclipse.dirigible.runtime.repository.RepositoryFacade runtimeFacade;

	private RepositoryFacade(org.eclipse.dirigible.runtime.repository.RepositoryFacade runtimeFacade) {
		this.runtimeFacade = runtimeFacade;
	}

	public static RepositoryFacade getInstance() {
		if (instance == null) {
			org.eclipse.dirigible.runtime.repository.RepositoryFacade runtimeFacade = org.eclipse.dirigible.runtime.repository.RepositoryFacade
					.getInstance();
			instance = new RepositoryFacade(runtimeFacade);
		}
		return instance;
	}

	public IRepository getRepository() throws RepositoryException {
		HttpServletRequest request = CommonIDEParameters.getRequest();
		return getRepository(request);
	}

	public IRepository getRepository(HttpServletRequest request) throws RepositoryException {
		return runtimeFacade.getRepository(request);
	}

	public DataSource lookupDataSource(HttpServletRequest request) throws NamingException {
		return DataSourceFacade.getInstance().getDataSource(request);
	}

}
