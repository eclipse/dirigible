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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.db.DatabaseUpdater;
import org.eclipse.dirigible.repository.ext.db.DsvUpdater;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionUpdater;
import org.eclipse.dirigible.repository.ext.security.SecurityUpdater;
import org.eclipse.dirigible.runtime.job.JobsUpdater;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class ContentPostImportUpdater {

	private IRepository repository;

	public ContentPostImportUpdater(IRepository repository) {
		this.repository = repository;
	}

	public IRepository getRepository() {
		return repository;
	}

	public void update(HttpServletRequest request) throws IOException, Exception {
		// 1. Execute the real database "create or update"
		DatabaseUpdater databaseUpdater = new DatabaseUpdater(getRepository(), RepositoryFacade
				.getInstance().getDataSource(request), DatabaseUpdater.REGISTRY_DATA_STRUCTURES_DEFAULT);
		databaseUpdater.applyUpdates();

		// 2. Execute the real security "create or update"
		SecurityUpdater securityUpdater = new SecurityUpdater(getRepository(), RepositoryFacade
				.getInstance().getDataSource(request),
				SecurityUpdater.REGISTRY_SECURITY_CONSTRAINTS_DEFAULT);
		securityUpdater.applyUpdates();

		// 3. Execute the real import from DSV files
		DsvUpdater dsvUpdater = new DsvUpdater(getRepository(), RepositoryFacade.getInstance()
				.getDataSource(request), DatabaseUpdater.REGISTRY_DATA_STRUCTURES_DEFAULT);
		dsvUpdater.applyUpdates();

		// 4. Extensions
		ExtensionUpdater extensionUpdater = new ExtensionUpdater(getRepository(), RepositoryFacade
				.getInstance().getDataSource(request),
				ExtensionUpdater.REGISTRY_EXTENSION_DEFINITIONS_DEFAULT);
		extensionUpdater.applyUpdates();
		
		// 5. Jobs
		JobsUpdater jobsUpdater = new JobsUpdater(getRepository(), RepositoryFacade
				.getInstance().getDataSource(request),
				JobsUpdater.REGISTRY_INTEGRATION_DEFAULT);
		jobsUpdater.applyUpdates();


	}

}
