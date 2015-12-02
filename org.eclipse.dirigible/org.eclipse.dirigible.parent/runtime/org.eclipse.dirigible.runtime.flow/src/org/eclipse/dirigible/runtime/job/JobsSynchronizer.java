/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import java.util.List;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class JobsSynchronizer implements Runnable {

	private static final Logger logger = Logger.getLogger(JobsSynchronizer.class);

	@Override
	public void run() {

		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$

		try {
			refreshJobs();
			logger.debug("Refresh of jobs locations successful for: " //$NON-NLS-1$
					+ JobsUpdater.activeJobs.size());
		} catch (Exception e) {
			logger.error("Refreshing Jobs failed.", e);
		}

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$
	}

	public static List<String> getActiveJobs() {
		return JobsUpdater.activeJobs;
	}

	private void refreshJobs() throws ServletException {

		try {
			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			IRepository repository = RepositoryFacade.getInstance().getRepository(null);
			// TODO
			JobsUpdater jobsUpdater = new JobsUpdater(repository, dataSource, JobsUpdater.REGISTRY_INTEGRATION_DEFAULT);
			jobsUpdater.applyUpdates();
			jobsUpdater.cleanDeletedJobs();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
