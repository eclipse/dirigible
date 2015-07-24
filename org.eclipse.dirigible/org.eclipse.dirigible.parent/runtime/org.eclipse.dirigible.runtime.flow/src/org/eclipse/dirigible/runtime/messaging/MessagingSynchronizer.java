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

package org.eclipse.dirigible.runtime.messaging;

import java.util.List;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.messaging.MessageHub;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MessagingSynchronizer implements Runnable {

	private static final Logger logger = Logger.getLogger(MessagingSynchronizer.class);

	@Override
	public void run() {

		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$
		try {
			routeMessages();
			logger.debug("Routing of Messages was successful"); //$NON-NLS-1$
		} catch (Exception e) {
			logger.error("Routing of Messages failed.", e);
		}

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$
	}

	private void routeMessages() throws ServletException {

		try {
			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			MessageHub messageHub = MessageHub.getInstance(dataSource);
			messageHub.route();
			messageHub.cleanup();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
