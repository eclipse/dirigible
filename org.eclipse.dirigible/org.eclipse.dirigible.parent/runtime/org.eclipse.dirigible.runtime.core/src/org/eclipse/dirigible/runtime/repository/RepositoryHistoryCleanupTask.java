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

import java.io.IOException;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.task.IRunnableTask;

public class RepositoryHistoryCleanupTask implements IRunnableTask {

	private static final Logger logger = Logger.getLogger(RepositoryHistoryCleanupTask.class);

	@Override
	public String getName() {
		return "Repository History Cleanup Task";
	}

	@Override
	public void start() {
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "start()"); //$NON-NLS-1$
		try {
			cleanupOldHistory();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "start()"); //$NON-NLS-1$
	}

	private void cleanupOldHistory() throws IOException {
		RepositoryFacade.getInstance().getRepository(null).cleanupOldVersions();
	}

}
