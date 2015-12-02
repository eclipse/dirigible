/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.SecurityManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class SecuritySynchronizer implements Runnable {

	private static final String REFRESHING_OF_SECURED_LOCATIONS_FAILED = Messages.SecuritySynchronizer_REFRESHING_OF_SECURED_LOCATIONS_FAILED;

	private static final Logger logger = Logger.getLogger(SecuritySynchronizer.class);

	private static List<String> securedLocations = Collections.synchronizedList(new ArrayList<String>());

	@Override
	public void run() {

		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$

		try {
			refreshSecuredLocations();
			logger.debug("Refresh of secured locations successful for: " //$NON-NLS-1$
					+ securedLocations.size());
		} catch (Exception e) {
			logger.error(REFRESHING_OF_SECURED_LOCATIONS_FAILED, e);
		}

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$
	}

	public static List<String> getSecuredLocations() {
		return securedLocations;
	}

	private void refreshSecuredLocations() throws ServletException {

		SecurityManager securityManager = null;
		try {
			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			IRepository repository = RepositoryFacade.getInstance().getRepository(null);
			// TODO
			securityManager = SecurityManager.getInstance(repository, dataSource);
			securedLocations = securityManager.getSecuredLocations();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
